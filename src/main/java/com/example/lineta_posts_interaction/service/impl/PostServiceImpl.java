package com.example.lineta_posts_interaction.service.impl;

import com.example.lineta_posts_interaction.entity.Post;
import com.example.lineta_posts_interaction.service.PostService;
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
public class PostServiceImpl implements PostService {
    private final Firestore firestore;

    public PostServiceImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public WriteResult savePost(Post post) throws ExecutionException, InterruptedException {
        Map<String, Object> postFB = new HashMap<>();
        postFB.put("username", post.getUsername());
        postFB.put("content", post.getContent());
        postFB.put("picture", post.getPicture());
        postFB.put("video", post.getVideo());
        postFB.put("date", post.getDate());
        postFB.put("timestamp", Timestamp.now());


        DocumentReference docRef = firestore.collection("posts").document();
        ApiFuture<WriteResult> writeResult = docRef.set(postFB);
        return writeResult.get();
    }
}