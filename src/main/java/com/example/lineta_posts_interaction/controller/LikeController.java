package com.example.lineta_posts_interaction.controller;

import com.example.lineta_posts_interaction.entity.Like;
import com.example.lineta_posts_interaction.service.LikeService;
import com.example.lineta_posts_interaction.service.PostService;
import com.google.cloud.firestore.WriteResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/likes")
public class LikeController {
    private final LikeService likeService;
    private final PostService postService;

    public LikeController(LikeService likeService, PostService postService) {
        this.likeService = likeService;
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity<Like> addLike(@RequestBody Like like) throws ExecutionException, InterruptedException {
        WriteResult saved = likeService.saveLike(like);
        return ResponseEntity.ok(like);
    }

//    @PostMapping("/like")
//    public ResponseEntity<String> toggleLike(
//            @RequestBody Like like
//    ) {
//        try {
//            WriteResult saved = likeService.saveLike(like);
//            WriteResult liked = postService.incrementLike(like.getPostID(), 1);
//            return ResponseEntity.ok("liked " + saved.getUpdateTime());
//        } catch (ExecutionException | InterruptedException e) {
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }

    @GetMapping("/check")
    public ResponseEntity<?> checkIfLiked(
            @RequestParam String username,
            @RequestParam String postID
    ) throws ExecutionException, InterruptedException {
        boolean isLiked = likeService.isPostLikedByUser(username, postID);
        return ResponseEntity.ok(Map.of("liked", isLiked));
    }

    @PostMapping("/likes")
    public ResponseEntity<?> toggleLikex(@RequestBody Like like) throws Exception {
        boolean alreadyLiked = likeService.isPostLikedByUser(like.getUsername(), like.getPostID());

        if (alreadyLiked) {
            likeService.deleteLike(like.getUsername(), like.getPostID()); // và giảm số like
            postService.incrementLike(like.getPostID(), -1);
        } else {
            likeService.saveLike(like);
            postService.incrementLike(like.getPostID(), 1);
        }

        return ResponseEntity.ok().build();
    }


}
