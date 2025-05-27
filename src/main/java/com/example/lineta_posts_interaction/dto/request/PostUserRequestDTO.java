package com.example.lineta_posts_interaction.dto.request;
import com.example.lineta_posts_interaction.entity.Post;
import lombok.Data;

@Data
public class PostUserRequestDTO {
    private String postId;
    private String content;
    private String username;
    private String picture;
    private String video;
    private int numberOfLike = 0;
    private String date;
    private String fullName;
    private String profilePicURL;
    private String uid;

    public PostUserRequestDTO() {
        super();
    }

}

