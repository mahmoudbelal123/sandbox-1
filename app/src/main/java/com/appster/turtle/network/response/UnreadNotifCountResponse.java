/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.network.BaseResponse;

/**
 * Created  on 10/10/17 .
 */

public class UnreadNotifCountResponse extends BaseResponse {

    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {

        public void setCount(int count) {
            this.count = count;
        }

        private int count;

        public int getCount() {
            return count;
        }
    }

}
