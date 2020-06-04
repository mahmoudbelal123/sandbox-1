/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.Room;
import com.appster.turtle.network.BaseResponse;

public class AddRoomResponse extends BaseResponse {

    private Room data;

    public Room getData() {
        return data;
    }
}
