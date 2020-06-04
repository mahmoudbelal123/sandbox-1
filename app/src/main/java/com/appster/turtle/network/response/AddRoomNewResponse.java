package com.appster.turtle.network.response;

import com.appster.turtle.network.BaseResponse;

/**
 * Created  on 22/02/18 .
 */

public class AddRoomNewResponse extends BaseResponse {

    private AddRoomNewResponse.Data data;

    public AddRoomNewResponse.Data getData() {
        return data;
    }

    public class Data {
        private int userReqStatus;

        public int getUserReqStatus() {
            return userReqStatus;
        }

        public void setUserReqStatus(int userReqStatus) {
            this.userReqStatus = userReqStatus;
        }


    }
}
