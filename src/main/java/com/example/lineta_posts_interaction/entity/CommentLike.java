package com.example.lineta_posts_interaction.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class CommentLike {
    private String username;
    private String commentId;
    private String tempContent;
}
