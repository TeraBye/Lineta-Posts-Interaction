package com.example.lineta_posts_interaction.controller;

import com.example.lineta_posts_interaction.client.UserClient;
import com.example.lineta_posts_interaction.dto.response.CommentWithUserDTO;
import com.example.lineta_posts_interaction.dto.response.ReplyWithUserDTO;
import com.example.lineta_posts_interaction.dto.response.UserDTO;
import com.example.lineta_posts_interaction.entity.Comment;
import com.example.lineta_posts_interaction.entity.ReplyComment;
import com.example.lineta_posts_interaction.service.GetCommentService;
import com.example.lineta_posts_interaction.service.GetReplyCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
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

    @GetMapping("/comments")
    public ResponseEntity<?>  getComments(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(name = "postID") String postId,
            @RequestParam(defaultValue = "0") int page,  // Trang hiện tại, mặc định là 0
            @RequestParam(defaultValue = "5") int size  // Số bài viết mỗi lần, mặc định là 5
    ) throws ExecutionException, InterruptedException {
        List<Comment> comments = getCommentService.getComments(postId, page, size);
        Set<String> usernames = comments.stream()
                .map(Comment::getUsername)
                .collect(Collectors.toSet());

        // Gọi sang UserService và truyền token
        System.out.println(extractToken(authorizationHeader));
        Map<String, UserDTO> userMap = userClient.getUsersByUsernames(usernames, extractToken(authorizationHeader));

        List<CommentWithUserDTO> commentDTOs = comments.stream()
                .map(comment -> new CommentWithUserDTO(comment, userMap.get(comment.getUsername())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(commentDTOs);
    }

    // Nếu cần tách "Bearer <token>" thành token:
    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return header; // fallback
    }




    @GetMapping("/replyComments")
    public ResponseEntity<?>  getReplyComments(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(name = "commentID") String commentID,
            @RequestParam(defaultValue = "0") int page,  // Trang hiện tại, mặc định là 0
            @RequestParam(defaultValue = "5") int size  // Số bài viết mỗi lần, mặc định là 5
    ) throws ExecutionException, InterruptedException {
        List<ReplyComment> replyComments = getReplyCommentService.getReplyComments(commentID, page, size);
        Set<String> usernames = replyComments.stream()
                .map(ReplyComment::getUsername)
                .collect(Collectors.toSet());

        // Gọi sang UserService và truyền token
        Map<String, UserDTO> userMap = userClient.getUsersByUsernames(usernames, extractToken(authorizationHeader));

        List<ReplyWithUserDTO> replyDTOs = replyComments.stream()
                .map(replyComment -> new ReplyWithUserDTO(replyComment, userMap.get(replyComment.getUsername())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(replyDTOs);
    }
}
