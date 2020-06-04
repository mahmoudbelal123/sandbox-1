package com.appster.turtle.network.response;

import com.appster.turtle.network.BaseResponse;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 18/12/17.
 */

public class FriendRequestResponse extends BaseResponse {

    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {

        private boolean status;
        private int userReqStatus;


        public boolean isStatus() {
            return status;
        }

        public int getUserReqStatus() {
            return userReqStatus;
        }
    }
}
