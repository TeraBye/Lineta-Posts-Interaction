package com.example.lineta_posts_interaction.controller;

import com.example.lineta_posts_interaction.dto.request.CommentUserRequestDTO;
import com.example.lineta_posts_interaction.dto.response.CommentWithUserDTO;
import com.example.lineta_posts_interaction.entity.Comment;
import com.example.lineta_posts_interaction.entity.ReplyComment;
import com.example.lineta_posts_interaction.service.CommentService;
import com.example.lineta_posts_interaction.service.PostService;
import com.example.lineta_posts_interaction.service.ReplyCommentService;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.example.lineta_posts_interaction.entity.Post;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private final CommentService commentService;
    private final ReplyCommentService replyCommentService;

    public CommentController(CommentService commentService, ReplyCommentService replyCommentService) {
        this.commentService = commentService;
        this.replyCommentService = replyCommentService;
    }


    @PostMapping("/create")
    public ResponseEntity<String> addComment(@RequestBody CommentUserRequestDTO comment) throws ExecutionException, InterruptedException {
     try{
        WriteResult saved = commentService.saveComment(comment);
        messagingTemplate.convertAndSend("/topic/comments", comment);
         return ResponseEntity.ok("Comment created at: " + saved.getUpdateTime());
    } catch (ExecutionException | InterruptedException e){
        return ResponseEntity.status(500).body("Error: " + e.getMessage());
    }
    }

    @PostMapping("/reply")
    public ResponseEntity<ReplyComment> addReply(@RequestBody ReplyComment replyComment) throws ExecutionException, InterruptedException {
        WriteResult saved = replyCommentService.saveReplyComment(replyComment);
        return ResponseEntity.ok(replyComment);
    }
}
