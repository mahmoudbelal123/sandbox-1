package com.appster.turtle.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.appster.turtle.util.Utils;

import java.util.ArrayList;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 22/03/18.
 */

public class Comment implements Parcelable {

    private String comment;
    private int commentId;
    private boolean commentedByMe;
    private String createdAt;
    private User createdBy;
    private boolean flaggedByMe;
    private int postId;
    private int reaction;
    private ArrayList<User> taggedUsers;
    private ArrayList<Tag> tags;
    private int totalLikesCount;


    protected Comment(Parcel in) {
        comment = in.readString();
        commentId = in.readInt();
        commentedByMe = in.readByte() != 0;
        createdAt = in.readString();
        createdBy = in.readParcelable(User.class.getClassLoader());
        flaggedByMe = in.readByte() != 0;
        postId = in.readInt();
        reaction = in.readInt();
        taggedUsers = in.createTypedArrayList(User.CREATOR);
        tags = in.createTypedArrayList(Tag.CREATOR);
        totalLikesCount = in.readInt();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public int getCommentId() {
        return commentId;
    }

    public boolean isCommentedByMe() {
        return commentedByMe;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public boolean isFlaggedByMe() {
        return flaggedByMe;
    }

    public int getPostId() {
        return postId;
    }

    public int getReaction() {
        return reaction;
    }

    public ArrayList<User> getTaggedUsers() {
        return taggedUsers;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public int getTotalLikesCount() {
        return totalLikesCount;
    }

    public String getComment() {
        return comment;
    }

    public void setFlaggedByMe(boolean flaggedByMe) {
        this.flaggedByMe = flaggedByMe;
    }

    public String getFormatedPostDate() {
        return Utils.changeTime(createdAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(comment);
        dest.writeInt(commentId);
        dest.writeByte((byte) (commentedByMe ? 1 : 0));
        dest.writeString(createdAt);
        dest.writeParcelable(createdBy, flags);
        dest.writeByte((byte) (flaggedByMe ? 1 : 0));
        dest.writeInt(postId);
        dest.writeInt(reaction);
        dest.writeTypedList(taggedUsers);
        dest.writeTypedList(tags);
        dest.writeInt(totalLikesCount);
    }
}
