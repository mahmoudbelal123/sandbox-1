/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.request.auth;


import com.appster.turtle.network.BaseRequest;

/**
 *
 */
public class RefreshTokenRequest extends BaseRequest {

    private int mUserId;
    private String mRefreshToken;
    private String mDeviceType;

    public String getDeviceType() {
        return mDeviceType;
    }

    public void setDeviceType(String deviceType) {
        this.mDeviceType = deviceType;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.mRefreshToken = refreshToken;
    }
}
