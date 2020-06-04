/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.model;

/**
 * Created  on 01/11/17 .
 */

public class SearchRoomsModel {

    private String roomType;
    private String roomName;
    private String createdAt;
    private String userReqStatus;
    private String roomId;
    private String roomUrl;
    private String favourite;
    private String categoryHead;
    private String buttonText;
    private Integer extraElementType;
    private Integer membersCount;
    private boolean userBlocked;


    public boolean isUserBlocked() {
        return userBlocked;
    }

    public void setUserBlocked(boolean userBlocked) {
        this.userBlocked = userBlocked;
    }


    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserReqStatus() {
        return userReqStatus;
    }

    public void setUserReqStatus(String userReqStatus) {
        this.userReqStatus = userReqStatus;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomUrl() {
        return roomUrl;
    }

    public void setRoomUrl(String roomUrl) {
        this.roomUrl = roomUrl;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

    public Integer getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
    }

    public void setCategoryHead(String categoryHead) {
        this.categoryHead = categoryHead;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getCategoryHead() {
        return categoryHead;
    }

    public String getButtonText() {
        return buttonText;
    }

    public Integer getExtraElementType() {
        return extraElementType;
    }

    public void setExtraElementType(Integer extraElementType) {
        this.extraElementType = extraElementType;
    }
}
