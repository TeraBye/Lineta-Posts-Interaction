package com.example.lineta_posts_interaction.service;

import com.example.lineta_posts_interaction.dto.request.CommentLikeUserRequestDTO;
import com.google.cloud.firestore.WriteResult;

import java.util.concurrent.ExecutionException;

public interface CommentLikeService {
    WriteResult saveLike(CommentLikeUserRequestDTO like) throws ExecutionException, InterruptedException;
    WriteResult deleteLike(String username, String commentId) throws Exception;
    boolean isCommentLikedByUser(String username, String commentId) throws ExecutionException, InterruptedException;
}
