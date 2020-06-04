package com.appster.turtle.network.request;

import com.appster.turtle.model.Tag;

import java.util.ArrayList;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 22/03/18.
 */

public class PostCommentRequest {


    private String comment;
    private int commentId;
    private int parCommentId;
    private int postId;
    private ArrayList<Integer> taggedUserIds;
    private ArrayList<Tag> tags;

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public void setParCommentId(int parCommentId) {
        this.parCommentId = parCommentId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public void setTaggedUserIds(ArrayList<Integer> taggedUserIds) {
        this.taggedUserIds = taggedUserIds;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }
}

