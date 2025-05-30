package com.example.lineta_posts_interaction.service.impl;

import com.example.lineta_posts_interaction.dto.request.PostUserRequestDTO;
import com.example.lineta_posts_interaction.entity.Post;
import com.example.lineta_posts_interaction.service.PostService;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
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

    public WriteResult updatePostContent(String postId, String content) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("posts").document(postId);

        // Chỉ cập nhật field "content" và thêm timestamp mới nếu cần
        Map<String, Object> updates = new HashMap<>();
        updates.put("content", content);
        ApiFuture<WriteResult> writeResult = docRef.update(updates);
        return writeResult.get();
    }


    public WriteResult incrementLike(String postId, int delta) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("posts").document(postId);
        ApiFuture<WriteResult> future = docRef.update("numberOfLike", com.google.cloud.firestore.FieldValue.increment(delta));
        return future.get();
    }

    public WriteResult incrementCommentLike(String commentId, int delta) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("comments").document(commentId);
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

    public String getUsernameFromComment(String commentId) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection("comments")
                .document(commentId)
                .get()
                .get();

        if (snapshot.exists()) {
            return snapshot.getString("username");
        } else {
            return null;
        }
    }

    public void deletePostWithDependencies(String postId) throws ExecutionException, InterruptedException {
        WriteBatch batch = firestore.batch();

        // 1. Xóa post chính
        DocumentReference postRef = firestore.collection("posts").document(postId);
        batch.delete(postRef);

        // 2. Tìm comments theo postId
        List<QueryDocumentSnapshot> commentDocs = firestore.collection("comments")
                .whereEqualTo("postId", postId)
                .get()
                .get()
                .getDocuments();

        for (QueryDocumentSnapshot commentDoc : commentDocs) {
            String commentId = commentDoc.getId();
            DocumentReference commentRef = commentDoc.getReference();
            batch.delete(commentRef);

            // 2.1 Xóa replyComments theo commentId
            List<QueryDocumentSnapshot> replyDocs = firestore.collection("replyComments")
                    .whereEqualTo("commentId", commentId)
                    .get()
                    .get()
                    .getDocuments();
            for (QueryDocumentSnapshot replyDoc : replyDocs) {
                batch.delete(replyDoc.getReference());
            }

            // 2.2 Xóa commentLikes theo commentId
            List<QueryDocumentSnapshot> commentLikeDocs = firestore.collection("commentLikes")
                    .whereEqualTo("commentId", commentId)
                    .get()
                    .get()
                    .getDocuments();
            for (QueryDocumentSnapshot likeDoc : commentLikeDocs) {
                batch.delete(likeDoc.getReference());
            }
        }

        // 3. Xóa postLikes theo postId
        List<QueryDocumentSnapshot> postLikeDocs = firestore.collection("postLikes")
                .whereEqualTo("postId", postId)
                .get()
                .get()
                .getDocuments();
        for (QueryDocumentSnapshot postLikeDoc : postLikeDocs) {
            batch.delete(postLikeDoc.getReference());
        }

        // 4. Thực thi batch
        batch.commit().get();
    }

}