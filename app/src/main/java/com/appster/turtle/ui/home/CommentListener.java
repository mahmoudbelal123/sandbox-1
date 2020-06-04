package com.appster.turtle.ui.home;

import com.appster.turtle.model.Comment;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 23/03/18.
 */

public interface CommentListener {
    void onCommentMenuClicked(Comment comment, int pos);

    void onCommentLiked(int position, int postId, int likeStatus);

    void onCommentLikedBy(int position, int postId, int likeStatus);

}
