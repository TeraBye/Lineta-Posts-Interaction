package com.example.lineta_posts_interaction.controller;

import com.example.lineta_posts_interaction.dto.request.LikeUserRequestDTO;
import com.example.lineta_posts_interaction.dto.response.ApiResponse;
import com.example.lineta_posts_interaction.dto.response.LikeWithUserDTO;
import com.example.lineta_posts_interaction.entity.Like;
import com.example.lineta_posts_interaction.service.LikeService;
import com.example.lineta_posts_interaction.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/likes")
public class LikeController {
    private final LikeService likeService;
    private final PostService postService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public LikeController(LikeService likeService, PostService postService) {
        this.likeService = likeService;
        this.postService = postService;
    }

    @PostMapping("/createLike")
    public ResponseEntity<ApiResponse<LikeUserRequestDTO>> addLike(@RequestBody LikeUserRequestDTO like) throws ExecutionException, InterruptedException {
        WriteResult saved = likeService.saveLike(like);
        return ResponseEntity.ok(ApiResponse.<LikeUserRequestDTO>builder()
                .code(1000)
                .message("Like added successfully")
                .result(like)
                .build());
    }


    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkIfLiked(
            @RequestParam String username,
            @RequestParam String postID
    ) throws ExecutionException, InterruptedException {
        boolean isLiked = likeService.isPostLikedByUser(username, postID);
        return ResponseEntity.ok(ApiResponse.<Map<String, Boolean>>builder()
                .code(1000)
                .message("Check like status successful")
                .result(Map.of("liked", isLiked))
                .build());
    }


    @PostMapping("/likes")
    public ResponseEntity<ApiResponse<Void>> toggleLike(@RequestBody LikeUserRequestDTO like) throws Exception {
        boolean alreadyLiked = likeService.isPostLikedByUser(like.getUsername(), like.getPostID());

        if (alreadyLiked) {
            likeService.deleteLike(like.getUsername(), like.getPostID());
            postService.incrementLike(like.getPostID(), -1);
        } else {
            likeService.saveLike(like);
            postService.incrementLike(like.getPostID(), 1);

            // Gửi thông báo qua Kafka
            Map<String, Object> message = new HashMap<>();
            message.put("senderUsername", like.getUsername());
            message.put("cmtReceiver",postService.getUsernameFromPost(like.getPostID()));
            message.put("type", "like");
            message.put("isRead", false);
            message.put("postId", like.getPostID());
            message.put("content", like.getFullName() + " đã thích một bài viết của bạn: "
                    +shortenText(like.getTempContent(),5));

            ObjectMapper mapper = new ObjectMapper();
            kafkaTemplate.send("post-notifications", mapper.writeValueAsString(message));

        }
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(1000)
                .message(alreadyLiked ? "Like removed" : "Like added")
                .build());
    }
    public static String shortenText(String text, int wordLimit) {
        String[] words = text.split("\\s+");
        if (words.length <= wordLimit) {
            return text;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordLimit; i++) {
            sb.append(words[i]).append(" ");
        }

        sb.append("...");
        return sb.toString().trim();
    }




}
