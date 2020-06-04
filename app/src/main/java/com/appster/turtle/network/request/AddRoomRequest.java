/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.request;

import com.appster.turtle.model.Tag;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class AddRoomRequest {


    private boolean memberCanPost;
    private ArrayList<Integer> memberIds;
    private boolean publicFlag;
    private String roomDesc;
    private int roomId;
    private String roomName;
    private int roomType;
    private boolean searchable;
    private boolean globalRoom;
    private ArrayList<Tag> tags;

    public void setRoomDesc(String roomDesc) {
        this.roomDesc = roomDesc;
    }

    public void setMemberCanPost(boolean memberCanPost) {
        this.memberCanPost = memberCanPost;
    }

    public void setMemberIds(ArrayList<Integer> memberIds) {
        this.memberIds = memberIds;
    }

    public void setPublicFlag(boolean publicFlag) {
        this.publicFlag = publicFlag;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public void setGlobalRoom(boolean globalRoom) {
        this.globalRoom = globalRoom;
    }
}
