package com.example.lineta_posts_interaction.controller;

import com.example.lineta_posts_interaction.dto.request.CommentUserRequestDTO;
import com.example.lineta_posts_interaction.dto.request.ReplyUserRequestDTO;
import com.example.lineta_posts_interaction.dto.response.ApiResponse;
import com.example.lineta_posts_interaction.dto.response.CommentWithUserDTO;
import com.example.lineta_posts_interaction.dto.response.ReplyWithUserDTO;
import com.example.lineta_posts_interaction.entity.Comment;
import com.example.lineta_posts_interaction.entity.ReplyComment;
import com.example.lineta_posts_interaction.service.CommentService;
import com.example.lineta_posts_interaction.service.PostService;
import com.example.lineta_posts_interaction.service.ReplyCommentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final PostService postService;
    private final ReplyCommentService replyCommentService;

    public CommentController(CommentService commentService, PostService postService, ReplyCommentService replyCommentService) {
        this.commentService = commentService;
        this.postService = postService;
        this.replyCommentService = replyCommentService;
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @PostMapping("/createComment")
    public ResponseEntity<ApiResponse<Void>> addComment(@RequestBody CommentUserRequestDTO comment) throws ExecutionException, InterruptedException {
        try {
            WriteResult saved = commentService.saveComment(comment);

            // Gửi comment qua WebSocket
            messagingTemplate.convertAndSend("/topic/comments/" + comment.getPostID(), comment);

            // Tạo nội dung message Kafka
            Map<String, Object> message = new HashMap<>();
            message.put("senderUsername",comment.getUsername());
            message.put("type", "comment");
            message.put("cmtReceiver", postService.getUsernameFromPost(comment.getPostID()));
            message.put("isRead", false);
            message.put("postId", comment.getPostID());
            message.put("content", comment.getFullName() + " commented your post: "
            + shortenText(comment.getContent(),5));

            ObjectMapper mapper = new ObjectMapper();
            kafkaTemplate.send("post-notifications", mapper.writeValueAsString(message));

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .code(1000)
                    .message("Comment created at: " + saved.getUpdateTime())
                    .build());

        } catch (ExecutionException | InterruptedException | JsonProcessingException e) {
            return ResponseEntity.status(500).body(ApiResponse.<Void>builder()
                    .code(1001)
                    .message("Error: " + e.getMessage())
                    .build());
        }
    }


    @PostMapping("/reply")
    public ResponseEntity<ApiResponse<ReplyUserRequestDTO>> addReply(@RequestBody ReplyUserRequestDTO replyComment) throws ExecutionException, InterruptedException, JsonProcessingException {
        WriteResult saved = replyCommentService.saveReplyComment(replyComment);

        messagingTemplate.convertAndSend("/topic/reply/" + replyComment.getCommentId(), replyComment);
        // Tạo nội dung message Kafka
        Map<String, Object> message = new HashMap<>();
        message.put("senderUsername",replyComment.getUsername());
        message.put("type", "reply");
        message.put("cmtReceiver", postService.getUsernameFromComment(replyComment.getCommentId()));
        message.put("isRead", false);
        message.put("postId", null);
        message.put("content", replyComment.getFullName() + " responded your comment: "
                + shortenText(replyComment.getContent(),5));

        ObjectMapper mapper = new ObjectMapper();
        kafkaTemplate.send("post-notifications", mapper.writeValueAsString(message));

        return ResponseEntity.ok(ApiResponse.<ReplyUserRequestDTO>builder()
                .code(1000)
                .message("Reply comment created at: " + saved.getUpdateTime())
                .result(replyComment)
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
