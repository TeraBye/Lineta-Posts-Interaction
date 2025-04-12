package com.example.lineta_posts_interaction.controller;

import com.example.lineta_posts_interaction.entity.Comment;
import com.example.lineta_posts_interaction.entity.Post;
import com.example.lineta_posts_interaction.entity.ReplyComment;
import com.example.lineta_posts_interaction.service.GetCommentService;
import com.example.lineta_posts_interaction.service.GetReplyCommentService;
import com.example.lineta_posts_interaction.service.PostUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class GetCommentsController {

    private final GetCommentService getCommentService;
    private final GetReplyCommentService getReplyCommentService;


    @Autowired
    public GetCommentsController(GetCommentService getCommentService, GetReplyCommentService getReplyCommentService) {
        this.getCommentService = getCommentService;
        this.getReplyCommentService = getReplyCommentService;
    }

    @GetMapping("/comments")
    public List<Comment> getComments(
            @RequestParam(name = "postID") String postId,
            @RequestParam(defaultValue = "0") int page,  // Trang hiện tại, mặc định là 0
            @RequestParam(defaultValue = "5") int size  // Số bài viết mỗi lần, mặc định là 5
    ) throws ExecutionException, InterruptedException {
        return getCommentService.getComments(postId, page, size);
    }

    @GetMapping("/replyComments")
    public List<ReplyComment> getReplyComments(
            @RequestParam(name = "commentID") String commentID,
            @RequestParam(defaultValue = "0") int page,  // Trang hiện tại, mặc định là 0
            @RequestParam(defaultValue = "5") int size  // Số bài viết mỗi lần, mặc định là 5
    ) throws ExecutionException, InterruptedException {
        return getReplyCommentService.getReplyComments(commentID, page, size);
    }
}
