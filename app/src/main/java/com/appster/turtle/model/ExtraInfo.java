package com.appster.turtle.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.appster.turtle.util.StringUtils;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 01/02/18.
 */

public class ExtraInfo implements Parcelable {


    private int spaceCount;
    private int connectionCount;
    private int postCount;
    private int mutualFriendCount;


    public String getSpaceCount() {

        return StringUtils.formatLargeInt(spaceCount);
    }

    public int getSpaceCountInt() {
        return spaceCount;
    }

    public int getConnectionCountInt() {
        return connectionCount;
    }

    public String getConnectionCount() {
        return StringUtils.formatLargeInt(connectionCount);
    }

    public String getPostCount() {
        return StringUtils.formatLargeInt(postCount);
    }

    public int getPostCountInt() {
        return postCount;
    }


    public int getMutualFriendCount() {
        return mutualFriendCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.spaceCount);
        dest.writeInt(this.connectionCount);
        dest.writeInt(this.postCount);
        dest.writeInt(this.mutualFriendCount);
    }

    public ExtraInfo() {
    }

    protected ExtraInfo(Parcel in) {
        this.spaceCount = in.readInt();
        this.connectionCount = in.readInt();
        this.postCount = in.readInt();
        this.mutualFriendCount = in.readInt();
    }

    public static final Parcelable.Creator<ExtraInfo> CREATOR = new Parcelable.Creator<ExtraInfo>() {
        @Override
        public ExtraInfo createFromParcel(Parcel source) {
            return new ExtraInfo(source);
        }

        @Override
        public ExtraInfo[] newArray(int size) {
            return new ExtraInfo[size];
        }
    };
}
