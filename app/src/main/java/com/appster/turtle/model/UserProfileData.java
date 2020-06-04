package com.appster.turtle.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 01/02/18.
 */

public class UserProfileData implements Parcelable {

    private University universityBaseModel;
    private UserBaseModel userModel;
    private ExtraInfo extraInfo;
    private int friendStatus;
    private boolean flaggedByMe;
    private boolean requestedByMe;

    public boolean isRequestedByMe() {
        return requestedByMe;
    }

    public void setRequestedByMe(boolean requestedByMe) {
        this.requestedByMe = requestedByMe;
    }

    public boolean isFlaggedByMe() {
        return flaggedByMe;
    }

    public void setFlaggedByMe(boolean flaggedByMe) {
        this.flaggedByMe = flaggedByMe;
    }


    public University getUniversityBaseModel() {
        return universityBaseModel;
    }

    public UserBaseModel getUserModel() {
        return userModel;
    }

    public ExtraInfo getExtraInfo() {
        return extraInfo;
    }

    public int getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(int friendStatus) {
        this.friendStatus = friendStatus;
    }

    public UserProfileData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.universityBaseModel, flags);
        dest.writeParcelable(this.userModel, flags);
        dest.writeParcelable(this.extraInfo, flags);
        dest.writeInt(this.friendStatus);
        dest.writeByte(this.flaggedByMe ? (byte) 1 : (byte) 0);
        dest.writeByte(this.requestedByMe ? (byte) 1 : (byte) 0);
    }

    protected UserProfileData(Parcel in) {
        this.universityBaseModel = in.readParcelable(University.class.getClassLoader());
        this.userModel = in.readParcelable(UserBaseModel.class.getClassLoader());
        this.extraInfo = in.readParcelable(ExtraInfo.class.getClassLoader());
        this.friendStatus = in.readInt();
        this.flaggedByMe = in.readByte() != 0;
        this.requestedByMe = in.readByte() != 0;
    }

    public static final Creator<UserProfileData> CREATOR = new Creator<UserProfileData>() {
        @Override
        public UserProfileData createFromParcel(Parcel source) {
            return new UserProfileData(source);
        }

        @Override
        public UserProfileData[] newArray(int size) {
            return new UserProfileData[size];
        }
    };
}
