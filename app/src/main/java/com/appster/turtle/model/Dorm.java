package com.appster.turtle.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 11/12/17.
 */

public class Dorm implements Parcelable {


    private String address;
    private int dormId;
    private double lat;
    private double lon;
    private int noOfBeds;
    private String title;

    public String getAddress() {
        return address;
    }

    public int getDormId() {
        return dormId;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getNoOfBeds() {
        return noOfBeds;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Dorm)
            return ((Dorm) obj).getDormId() == dormId;
        return false;

    }

    @Override
    public int hashCode() {
        return dormId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeInt(this.dormId);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
        dest.writeInt(this.noOfBeds);
        dest.writeString(this.title);
    }

    public Dorm() {
    }

    protected Dorm(Parcel in) {
        this.address = in.readString();
        this.dormId = in.readInt();
        this.lat = in.readDouble();
        this.lon = in.readDouble();
        this.noOfBeds = in.readInt();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<Dorm> CREATOR = new Parcelable.Creator<Dorm>() {
        @Override
        public Dorm createFromParcel(Parcel source) {
            return new Dorm(source);
        }

        @Override
        public Dorm[] newArray(int size) {
            return new Dorm[size];
        }
    };
}
