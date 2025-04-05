
package com.example.lineta_posts_interaction.service.impl;

import org.springframework.stereotype.Service;
import com.example.lineta_posts_interaction.service.PostUpService;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.example.lineta_posts_interaction.entity.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PostUpServiceImpl implements PostUpService {

    @Override
    public List<Post> getPostsByUsername(String username) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<Post> posts = new ArrayList<>();

        // Truy vấn các post có username là "terabye"
        QuerySnapshot querySnapshot = db.collection("posts")
                .whereEqualTo("username", username)
                .get()
                .get(); // Truy vấn và nhận kết quả

        for (QueryDocumentSnapshot document : querySnapshot) {
            Post post = document.toObject(Post.class);
            posts.add(post);
        }
        return posts;
    }
}
