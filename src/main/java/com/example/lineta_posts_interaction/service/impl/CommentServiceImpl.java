package com.example.lineta_posts_interaction.service.impl;

import com.example.lineta_posts_interaction.dto.request.CommentUserRequestDTO;
import com.example.lineta_posts_interaction.dto.response.CommentWithUserDTO;
import com.example.lineta_posts_interaction.entity.Comment;

import com.example.lineta_posts_interaction.service.CommentService;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class CommentServiceImpl implements CommentService {
    private final Firestore firestore;

    public CommentServiceImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public WriteResult saveComment(CommentUserRequestDTO comment) throws ExecutionException, InterruptedException {
        Map<String, Object> commentFB = new HashMap<>();
        commentFB.put("username", comment.getUsername());
        commentFB.put("content", comment.getContent());
        commentFB.put("date", comment.getDate());
        commentFB.put("timestamp", Timestamp.now());
        commentFB.put("postID", comment.getPostID());


        DocumentReference docRef = firestore.collection("comments").document(comment.getCommentId());
        ApiFuture<WriteResult> writeResult = docRef.set(commentFB);
        return writeResult.get();
    }
}