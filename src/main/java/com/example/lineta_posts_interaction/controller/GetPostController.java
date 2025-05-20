package com.example.lineta_posts_interaction.controller;

import com.example.lineta_posts_interaction.client.UserClient;
import com.example.lineta_posts_interaction.dto.response.ApiResponse;
import com.example.lineta_posts_interaction.dto.response.UserDTO;
import com.example.lineta_posts_interaction.dto.response.PostWithUserDTO;
import com.example.lineta_posts_interaction.entity.Post;
import com.example.lineta_posts_interaction.service.PostUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class GetPostController {

    private final PostUpService postUpService;
    private final UserClient userClient;

    @Autowired
    public GetPostController(PostUpService postUpService, UserClient userClient) {
        this.postUpService = postUpService;
        this.userClient = userClient;
    }

    @GetMapping("/getPosts")
    public ResponseEntity<ApiResponse<List<PostWithUserDTO>>> getPostsByUsername(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) throws ExecutionException, InterruptedException {

        List<Post> posts = postUpService.getPosts(page, size);

        Set<String> usernames = posts.stream()
                .map(Post::getUsername)
                .collect(Collectors.toSet());

        // Gọi sang UserService và truyền token

        Map<String, UserDTO> userMap = userClient.getUsersByUsernames(usernames);

        List<PostWithUserDTO> postDTOs = posts.stream()
                .map(post -> new PostWithUserDTO(post, userMap.get(post.getUsername())))
                .collect(Collectors.toList());
        System.out.println(postDTOs);
        return ResponseEntity.ok(ApiResponse.<List<PostWithUserDTO>>builder()
                .code(1000)
                .message("Fetched posts successfully")
                .result(postDTOs)
                .build());
    }


    // Nếu cần tách "Bearer <token>" thành token:
    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return header; // fallback
    }

}
