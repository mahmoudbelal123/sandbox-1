package com.appster.turtle.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 08/12/17.
 */

public class MajorModel implements Parcelable {

    private int id;
    private String value;
    private String type;

    public String getType() {
        return type;
    }

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
        if (obj instanceof MajorModel)
            return ((MajorModel) obj).getId() == id;
        return false;

    }

    @Override
    public int hashCode() {
        return id;
    }


    public MajorModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.value);
        dest.writeString(this.type);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected MajorModel(Parcel in) {
        this.id = in.readInt();
        this.value = in.readString();
        this.type = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<MajorModel> CREATOR = new Creator<MajorModel>() {
        @Override
        public MajorModel createFromParcel(Parcel source) {
            return new MajorModel(source);
        }

        @Override
        public MajorModel[] newArray(int size) {
            return new MajorModel[size];
        }
    };
}
