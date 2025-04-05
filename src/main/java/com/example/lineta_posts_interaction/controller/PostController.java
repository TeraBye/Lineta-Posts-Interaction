package com.example.lineta_posts_interaction.controller;

import com.example.lineta_posts_interaction.service.PostService;
import com.google.cloud.firestore.WriteResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import com.example.lineta_posts_interaction.entity.Post;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

//    @PostMapping("/creat2e")
//    public ResponseEntity<String> createPost(@RequestParam String userId,
//                                             @RequestParam String content,
//                                             @RequestParam String imageUrl) {
//        try {
//            WriteResult result = postService.savePost(userId, content, imageUrl);
//            return ResponseEntity.ok("Post created at: " + result.getUpdateTime());
//        } catch (ExecutionException | InterruptedException e) {
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }

    @PostMapping("/create")
    public ResponseEntity<String> createPost(@RequestBody Post post) {
        try {
            WriteResult result = postService.savePost(post);
            return ResponseEntity.ok("Post created at: " + result.getUpdateTime());
        } catch (ExecutionException | InterruptedException e){
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }


    }
}
