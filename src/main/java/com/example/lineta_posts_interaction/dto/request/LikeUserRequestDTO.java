package com.example.lineta_posts_interaction.dto.request;
import com.example.lineta_posts_interaction.entity.Like;
import lombok.Data;

@Data
public class LikeUserRequestDTO {
    private String username;
    private String postID;
    private String tempContent;

    private String fullName;
    private String profilePicURL;

    public LikeUserRequestDTO() {
    }
}