package com.example.lineta_posts_interaction.controller;

import com.example.lineta_posts_interaction.dto.request.CommentLikeUserRequestDTO;
import com.example.lineta_posts_interaction.dto.request.LikeUserRequestDTO;
import com.example.lineta_posts_interaction.dto.response.ApiResponse;
import com.example.lineta_posts_interaction.service.CommentLikeService;
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
@RequestMapping("/api/likes/cmtLike/")
public class CommentLikeController {
    private final CommentLikeService likeService;
    private final PostService postService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public CommentLikeController(CommentLikeService likeService, PostService postService) {
        this.likeService = likeService;
        this.postService = postService;
    }

    @PostMapping("/createLike")
    public ResponseEntity<ApiResponse<CommentLikeUserRequestDTO>> addLike(@RequestBody CommentLikeUserRequestDTO like) throws ExecutionException, InterruptedException {
        WriteResult saved = likeService.saveLike(like);
        return ResponseEntity.ok(ApiResponse.<CommentLikeUserRequestDTO>builder()
                .code(1000)
                .message("Like added successfully")
                .result(like)
                .build());
    }


    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkIfLiked(
            @RequestParam String username,
            @RequestParam String commentId
    ) throws ExecutionException, InterruptedException {
        boolean isLiked = likeService.isCommentLikedByUser(username, commentId);
        return ResponseEntity.ok(ApiResponse.<Map<String, Boolean>>builder()
                .code(1000)
                .message("Check like status successful")
                .result(Map.of("liked", isLiked))
                .build());
    }


    @PostMapping("/likes")
    public ResponseEntity<ApiResponse<Void>> toggleLike(@RequestBody CommentLikeUserRequestDTO like) throws Exception {
        boolean alreadyLiked = likeService.isCommentLikedByUser(like.getUsername(), like.getCommentId());

        if (alreadyLiked) {
            likeService.deleteLike(like.getUsername(), like.getCommentId());
            postService.incrementCommentLike(like.getCommentId(), -1);
        } else {
            likeService.saveLike(like);
            postService.incrementCommentLike(like.getCommentId(), 1);

            // Gửi thông báo qua Kafka
            Map<String, Object> message = new HashMap<>();
            message.put("senderUsername", like.getUsername());
            message.put("cmtReceiver",postService.getUsernameFromComment(like.getCommentId()));
            message.put("type", "like");
            message.put("isRead", false);
            message.put("postId", null);
            message.put("content", like.getFullName() + " liked your comment: "
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
