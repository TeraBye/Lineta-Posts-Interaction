package com.example.lineta_posts_interaction.service.impl;

import com.example.lineta_posts_interaction.dto.request.PostUserRequestDTO;
import com.example.lineta_posts_interaction.entity.Post;
import com.example.lineta_posts_interaction.service.PostService;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
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
    public WriteResult savePost(PostUserRequestDTO post) throws ExecutionException, InterruptedException {
        Map<String, Object> postFB = new HashMap<>();
        postFB.put("username", post.getUsername());
        postFB.put("content", post.getContent());
        postFB.put("picture", post.getPicture());
        postFB.put("video", post.getVideo());
        postFB.put("date", post.getDate());
        postFB.put("numberOfLike", post.getNumberOfLike());
        postFB.put("timestamp", Timestamp.now());

        DocumentReference docRef = firestore.collection("posts").document(post.getPostId());
        ApiFuture<WriteResult> writeResult = docRef.set(postFB);
        return writeResult.get();
    }

    public WriteResult incrementLike(String postId, int delta) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("posts").document(postId);
        ApiFuture<WriteResult> future = docRef.update("numberOfLike", com.google.cloud.firestore.FieldValue.increment(delta));
        return future.get();
    }

    public String getUsernameFromPost(String postId) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection("posts")
                .document(postId)
                .get()
                .get();

        if (snapshot.exists()) {
            return snapshot.getString("username");
        } else {
            return null;
        }
    }

}