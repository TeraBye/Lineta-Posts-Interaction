package com.example.lineta_posts_interaction.service;

import com.example.lineta_posts_interaction.entity.Post;
import com.google.api.Page;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface PostUpService {
    //List<Post> getPostsByUsername(String username) throws ExecutionException, InterruptedException;
    List<Post> getPosts(int page, int size) throws ExecutionException, InterruptedException ;

    }
