package com.example.lineta_posts_interaction.service.impl;
import com.example.lineta_posts_interaction.entity.Comment;
import com.example.lineta_posts_interaction.entity.Post;
import com.example.lineta_posts_interaction.service.GetCommentService;
import com.example.lineta_posts_interaction.service.PostUpService;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class GetCommentServiceImpl implements GetCommentService {

    @Override
    public List<Comment> getComments(String postID, int page, int size) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<Comment> comments = new ArrayList<>();

        // Cập nhật query phân trang
        Query query = db.collection("comments")
                .whereEqualTo("postID", postID) // Lọc theo postID
                .orderBy("timestamp")
                .limit(size);

        // Nếu page > 1, áp dụng startAfter để phân trang
        if (page > 1) {
            QueryDocumentSnapshot lastVisiblePost = getLastVisiblePostForPage(postID,page - 1, size);
            if (lastVisiblePost != null) {
                query = query.startAfter(lastVisiblePost);
            }
        }

        QuerySnapshot querySnapshot = query.get().get();

        for (QueryDocumentSnapshot document : querySnapshot) {
            Comment comment = document.toObject(Comment.class);
            comment.setId(document.getId()); // Gán ID của document vào comment
            comments.add(comment);
        }

        return comments;
    }


    // Lấy bài viết cuối cùng trong trang trước
    private QueryDocumentSnapshot getLastVisiblePostForPage(String postID, int page, int size) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        Query query = db.collection("comments")
                .whereEqualTo("postID", postID) // Lọc theo postID
                .orderBy("timestamp")
                .limit(size * page);

        QuerySnapshot querySnapshot = query.get().get();
        if (!querySnapshot.isEmpty()) {
            return querySnapshot.getDocuments().get(querySnapshot.size() - 1);
        }
        return null;
    }

}
