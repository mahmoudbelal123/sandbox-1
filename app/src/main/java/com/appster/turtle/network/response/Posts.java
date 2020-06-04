/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.model.Tag;
import com.appster.turtle.model.User;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by atul on 04/09/17.
 * To inject activity reference.
 */

public class Posts implements Parcelable {

    private int postId;
    private Room rooms;
    private int postType;
    private int likesCount;
    private List<Tag> tags;
    private String caption;
    private int likeStatus = -1;

    public void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }


    private String postDate;
    private int commentsCount;
    private boolean activeFlag;
    private boolean postedByMe;
    private ArrayList<User> taggedUsers;


    public ArrayList<User> getTaggedUsers() {
        return taggedUsers;
    }

    public boolean isPostedByMe() {
        return postedByMe;
    }

    public void setPostedByMe(boolean postedByMe) {
        this.postedByMe = postedByMe;
    }


    public boolean isFlaggedByMe() {
        return flaggedByMe;
    }

    public void setFlaggedByMe(boolean flaggedByMe) {
        this.flaggedByMe = flaggedByMe;
    }

    private boolean flaggedByMe;
    private User usersListModel;
    private PostDetail postDetail;


    public int getPostId() {
        return postId;
    }

    public Room getRooms() {
        return rooms;
    }

    public int getPostType() {
        return postType;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getCaption() {
        return caption;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public String getPostDate() {
        return postDate;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public boolean isActiveFlag() {
        return activeFlag;
    }

    public User getUsersListModel() {
        return usersListModel;
    }

    public PostDetail getPostDetail() {
        return postDetail;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getFormatedPostDate() {
        return Utils.changeTime(postDate);
    }

    @Override
    public int hashCode() {
        return postId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Posts) {

            return postId == ((Posts) obj).getPostId();
        }
        return false;
    }

    public Posts() {
    }

    public String getFormattedTags() {
        StringBuilder tagsDisplayString = new StringBuilder();

        for (int index = 0; index < tags.size(); index++) {
            tagsDisplayString.append(ApplicationController.getAppContext().getString(R.string.hash)).append(tags.get(index).getValue()).append(ApplicationController.getAppContext().getString(R.string.space));
        }

        return tagsDisplayString.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.postId);
        dest.writeParcelable(this.rooms, flags);
        dest.writeInt(this.postType);
        dest.writeInt(this.likesCount);
        dest.writeTypedList(this.tags);
        dest.writeString(this.caption);
        dest.writeInt(this.likeStatus);
        dest.writeString(this.postDate);
        dest.writeInt(this.commentsCount);
        dest.writeByte(this.activeFlag ? (byte) 1 : (byte) 0);
        dest.writeByte(this.postedByMe ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.taggedUsers);
        dest.writeByte(this.flaggedByMe ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.usersListModel, flags);
        dest.writeParcelable(this.postDetail, flags);
    }

    protected Posts(Parcel in) {
        this.postId = in.readInt();
        this.rooms = in.readParcelable(Room.class.getClassLoader());
        this.postType = in.readInt();
        this.likesCount = in.readInt();
        this.tags = in.createTypedArrayList(Tag.CREATOR);
        this.caption = in.readString();
        this.likeStatus = in.readInt();
        this.postDate = in.readString();
        this.commentsCount = in.readInt();
        this.activeFlag = in.readByte() != 0;
        this.postedByMe = in.readByte() != 0;
        this.taggedUsers = in.createTypedArrayList(User.CREATOR);
        this.flaggedByMe = in.readByte() != 0;
        this.usersListModel = in.readParcelable(User.class.getClassLoader());
        this.postDetail = in.readParcelable(PostDetail.class.getClassLoader());
    }

    public static final Creator<Posts> CREATOR = new Creator<Posts>() {
        @Override
        public Posts createFromParcel(Parcel source) {
            return new Posts(source);
        }

        @Override
        public Posts[] newArray(int size) {
            return new Posts[size];
        }
    };
}
