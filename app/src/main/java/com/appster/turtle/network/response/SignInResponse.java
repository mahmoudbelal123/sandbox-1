/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.University;
import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.network.BaseResponse;

public class SignInResponse extends BaseResponse {

    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {

        private String token;
        private UserBaseModel userBaseModel;
        private University universityBaseModel;

        public String getToken() {
            return token;
        }

        public University getUniversityBaseModel() {
            return universityBaseModel;
        }

        public UserBaseModel getUserBaseModel() {
            return userBaseModel;
        }


    }
}
