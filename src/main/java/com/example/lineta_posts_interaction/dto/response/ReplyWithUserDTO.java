package com.example.lineta_posts_interaction.dto.response;
import com.example.lineta_posts_interaction.entity.ReplyComment;
import lombok.Data;

@Data
public class ReplyWithUserDTO {
    String username;
    String commentId;
    String content;

    private String fullName;
    private String profilePicURL;

    public ReplyWithUserDTO(ReplyComment replyComment, UserDTO userDTO) {
        this.username = replyComment.getUsername();
        this.commentId = replyComment.getCommentID();
        this.content = replyComment.getContent();


        if (userDTO != null) {
            this.fullName = userDTO.getFullName();
            this.profilePicURL = userDTO.getProfilePicURL();
        }
    }
}


