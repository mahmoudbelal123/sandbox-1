/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.request.auth;

@SuppressWarnings("ALL")
public class SignInRequest {

    private String deviceToken;
    private int deviceType;
    private String loginId;
    private String password;

    public SignInRequest() {
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
