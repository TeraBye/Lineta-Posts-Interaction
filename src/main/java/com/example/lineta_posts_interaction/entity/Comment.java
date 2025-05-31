package com.example.lineta_posts_interaction.entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class Comment {
    private String id;
    private String username;
    private String content;
    private String date;
    private String postID;
    private int numberOfLike;
}
