package com.appster.turtle.network.response;

import com.appster.turtle.model.Room;
import com.appster.turtle.network.BaseResponse;

/**
 * Created  on 14/05/18 .
 */

public class RoomResponse extends BaseResponse {
    public Room getData() {
        return data;
    }

    public void setData(Room data) {
        this.data = data;
    }

    private Room data;


}
