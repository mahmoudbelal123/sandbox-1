package com.appster.turtle.ui.home;

import com.appster.turtle.network.response.Posts;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 12/03/18.
 */

public interface PostListener {

    void onMenuClicked(Posts post, int pos);

    void onPollClick(int position, int postId, int answerId);


    void onLikedBy(int position, int postId);

    void onLiked(int position, Posts post, int likeStatus, int alreadyStatus);

    void onClick(Posts post);

    void onMeetupStatusChange(int position, int postId, int status, boolean isMeetupResponded);

    void onHasHTagClick(Posts post, int pos, String string);


    void onCommentClicked(Posts post);

}
