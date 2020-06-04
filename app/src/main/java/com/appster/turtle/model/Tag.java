package com.appster.turtle.model;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("ALL")
public class Tag implements Parcelable {


    private Integer id;
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

    public Tag() {
    }

    protected Tag(Parcel in) {
        this.id = in.readInt();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel source) {
            return new Tag(source);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    @Override
    public String toString() {
        return value;
    }
}
