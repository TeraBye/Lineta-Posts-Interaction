package com.example.lineta_posts_interaction.dto.request;

import lombok.Data;

@Data
public class CommentLikeUserRequestDTO {
    private String username;
    private String commentId;
    private String tempContent;

    private String fullName;
    private String profilePicURL;

    public CommentLikeUserRequestDTO() {
    }
}
