/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.University;
import com.appster.turtle.model.UserBaseModel;

/**
 * Created by atul on 18/09/17.
 * To inject activity reference.
 */

public class SignUpData {

    private String token;
    private UserBaseModel userBaseModel;
    private University universityBaseModel;

    public String getToken() {
        return token;
    }

    public UserBaseModel getUserBaseModel() {
        return userBaseModel;
    }

    public University getUniversityBaseModel() {
        return universityBaseModel;
    }

}
