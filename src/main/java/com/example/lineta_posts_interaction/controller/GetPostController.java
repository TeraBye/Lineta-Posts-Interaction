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
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class GetPostController {

    private final PostUpService postUpService;
    private final UserClient userClient;

    @Autowired
    private RestTemplate restTemplate;


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

    @GetMapping("/getPosts-user")
    public ResponseEntity<ApiResponse<List<PostWithUserDTO>>> getPostsUsername(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) throws ExecutionException, InterruptedException {

        List<Post> posts = postUpService.getPostsByUsername(username,page, size);

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

    @GetMapping("/getPosts-home")
    public ResponseEntity<ApiResponse<List<PostWithUserDTO>>> getPostsUsernames(
            @RequestParam String uid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) throws ExecutionException, InterruptedException {

        List<String> friends = userClient.getFollowingUsernames(uid);
        List<Post> posts = new ArrayList<>();
        if(friends.isEmpty()){
            posts = postUpService.getPosts(page, size);
        }
        else{
            posts = postUpService.getPostsByUsernames(friends,page, size);
        }


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
}
