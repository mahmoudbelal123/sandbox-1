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

public class SearchRoomsNewModel {

    private int roomType;
    private String roomName;
    private String createdAt;
    private int roomId;
    private String roomUrl;
    private String categoryHead;
    private String buttonText;
    private Integer extraElementType;
    private Integer membersCount;
    private boolean userBlocked;
    private int userReqStatus;
    private boolean favourite;
    private boolean requestedByMe;
    private boolean globalRoom;

    public boolean isRequestedByMe() {
        return requestedByMe;
    }

    public void setRequestedByMe(boolean requestedByMe) {
        this.requestedByMe = requestedByMe;
    }

    public boolean isGlobalRoom() {
        return globalRoom;
    }

    public void setGlobalRoom(boolean globalRoom) {
        this.globalRoom = globalRoom;
    }

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
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

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomUrl() {
        return roomUrl;
    }

    public void setRoomUrl(String roomUrl) {
        this.roomUrl = roomUrl;
    }

    public String getCategoryHead() {
        return categoryHead;
    }

    public void setCategoryHead(String categoryHead) {
        this.categoryHead = categoryHead;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public Integer getExtraElementType() {
        return extraElementType;
    }

    public void setExtraElementType(Integer extraElementType) {
        this.extraElementType = extraElementType;
    }

    public Integer getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
    }

    public boolean isUserBlocked() {
        return userBlocked;
    }

    public void setUserBlocked(boolean userBlocked) {
        this.userBlocked = userBlocked;
    }

    public int getUserReqStatus() {
        return userReqStatus;
    }

    public void setUserReqStatus(int userReqStatus) {
        this.userReqStatus = userReqStatus;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

}
