/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.network.BaseResponse;

public class BaseMessageResponse extends BaseResponse {

    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {

        private String message;
        private boolean success;

        public String getMessage() {
            return message;
        }


        public boolean isSuccess() {
            return success;
        }
    }
}
