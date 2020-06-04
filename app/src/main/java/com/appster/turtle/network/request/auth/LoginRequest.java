/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.request.auth;

/**
 *
 */
public class LoginRequest {

    private String mEmailId;
    private String mPassword;
    private String mDeviceType;

    public String getEmailId() {
        return mEmailId;
    }

    public void setEmailId(String emailId) {
        this.mEmailId = emailId;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public String getDeviceType() {
        return mDeviceType;
    }

    public void setDeviceType(String deviceType) {
        this.mDeviceType = deviceType;
    }
}
