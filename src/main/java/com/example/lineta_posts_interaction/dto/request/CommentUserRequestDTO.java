package com.example.lineta_posts_interaction.dto.request;
import com.example.lineta_posts_interaction.entity.Comment;
import lombok.Data;

@Data
public class CommentUserRequestDTO {
    private String commentId;
    private String username;
    private String content;
    private String date;
    private String postID;
    private int numberOfLike;

    private String fullName;
    private String profilePicURL;

    public CommentUserRequestDTO() {
        super();
    }
}
