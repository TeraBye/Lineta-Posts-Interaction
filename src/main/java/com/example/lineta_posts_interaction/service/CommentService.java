
package com.example.lineta_posts_interaction.service;

import com.example.lineta_posts_interaction.dto.request.CommentUserRequestDTO;
import com.example.lineta_posts_interaction.dto.response.CommentWithUserDTO;
import com.example.lineta_posts_interaction.entity.Comment;

import com.google.cloud.firestore.WriteResult;
import java.util.concurrent.ExecutionException;

public interface CommentService {
    WriteResult saveComment(CommentUserRequestDTO comment) throws ExecutionException, InterruptedException;
}
