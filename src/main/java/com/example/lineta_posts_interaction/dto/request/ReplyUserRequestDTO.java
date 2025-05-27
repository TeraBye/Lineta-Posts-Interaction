package com.example.lineta_posts_interaction.dto.request;

import lombok.Data;

@Data
public class ReplyUserRequestDTO {
    String username;
    String commentId;
    String content;

    private String fullName;
    private String profilePicURL;

    public ReplyUserRequestDTO() {
        super();
    }
}
