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

public class Room implements Parcelable {
    private int id;
    private String value;

    public void setId(int id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.value);
    }

    public Room() {
    }

    protected Room(Parcel in) {
        this.id = in.readInt();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<Room> CREATOR = new Parcelable.Creator<Room>() {
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
