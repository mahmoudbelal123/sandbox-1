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
public class CreateMeetupRequest {

    private String address;
    private Integer durationId;
    private Double lat;
    private Double lon;
    private Integer postId;
    private List<Integer> roomIds;
    private String scheduleTime;
    private List<Tag> tags;
    private String title;

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDurationId(Integer durationId) {
        this.durationId = durationId;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public void setRoomIds(List<Integer> roomIds) {
        this.roomIds = roomIds;
    }


    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
