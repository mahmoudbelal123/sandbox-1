/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.request;

import com.appster.turtle.model.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by atul on 04/09/17.
 * To inject activity reference.
 */

public class Poll {

    private int postId;
    private List<Tag> tags;
    private String question;
    private List<Integer> roomIds;
    private int visibility = 0;

    public void setRoomIds(List<Integer> roomIds) {
        this.roomIds = roomIds;
    }


    private List<PollAnswer> pollAnswersList;

    public Poll() {
        tags = new ArrayList<>();
        roomIds = new ArrayList<>();
        pollAnswersList = new ArrayList<>();
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public List<Integer> getRoomIds() {
        return roomIds;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<PollAnswer> getPollAnswersList() {
        return pollAnswersList;
    }

    public void setPollAnswersList(List<PollAnswer> pollAnswersList) {
        this.pollAnswersList = pollAnswersList;
    }

}
