/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.model;

/**
 * Created by rohantaneja on 05-Sep-2017
 */

public class MeetupPostDetail {

    private String title;
    private String address;
    private Double lat;
    private Double lon;
    private String duration;
    private Integer scheduledAt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Integer scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
}
