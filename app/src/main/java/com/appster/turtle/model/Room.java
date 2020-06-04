/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.appster.turtle.BR;
import com.appster.turtle.util.StringUtils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class Room extends BaseObservable implements Parcelable {


    private ArrayList<User> allAdmins;
    private ArrayList<User> randomUsers;
    private String createdAt;
    private String createdBy;
    private int membersCount;
    private int roomId;
    @SerializedName("public")
    private boolean _public;
    private String roomName;
    private String roomUrl;
    private boolean memberCanPost;
    private boolean primaryAdmin;
    private boolean searchable;
    private int roomType;
    private String status;
    private ArrayList<Tag> tags;
    private boolean favourite;
    private boolean isUnread;
    private boolean isPinned;
    private boolean userAdmin;
    private boolean userBlocked;
    private boolean globalRoom;
    private int userReqStatus;
    private boolean requestedByMe;

    public boolean isGetNotification() {
        return getNotification;
    }

    public void setGetNotification(boolean getNotification) {
        this.getNotification = getNotification;
    }

    private boolean getNotification;

    public boolean isRequestedByMe() {
        return requestedByMe;
    }

    public void setRequestedByMe(boolean requestedByMe) {
        this.requestedByMe = requestedByMe;
    }

    public int getUserReqStatus() {
        return userReqStatus;
    }

    public String getRoomDesc() {
        return roomDesc;
    }

    public void setUserReqStatus(int userReqStatus) {
        this.userReqStatus = userReqStatus;
    }

    private String roomDesc;

    private boolean isBlockedAlertShownBefore;

    public boolean isBlockedAlertShownBefore() {
        return isBlockedAlertShownBefore;
    }

    public void setBlockedAlertShownBefore(boolean blockedAlertShownBefore) {
        isBlockedAlertShownBefore = blockedAlertShownBefore;
    }

    public ArrayList<User> getRandomUsers() {
        return randomUsers;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public ArrayList<User> getAllAdmins() {
        return allAdmins;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public boolean isMemberCanPost() {
        return memberCanPost;
    }

    public boolean isPrimaryAdmin() {
        return primaryAdmin;
    }

    public boolean isGlobalRoom() {
        return globalRoom;
    }

    public void setGlobalRoom(boolean globalRoom) {
        this.globalRoom = globalRoom;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public int getRoomType() {
        return roomType;
    }

    public String getStatus() {
        return status;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }


    public Room(String roomName, boolean favourite, boolean isUnread, boolean isPinned) {
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.membersCount = membersCount;
        this.roomId = roomId;
        this._public = _public;
        this.roomName = roomName;
        this.roomUrl = roomUrl;
        this.favourite = favourite;
        this.isUnread = isUnread;
        this.isPinned = isPinned;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreateByWithAtSign() {
        if (!StringUtils.isNullOrEmpty(createdBy)) {
            String handle = createdBy.startsWith("@") ? createdBy : "@" + createdBy;
            return handle.replaceAll("\\s", "");
        }
        return createdBy;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
        notifyPropertyChanged(BR.roomName);
    }

    public int getMembersCount() {
        return membersCount;
    }

    public int getRoomId() {
        return roomId;
    }

    public boolean is_public() {
        return _public;
    }

    @Bindable
    public String getRoomName() {
        return roomName;
    }

    public String getRoomUrl() {
        return roomUrl;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public boolean isPinned() {
        return isPinned;
    }


    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isUserAdmin() {
        return userAdmin;
    }

    public boolean isUserBlocked() {
        return userBlocked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.allAdmins);
        dest.writeTypedList(this.randomUsers);
        dest.writeString(this.createdAt);
        dest.writeString(this.createdBy);
        dest.writeInt(this.membersCount);
        dest.writeInt(this.roomId);
        dest.writeByte(this._public ? (byte) 1 : (byte) 0);
        dest.writeString(this.roomName);
        dest.writeString(this.roomUrl);
        dest.writeByte(this.memberCanPost ? (byte) 1 : (byte) 0);
        dest.writeByte(this.primaryAdmin ? (byte) 1 : (byte) 0);
        dest.writeByte(this.searchable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.roomType);
        dest.writeString(this.status);
        dest.writeTypedList(this.tags);
        dest.writeByte(this.favourite ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isUnread ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isPinned ? (byte) 1 : (byte) 0);
        dest.writeByte(this.userAdmin ? (byte) 1 : (byte) 0);
        dest.writeByte(this.userBlocked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.globalRoom ? (byte) 1 : (byte) 0);
        dest.writeInt(this.userReqStatus);
        dest.writeByte(this.requestedByMe ? (byte) 1 : (byte) 0);
        dest.writeByte(this.getNotification ? (byte) 1 : (byte) 0);
        dest.writeString(this.roomDesc);
        dest.writeByte(this.isBlockedAlertShownBefore ? (byte) 1 : (byte) 0);
    }

    protected Room(Parcel in) {
        this.allAdmins = in.createTypedArrayList(User.CREATOR);
        this.randomUsers = in.createTypedArrayList(User.CREATOR);
        this.createdAt = in.readString();
        this.createdBy = in.readString();
        this.membersCount = in.readInt();
        this.roomId = in.readInt();
        this._public = in.readByte() != 0;
        this.roomName = in.readString();
        this.roomUrl = in.readString();
        this.memberCanPost = in.readByte() != 0;
        this.primaryAdmin = in.readByte() != 0;
        this.searchable = in.readByte() != 0;
        this.roomType = in.readInt();
        this.status = in.readString();
        this.tags = in.createTypedArrayList(Tag.CREATOR);
        this.favourite = in.readByte() != 0;
        this.isUnread = in.readByte() != 0;
        this.isPinned = in.readByte() != 0;
        this.userAdmin = in.readByte() != 0;
        this.userBlocked = in.readByte() != 0;
        this.globalRoom = in.readByte() != 0;
        this.userReqStatus = in.readInt();
        this.requestedByMe = in.readByte() != 0;
        this.getNotification = in.readByte() != 0;
        this.roomDesc = in.readString();
        this.isBlockedAlertShownBefore = in.readByte() != 0;
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel source) {
            return new Room(source);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };
}
