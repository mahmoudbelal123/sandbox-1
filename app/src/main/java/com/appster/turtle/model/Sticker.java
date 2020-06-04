/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

public class Sticker extends BaseObservable implements Parcelable {

    private int color;
    private String text;
    private int stickerHash;

    @Bindable
    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Bindable
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public int getStickerHash() {
        return stickerHash;
    }

    public void setStickerHash(int stickerHash) {
        this.stickerHash = stickerHash;
    }

    public Sticker() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.color);
        dest.writeString(this.text);
        dest.writeInt(this.stickerHash);
    }

    protected Sticker(Parcel in) {
        this.color = in.readInt();
        this.text = in.readString();
        this.stickerHash = in.readInt();
    }

    public static final Creator<Sticker> CREATOR = new Creator<Sticker>() {
        @Override
        public Sticker createFromParcel(Parcel source) {
            return new Sticker(source);
        }

        @Override
        public Sticker[] newArray(int size) {
            return new Sticker[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Sticker && getStickerHash() == ((Sticker) obj).getStickerHash();
    }

    @Override
    public int hashCode() {
        return getStickerHash();
    }
}
