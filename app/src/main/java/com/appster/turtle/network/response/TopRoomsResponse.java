/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.SearchRoomsModel;
import com.appster.turtle.network.BaseResponse;

import java.util.List;

/**
 * Created  on 01/11/17 .
 */

public class TopRoomsResponse extends BaseResponse {

    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {

        private List<SearchRoomsModel> popularRooms;

        private List<SearchRoomsModel> newRooms;

        public List<SearchRoomsModel> getPopularRooms() {
            return popularRooms;
        }

        public List<SearchRoomsModel> getNewRooms() {
            return newRooms;
        }

    }
}
