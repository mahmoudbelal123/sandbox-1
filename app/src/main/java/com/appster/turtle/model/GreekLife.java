package com.appster.turtle.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 11/12/17.
 */

public class GreekLife implements Parcelable {

    private int id;
    private String value;
    private String type;
    private ArrayList<String> universities;

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public ArrayList getUniversities() {
        return universities;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GreekLife)
            return ((GreekLife) obj).getId() == id;
        return false;

    }


    @Override
    public int hashCode() {
        return id;
    }


    public GreekLife() {
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
        dest.writeStringList(this.universities);
    }

    protected GreekLife(Parcel in) {
        this.id = in.readInt();
        this.value = in.readString();
        this.type = in.readString();
        this.universities = in.createStringArrayList();
    }

    public static final Parcelable.Creator<GreekLife> CREATOR = new Parcelable.Creator<GreekLife>() {
        @Override
        public GreekLife createFromParcel(Parcel source) {
            return new GreekLife(source);
        }

        @Override
        public GreekLife[] newArray(int size) {
            return new GreekLife[size];
        }
    };
}
