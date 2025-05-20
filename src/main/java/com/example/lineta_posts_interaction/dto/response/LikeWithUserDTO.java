package com.example.lineta_posts_interaction.dto.response;
import com.example.lineta_posts_interaction.entity.Like;
import lombok.Data;

@Data
public class LikeWithUserDTO {
    private String username;
    private String postID;
    private String tempContent;

    private String fullName;
    private String profilePicURL;

    public LikeWithUserDTO(Like like, UserDTO userDTO) {
        this.username = like.getUsername();
        this.postID = like.getPostID();
        this.tempContent = like.getTempContent();


        if (userDTO != null) {
            this.fullName = userDTO.getFullName();
            this.profilePicURL = userDTO.getProfilePicURL();
        }
    }
}

