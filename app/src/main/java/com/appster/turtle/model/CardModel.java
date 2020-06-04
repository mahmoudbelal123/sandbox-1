/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CardModel implements Parcelable {


    private String brand;
    private String cardId;
    private int expiryMonth;
    private int expiryYear;
    private String last4digit;
    private String cardname;


    public String getCardname() {
        return cardname;
    }

    public void setCardname(String cardname) {
        this.cardname = cardname;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(int expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(int expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getLast4digit() {
        return last4digit;
    }

    public void setLast4digit(String last4digit) {
        this.last4digit = last4digit;
    }

    public CardModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.brand);
        dest.writeString(this.cardId);
        dest.writeInt(this.expiryMonth);
        dest.writeInt(this.expiryYear);
        dest.writeString(this.last4digit);
        dest.writeString(this.cardname);
    }

    protected CardModel(Parcel in) {
        this.brand = in.readString();
        this.cardId = in.readString();
        this.expiryMonth = in.readInt();
        this.expiryYear = in.readInt();
        this.last4digit = in.readString();
        this.cardname = in.readString();
    }

    public static final Creator<CardModel> CREATOR = new Creator<CardModel>() {
        @Override
        public CardModel createFromParcel(Parcel source) {
            return new CardModel(source);
        }

        @Override
        public CardModel[] newArray(int size) {
            return new CardModel[size];
        }
    };
}
