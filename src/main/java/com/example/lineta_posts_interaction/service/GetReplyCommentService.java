package com.example.lineta_posts_interaction.service;

import com.example.lineta_posts_interaction.entity.ReplyComment;


import java.util.List;
import java.util.concurrent.ExecutionException;

public interface GetReplyCommentService {

    List<ReplyComment> getReplyComments(String commentID, int page, int size) throws ExecutionException, InterruptedException ;

}
