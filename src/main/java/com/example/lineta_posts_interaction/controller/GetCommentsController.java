package com.example.lineta_posts_interaction.controller;

import com.example.lineta_posts_interaction.client.UserClient;
import com.example.lineta_posts_interaction.dto.response.ApiResponse;
import com.example.lineta_posts_interaction.dto.response.CommentWithUserDTO;
import com.example.lineta_posts_interaction.dto.response.ReplyWithUserDTO;
import com.example.lineta_posts_interaction.dto.response.UserDTO;
import com.example.lineta_posts_interaction.entity.Comment;
import com.example.lineta_posts_interaction.entity.ReplyComment;
import com.example.lineta_posts_interaction.service.GetCommentService;
import com.example.lineta_posts_interaction.service.GetReplyCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class GetCommentsController {

    private final GetCommentService getCommentService;
    private final GetReplyCommentService getReplyCommentService;
    private final UserClient userClient;

    @Autowired
    public GetCommentsController(GetCommentService getCommentService, GetReplyCommentService getReplyCommentService, UserClient userClient) {
        this.getCommentService = getCommentService;
        this.getReplyCommentService = getReplyCommentService;
        this.userClient = userClient;
    }

    @GetMapping("/getComments")
    public ResponseEntity<ApiResponse<List<CommentWithUserDTO>>> getComments(
            @RequestParam(name = "postID") String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) throws ExecutionException, InterruptedException {

        List<Comment> comments = getCommentService.getComments(postId, page, size);

        Set<String> usernames = comments.stream()
                .map(Comment::getUsername)
                .collect(Collectors.toSet());

        Map<String, UserDTO> userMap = userClient.getUsersByUsernames(usernames);

        List<CommentWithUserDTO> commentDTOs = comments.stream()
                .map(comment -> new CommentWithUserDTO(comment, userMap.get(comment.getUsername())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<CommentWithUserDTO>>builder()
                .code(1000)
                .message("Fetched comments successfully")
                .result(commentDTOs)
                .build());
    }


    // Nếu cần tách "Bearer <token>" thành token:
    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return header; // fallback
    }

    @GetMapping("/getReply")
    public ResponseEntity<ApiResponse<List<ReplyWithUserDTO>>> getReplyComments(
            @RequestParam(name = "commentID") String commentID,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) throws ExecutionException, InterruptedException {

        List<ReplyComment> replyComments = getReplyCommentService.getReplyComments(commentID, page, size);

        Set<String> usernames = replyComments.stream()
                .map(ReplyComment::getUsername)
                .collect(Collectors.toSet());

        Map<String, UserDTO> userMap = userClient.getUsersByUsernames(usernames);

        List<ReplyWithUserDTO> replyDTOs = replyComments.stream()
                .map(reply -> new ReplyWithUserDTO(reply, userMap.get(reply.getUsername())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<ReplyWithUserDTO>>builder()
                .code(1000)
                .message("Fetched reply comments successfully")
                .result(replyDTOs)
                .build());
    }

}
