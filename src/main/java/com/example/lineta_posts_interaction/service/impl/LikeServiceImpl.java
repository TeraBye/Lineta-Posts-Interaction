package com.example.lineta_posts_interaction.service.impl;


import com.example.lineta_posts_interaction.dto.request.LikeUserRequestDTO;
import com.example.lineta_posts_interaction.dto.response.LikeWithUserDTO;
import com.example.lineta_posts_interaction.entity.Like;
import com.example.lineta_posts_interaction.service.LikeService;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class LikeServiceImpl implements LikeService {
    private final Firestore firestore;

    public LikeServiceImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public WriteResult saveLike(LikeUserRequestDTO like) throws ExecutionException, InterruptedException {
        Map<String, Object> commentFB = new HashMap<>();
        commentFB.put("username", like.getUsername());
        commentFB.put("timestamp", Timestamp.now());
        commentFB.put("postID", like.getPostID());
        commentFB.put("tempContent", like.getTempContent());


        DocumentReference docRef = firestore.collection("postLikes").document();
        ApiFuture<WriteResult> writeResult = docRef.set(commentFB);
        return writeResult.get();
    }

    @Override
    public boolean isPostLikedByUser(String username, String postID) throws ExecutionException, InterruptedException {
        CollectionReference likesRef = firestore.collection("postLikes");

        Query query = likesRef
                .whereEqualTo("username", username)
                .whereEqualTo("postID", postID)
                .limit(1);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        return !querySnapshot.get().isEmpty();
    }

    @Override
    public WriteResult deleteLike(String username, String postID) throws Exception {
        CollectionReference likesRef = firestore.collection("postLikes");

        // Truy vấn like theo username và postID
        ApiFuture<QuerySnapshot> future = likesRef
                .whereEqualTo("username", username)
                .whereEqualTo("postID", postID)
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