/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.model;

import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by atul on 15/09/17.
 * To inject activity reference.
 */

public class SignUp extends BaseObservable implements Parcelable {

    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private String password;
    private String confirmPassword;
    private String deviceToken;
    private int deviceType;

    public SignUp() {
//
    }

    public static final Creator<SignUp> CREATOR = new Creator<SignUp>() {
        @Override
        public SignUp createFromParcel(Parcel in) {
            SignUp signUp = new SignUp();
            signUp.firstName = in.readString();
            signUp.lastName = in.readString();
            signUp.email = in.readString();
            signUp.userName = in.readString();
            signUp.password = in.readString();
            signUp.confirmPassword = in.readString();
            signUp.deviceToken = in.readString();
            signUp.deviceType = in.readInt();
            return signUp;
        }

        @Override
        public SignUp[] newArray(int size) {
            return new SignUp[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    private String getDeviceToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

    public void setDeviceToken() {
        this.deviceToken = getDeviceToken();
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(email);
        parcel.writeString(userName);
        parcel.writeString(password);
        parcel.writeString(confirmPassword);
        parcel.writeString(deviceToken);
        parcel.writeInt(deviceType);
    }
}
