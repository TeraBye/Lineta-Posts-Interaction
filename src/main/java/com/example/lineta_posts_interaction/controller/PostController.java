package com.example.lineta_posts_interaction.controller;

import com.example.lineta_posts_interaction.client.UserClient;
import com.example.lineta_posts_interaction.dto.request.PostUserRequestDTO;
import com.example.lineta_posts_interaction.dto.response.ApiResponse;
import com.example.lineta_posts_interaction.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private final PostService postService;
    @Autowired
    private RestTemplate restTemplate;
    private final UserClient userClient;


    public PostController(PostService postService, UserClient userClient) {
        this.postService = postService;
        this.userClient = userClient;
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/createPost")
    public ResponseEntity<ApiResponse<Void>> createPost(@RequestBody PostUserRequestDTO post) {
        try {
            WriteResult result = postService.savePost(post);

            // Gửi WebSocket post mới
            messagingTemplate.convertAndSend("/topic/posts", post);

            // Gửi thông báo qua Kafka
            Map<String, Object> message = new HashMap<>();
            message.put("senderUsername", post.getUsername());
            message.put("type", "post");
            message.put("isRead", false);
            message.put("postId", post.getPostId());
            message.put("content", post.getFullName() + " posted a new post: "
            +shortenText(post.getContent(),5));
            message.put("sender-uid",post.getUid());

            ObjectMapper mapper = new ObjectMapper();
            kafkaTemplate.send("post-notifications", mapper.writeValueAsString(message));

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .code(1000)
                    .message("Post created at: " + result.getUpdateTime())
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Void>builder()
                    .code(1001)
                    .message("Error: " + e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/deletePost/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable String postId) {
        try {
            postService.deletePostWithDependencies(postId); // Gọi hàm đã tối ưu ở trên

            // Gửi thông báo realtime (WebSocket)
            messagingTemplate.convertAndSend("/topic/posts/delete", postId);

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .code(1000)
                    .message("Post deleted successfully: " + postId)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .code(1001)
                            .message("Error deleting post: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/updatePost/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePostContent(@PathVariable String postId, @RequestBody Map<String, String> body) {
        try {
            String content = body.get("content");

            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                        .code(1002)
                        .message("Content must not be empty")
                        .build());
            }

            postService.updatePostContent(postId, content);


            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .code(1000)
                    .message("Post updated successfully: " + postId)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .code(1001)
                            .message("Error updating post: " + e.getMessage())
                            .build());
        }
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
