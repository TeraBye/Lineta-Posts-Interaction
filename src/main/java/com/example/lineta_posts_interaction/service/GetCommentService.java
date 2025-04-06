package com.example.lineta_posts_interaction.service;

import com.example.lineta_posts_interaction.entity.Post;
import com.example.lineta_posts_interaction.entity.Comment;
import com.google.api.Page;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface GetCommentService {

    List<Comment> getComments(String postID, int page, int size) throws ExecutionException, InterruptedException ;

}
