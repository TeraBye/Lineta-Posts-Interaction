
package com.example.lineta_posts_interaction.service;


import com.example.lineta_posts_interaction.dto.request.LikeUserRequestDTO;
import com.example.lineta_posts_interaction.dto.response.LikeWithUserDTO;
import com.example.lineta_posts_interaction.entity.Like;
import com.google.cloud.firestore.WriteResult;
import java.util.concurrent.ExecutionException;

public interface LikeService {
    WriteResult saveLike(LikeUserRequestDTO like) throws ExecutionException, InterruptedException;
    WriteResult deleteLike(String username, String postID) throws Exception;
    boolean isPostLikedByUser(String username, String postID) throws ExecutionException, InterruptedException;
}
