package com.example.lineta_posts_interaction.dto.response;
import com.example.lineta_posts_interaction.entity.Comment;
import lombok.Data;

@Data
public class CommentWithUserDTO {
    private String commentId;
    private String username;
    private String content;
    private String date;
    private String postID;

    private String fullName;
    private String profilePicURL;

    public CommentWithUserDTO(Comment comment, UserDTO userDTO) {
        this.commentId = comment.getId();
        this.username = userDTO.getUsername();
        this.content = comment.getContent();
        this.date = comment.getDate();
        this.postID = comment.getPostID();


        if (userDTO != null) {
            this.fullName = userDTO.getFullName();
            this.profilePicURL = userDTO.getProfilePicURL();
        }
    }
}

