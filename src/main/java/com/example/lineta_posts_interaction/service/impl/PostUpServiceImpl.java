package com.example.lineta_posts_interaction.service.impl;

import com.example.lineta_posts_interaction.entity.Post;
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
public class PostUpServiceImpl implements PostUpService {

    @Override
    public List<Post> getPosts(int page, int size) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<Post> posts = new ArrayList<>();

        // Cập nhật query phân trang
        Query query = db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(size);

        // Nếu page > 1, áp dụng startAfter để phân trang
        if (page > 1) {
            QueryDocumentSnapshot lastVisiblePost = getLastVisiblePostForPage(page - 1, size);
            if (lastVisiblePost != null) {
                query = query.startAfter(lastVisiblePost);
            }
        }

        QuerySnapshot querySnapshot = query.get().get();

        for (QueryDocumentSnapshot document : querySnapshot) {
            Post post = document.toObject(Post.class);
            post.setId(document.getId()); // Gán ID của document vào Post
            posts.add(post);
        }

        return posts;
    }


    // Lấy bài viết cuối cùng trong trang trước
    private QueryDocumentSnapshot getLastVisiblePostForPage(int page, int size) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        Query query = db.collection("posts")
                .orderBy("timestamp",Query.Direction.DESCENDING) // Giả sử bạn sắp xếp theo thời gian
                .limit(size * page);

        QuerySnapshot querySnapshot = query.get().get();
        if (!querySnapshot.isEmpty()) {
            return querySnapshot.getDocuments().get(querySnapshot.size() - 1); // Bài viết cuối cùng của trang trước
        }
        return null;
    }
}
