/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.appster.turtle.model.User;

/**
 * Created  on 27/09/17 .
 */

public class NotesReview implements Parcelable {


    private String createdAt;
    private String details;
    private int id;
    private int notesId;
    private String profileUrl;
    private int profileUrlType;
    private int reviewByUserId;
    private float rating;
    private String userName;

    public User getUsersListModel() {
        return usersListModel;
    }

    public int getReviewByUserId() {
        return reviewByUserId;
    }

    public void setReviewByUserId(int reviewByUserId) {
        this.reviewByUserId = reviewByUserId;
    }

    public void setUsersListModel(User usersListModel) {
        this.usersListModel = usersListModel;
    }

    private User usersListModel;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNotesId() {
        return notesId;
    }

    public void setNotesId(int notesId) {
        this.notesId = notesId;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public int getProfileUrlType() {
        return profileUrlType;
    }

    public void setProfileUrlType(int profileUrlType) {
        this.profileUrlType = profileUrlType;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public NotesReview() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.createdAt);
        dest.writeString(this.details);
        dest.writeInt(this.id);
        dest.writeInt(this.notesId);
        dest.writeString(this.profileUrl);
        dest.writeInt(this.profileUrlType);
        dest.writeInt(this.reviewByUserId);
        dest.writeFloat(this.rating);
        dest.writeString(this.userName);
        dest.writeParcelable(this.usersListModel, flags);
    }

    protected NotesReview(Parcel in) {
        this.createdAt = in.readString();
        this.details = in.readString();
        this.id = in.readInt();
        this.notesId = in.readInt();
        this.profileUrl = in.readString();
        this.profileUrlType = in.readInt();
        this.reviewByUserId = in.readInt();
        this.rating = in.readFloat();
        this.userName = in.readString();
        this.usersListModel = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<NotesReview> CREATOR = new Creator<NotesReview>() {
        @Override
        public NotesReview createFromParcel(Parcel source) {
            return new NotesReview(source);
        }

        @Override
        public NotesReview[] newArray(int size) {
            return new NotesReview[size];
        }
    };
}

