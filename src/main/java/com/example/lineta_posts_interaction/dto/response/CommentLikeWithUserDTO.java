package com.example.lineta_posts_interaction.dto.response;

import com.example.lineta_posts_interaction.entity.CommentLike;
import com.example.lineta_posts_interaction.entity.Like;
import lombok.Data;


@Data
public class CommentLikeWithUserDTO {
    private String username;
    private String commentId;
    private String tempContent;

    private String fullName;
    private String profilePicURL;

    public CommentLikeWithUserDTO(CommentLike like, UserDTO userDTO) {
        this.username = like.getUsername();
        this.commentId = like.getCommentId();
        this.tempContent = like.getTempContent();


        if (userDTO != null) {
            this.fullName = userDTO.getFullName();
            this.profilePicURL = userDTO.getProfilePicURL();
        }
    }
}
