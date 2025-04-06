package com.example.lineta_posts_interaction.controller;

import com.example.lineta_posts_interaction.entity.Comment;
import com.example.lineta_posts_interaction.service.CommentService;
import com.example.lineta_posts_interaction.service.PostService;
import com.google.cloud.firestore.WriteResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.lineta_posts_interaction.entity.Post;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

//    @PostMapping("/create")
//    public ResponseEntity<String> createPost(
//            @RequestBody Comment comment) {
//        try {
//            WriteResult result = commentService.saveComment(comment);
//            return ResponseEntity.ok(comment);;
//        } catch (ExecutionException | InterruptedException e){
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//
//
//    }
    @PostMapping("/create")
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment) throws ExecutionException, InterruptedException {
        WriteResult saved = commentService.saveComment(comment);
        return ResponseEntity.ok(comment);
    }
}
