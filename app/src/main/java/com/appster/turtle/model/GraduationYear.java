package com.appster.turtle.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 12/12/17.
 */

public class GraduationYear implements Parcelable {

    private int id;
    private String value;
    private boolean isSelected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GraduationYear)
            return ((GraduationYear) obj).getId() == id;
        return false;

    }

    @Override
    public int hashCode() {
        return id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.value);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    public GraduationYear() {
    }

    protected GraduationYear(Parcel in) {
        this.id = in.readInt();
        this.value = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<GraduationYear> CREATOR = new Parcelable.Creator<GraduationYear>() {
        @Override
        public GraduationYear createFromParcel(Parcel source) {
            return new GraduationYear(source);
        }

        @Override
        public GraduationYear[] newArray(int size) {
            return new GraduationYear[size];
        }
    };
}
