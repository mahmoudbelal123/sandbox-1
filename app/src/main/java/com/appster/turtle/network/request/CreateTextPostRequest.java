/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.request;

import com.appster.turtle.model.Tag;

import java.util.List;

/**
 * Created by rohantaneja on 04-Sep-2017
 */

@SuppressWarnings("ALL")
public class CreateTextPostRequest {

    private Integer postId;
    private List<Integer> roomIds = null;
    private List<Tag> tags = null;
    private List<Integer> taggedUserIds = null;
    private String text;
    private Integer visibility;

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public void setRoomIds(List<Integer> roomIds) {
        this.roomIds = roomIds;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setTaggedUserIds(List<Integer> taggedUserIds) {
        this.taggedUserIds = taggedUserIds;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

}
