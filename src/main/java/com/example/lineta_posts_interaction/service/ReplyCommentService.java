
package com.example.lineta_posts_interaction.service;

import com.example.lineta_posts_interaction.entity.ReplyComment;

import com.google.cloud.firestore.WriteResult;
import java.util.concurrent.ExecutionException;

public interface ReplyCommentService {
    WriteResult saveReplyComment(ReplyComment replyComment) throws ExecutionException, InterruptedException;
}

