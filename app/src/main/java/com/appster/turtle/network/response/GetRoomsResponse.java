/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.Room;
import com.appster.turtle.network.BaseResponse;

import java.util.List;

public class GetRoomsResponse extends BaseResponse {

    private List<Room> data;
    private Pagination pagination;


    public List<Room> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
