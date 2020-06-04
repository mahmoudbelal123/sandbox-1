package com.appster.turtle.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Interest implements Parcelable {
    private int id;
    private String value;
    private boolean selected;

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

    public boolean isSelected() {

        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Interest() {
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Interest)
            return ((Interest) obj).getId() == id;
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
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    protected Interest(Parcel in) {
        this.id = in.readInt();
        this.value = in.readString();
        this.selected = in.readByte() != 0;
    }

    public static final Creator<Interest> CREATOR = new Creator<Interest>() {
        @Override
        public Interest createFromParcel(Parcel source) {
            return new Interest(source);
        }

        @Override
        public Interest[] newArray(int size) {
            return new Interest[size];
        }
    };
}