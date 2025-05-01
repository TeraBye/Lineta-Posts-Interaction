package com.example.lineta_posts_interaction.controller;

import com.example.lineta_posts_interaction.dto.request.PostUserRequestDTO;
import com.example.lineta_posts_interaction.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
        import com.example.lineta_posts_interaction.entity.Post;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private final PostService postService;
    @Autowired
    private RestTemplate restTemplate;


    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/create")
    public ResponseEntity<String> createPost(@RequestBody PostUserRequestDTO post) {
        try {
            WriteResult result = postService.savePost(post);

            // WebSocket gửi bài post mới
            messagingTemplate.convertAndSend("/topic/posts", post);

            // Tạo nội dung message Kafka
            Map<String, Object> message = new HashMap<>();
            message.put("isRead", false);
            message.put("postId", post.getPostId());
            message.put("content", post.getFullName() + " vừa đăng một bài viết");

            ObjectMapper mapper = new ObjectMapper();
            kafkaTemplate.send("post-notifications", mapper.writeValueAsString(message));

            return ResponseEntity.ok("Post created at: " + result.getUpdateTime());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

}
