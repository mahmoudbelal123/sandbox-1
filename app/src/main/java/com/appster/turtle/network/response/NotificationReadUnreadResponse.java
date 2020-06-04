package com.appster.turtle.network.response;

import com.appster.turtle.network.BaseResponse;

/**
 * Created  on 12/04/18 .
 */

public class NotificationReadUnreadResponse extends BaseResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        private boolean status;
        private int userReqStatus;

        public int getUserReqStatus() {
            return userReqStatus;
        }

        public void setUserReqStatus(int userReqStatus) {
            this.userReqStatus = userReqStatus;
        }


        public boolean isStatus() {
            return status;
        }
    }
}
