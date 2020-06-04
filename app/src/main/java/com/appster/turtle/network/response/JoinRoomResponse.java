package com.appster.turtle.network.response;

import com.appster.turtle.network.BaseResponse;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 20/02/18.
 */

public class JoinRoomResponse extends BaseResponse {

    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {
        private int userReqStatus;

        public int getUserReqStatus() {
            return userReqStatus;
        }
    }
}
