/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by atul on 04/09/17.
 * To inject activity reference.
 */

public class Media implements Parcelable {

    private String media;
    private String caption;
    private int mediaType;
    private String videoThumbUrl;
    private int mediaId;


    public int getMediaId() {
        return mediaId;
    }

    public String getMedia() {
        return media;
    }

    public int getMediaType() {
        return mediaType;
    }

    public String getCaption() {
        return caption;
    }

    public String getVideoThumbUrl() {
        return videoThumbUrl;
    }

    public Media() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.media);
        dest.writeString(this.caption);
        dest.writeInt(this.mediaType);
        dest.writeString(this.videoThumbUrl);
        dest.writeInt(this.mediaId);
    }

    protected Media(Parcel in) {
        this.media = in.readString();
        this.caption = in.readString();
        this.mediaType = in.readInt();
        this.videoThumbUrl = in.readString();
        this.mediaId = in.readInt();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
