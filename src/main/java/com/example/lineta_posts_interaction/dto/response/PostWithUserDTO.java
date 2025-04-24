package com.example.lineta_posts_interaction.dto.response;
import com.example.lineta_posts_interaction.entity.Post;
import lombok.Data;

@Data
public class PostWithUserDTO {
    private String postId;
    private String content;
    private String username;
    private String picture;
    private String video;
    private int numberOfLike;
    private String date;

    private String fullName;
    private String profilePicURL;

    public PostWithUserDTO(Post post, UserDTO userDTO) {
        this.postId = post.getId();
        this.content = post.getContent();
        this.username = post.getUsername();
        this.picture = post.getPicture();
        this.video = post.getVideo();
        this.date = post.getDate();
        this.numberOfLike = post.getNumberOfLike();


        if (userDTO != null) {
            this.fullName = userDTO.getFullName();
            this.profilePicURL = userDTO.getProfilePicURL();
        }
    }
}
