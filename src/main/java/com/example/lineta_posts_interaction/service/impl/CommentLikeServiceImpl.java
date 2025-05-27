package com.example.lineta_posts_interaction.service.impl;

import com.example.lineta_posts_interaction.dto.request.CommentLikeUserRequestDTO;
import com.example.lineta_posts_interaction.service.CommentLikeService;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class CommentLikeServiceImpl implements CommentLikeService {
    private final Firestore firestore;

    public CommentLikeServiceImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public WriteResult saveLike(CommentLikeUserRequestDTO like) throws ExecutionException, InterruptedException {
        Map<String, Object> commentFB = new HashMap<>();
        commentFB.put("username", like.getUsername());
        commentFB.put("timestamp", Timestamp.now());
        commentFB.put("commentId", like.getCommentId());
        commentFB.put("tempContent", like.getTempContent());


        DocumentReference docRef = firestore.collection("commentLikes").document();
        ApiFuture<WriteResult> writeResult = docRef.set(commentFB);
        return writeResult.get();
    }

    @Override
    public boolean isCommentLikedByUser(String username, String commentId) throws ExecutionException, InterruptedException {
        CollectionReference likesRef = firestore.collection("commentLikes");

        Query query = likesRef
                .whereEqualTo("username", username)
                .whereEqualTo("commentId", commentId)
                .limit(1);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        return !querySnapshot.get().isEmpty();
    }

    @Override
    public WriteResult deleteLike(String username, String commentId) throws Exception {
        CollectionReference likesRef = firestore.collection("commentLikes");

        // Truy vấn like theo username và commentId
        ApiFuture<QuerySnapshot> future = likesRef
                .whereEqualTo("username", username)
                .whereEqualTo("commentId", commentId)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        if (!documents.isEmpty()) {
            // Giả sử mỗi user chỉ like 1 lần → xóa like đầu tiên tìm thấy
            DocumentReference docRef = documents.get(0).getReference();
            ApiFuture<WriteResult> writeResult = docRef.delete();
            return writeResult.get();
        }

        throw new Exception("Like not found to delete");
    }
}
