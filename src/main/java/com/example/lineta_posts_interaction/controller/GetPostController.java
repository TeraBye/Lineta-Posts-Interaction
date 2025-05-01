package com.example.lineta_posts_interaction.controller;

import com.example.lineta_posts_interaction.client.UserClient;
import com.example.lineta_posts_interaction.dto.response.UserDTO;
import com.example.lineta_posts_interaction.dto.response.PostWithUserDTO;
import com.example.lineta_posts_interaction.entity.Post;
import com.example.lineta_posts_interaction.service.PostUpService;
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
public class GetPostController {

    private final PostUpService postUpService;
    private final UserClient userClient;

    @Autowired
    public GetPostController(PostUpService postUpService, UserClient userClient) {
        this.postUpService = postUpService;
        this.userClient = userClient;
    }

//    @GetMapping("/posts")
//    public List<Post> getPostsByUsername(
//            @RequestParam(defaultValue = "0") int page,  // Trang hiện tại, mặc định là 0
//            @RequestParam(defaultValue = "5") int size  // Số bài viết mỗi lần, mặc định là 5
//    ) throws ExecutionException, InterruptedException {
//
//
//        return postUpService.getPosts(page, size);
//    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPostsByUsername(
            @RequestHeader("Authorization") String authorizationHeader, // 👈 Lấy token từ request
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) throws ExecutionException, InterruptedException {

        List<Post> posts = postUpService.getPosts(page, size);

        Set<String> usernames = posts.stream()
                .map(Post::getUsername)
                .collect(Collectors.toSet());

        // Gọi sang UserService và truyền token
        System.out.println(extractToken(authorizationHeader));
        Map<String, UserDTO> userMap = userClient.getUsersByUsernames(usernames, extractToken(authorizationHeader));

        List<PostWithUserDTO> postDTOs = posts.stream()
                .map(post -> new PostWithUserDTO(post, userMap.get(post.getUsername())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(postDTOs);
    }

    // Nếu cần tách "Bearer <token>" thành token:
    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return header; // fallback
    }

}
