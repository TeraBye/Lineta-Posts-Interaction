package com.example.lineta_posts_interaction.service;

import com.example.lineta_posts_interaction.entity.Post;
import com.google.cloud.firestore.WriteResult;
import java.util.concurrent.ExecutionException;

public interface PostService {
    WriteResult savePost(Post post) throws ExecutionException, InterruptedException;
}
