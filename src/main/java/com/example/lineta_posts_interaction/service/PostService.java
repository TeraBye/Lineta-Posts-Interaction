package com.example.lineta_posts_interaction.service;

import com.example.lineta_posts_interaction.dto.request.PostUserRequestDTO;
import com.example.lineta_posts_interaction.entity.Post;
import com.google.cloud.firestore.WriteResult;
import java.util.concurrent.ExecutionException;

public interface PostService {
    WriteResult savePost(PostUserRequestDTO post) throws ExecutionException, InterruptedException;
    WriteResult incrementLike(String postId, int delta) throws ExecutionException, InterruptedException;
    String getUsernameFromPost(String postId) throws ExecutionException, InterruptedException;
    String getUsernameFromComment(String commentId) throws ExecutionException, InterruptedException;
    WriteResult incrementCommentLike(String commentId, int delta) throws ExecutionException, InterruptedException;
    void deletePostWithDependencies(String postId) throws ExecutionException, InterruptedException;
    WriteResult updatePostContent(String postId, String content) throws ExecutionException, InterruptedException;
}
