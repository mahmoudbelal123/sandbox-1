/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.Tag;
import com.appster.turtle.network.BaseResponse;
import com.appster.turtle.network.request.PollAnswer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by atul on 04/09/17.
 * To inject activity reference.
 */

public class PollResponse extends BaseResponse {

    private int postId;
    private List<Tag> tags;
    private int visibility;
    private String question;
    private boolean activeFlag;
    private List<Integer> roomIds;
    private List<PollAnswer> pollAnswersList;

    public PollResponse() {
        tags = new ArrayList<>();
        roomIds = new ArrayList<>();
        pollAnswersList = new ArrayList<>();
    }

    public int getPostId() {
        return postId;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public int getVisibility() {
        return visibility;
    }

    public String getQuestion() {
        return question;
    }

    public boolean isActiveFlag() {
        return activeFlag;
    }

    public List<Integer> getRoomIds() {
        return roomIds;
    }

    public List<PollAnswer> getPollAnswersList() {
        return pollAnswersList;
    }

}
