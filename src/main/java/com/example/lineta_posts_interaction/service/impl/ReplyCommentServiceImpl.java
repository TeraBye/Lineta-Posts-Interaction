package com.example.lineta_posts_interaction.service.impl;

import com.example.lineta_posts_interaction.dto.request.ReplyUserRequestDTO;
import com.example.lineta_posts_interaction.dto.response.ReplyWithUserDTO;
import com.example.lineta_posts_interaction.entity.ReplyComment;

import com.example.lineta_posts_interaction.service.ReplyCommentService;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.database.core.Repo;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class ReplyCommentServiceImpl implements ReplyCommentService {
    private final Firestore firestore;


    public ReplyCommentServiceImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public WriteResult saveReplyComment(ReplyUserRequestDTO replyComment) throws ExecutionException, InterruptedException {
        Map<String, Object> commentFB = new HashMap<>();
        commentFB.put("username", replyComment.getUsername());
        commentFB.put("content", replyComment.getContent());
        commentFB.put("timestamp", Timestamp.now());
        commentFB.put("commentID", replyComment.getCommentId());


        DocumentReference docRef = firestore.collection("replyComments").document();
        ApiFuture<WriteResult> writeResult = docRef.set(commentFB);
        return writeResult.get();
    }
}
