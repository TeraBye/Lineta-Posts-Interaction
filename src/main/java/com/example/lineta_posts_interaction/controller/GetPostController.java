package com.example.lineta_posts_interaction.controller;

import com.example.lineta_posts_interaction.entity.Post;
import com.example.lineta_posts_interaction.service.PostUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class GetPostController {

    private final PostUpService postUpService;

    @Autowired
    public GetPostController(PostUpService postUpService) {
        this.postUpService = postUpService;
    }

    @GetMapping("/posts")
    public List<Post> getPostsByUsername(
            @RequestParam(defaultValue = "0") int page,  // Trang hiện tại, mặc định là 0
            @RequestParam(defaultValue = "5") int size  // Số bài viết mỗi lần, mặc định là 5
    ) throws ExecutionException, InterruptedException {
        return postUpService.getPosts(page, size);
    }
}
