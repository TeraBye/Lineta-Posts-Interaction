
package com.example.lineta_posts_interaction.service;

import com.example.lineta_posts_interaction.dto.request.ReplyUserRequestDTO;
import com.example.lineta_posts_interaction.dto.response.ReplyWithUserDTO;
import com.example.lineta_posts_interaction.entity.ReplyComment;

import com.google.cloud.firestore.WriteResult;
import java.util.concurrent.ExecutionException;

public interface ReplyCommentService {
    WriteResult saveReplyComment(ReplyUserRequestDTO replyComment) throws ExecutionException, InterruptedException;
}

