/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.SearchRoomsNewModel;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseResponse;

import java.util.List;

/**
 */

public class HomeTopRoomsResponse extends BaseResponse {

    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {

        public List<SearchRoomsNewModel> getHotRooms() {
            return hotRooms;
        }

        public void setHotRooms(List<SearchRoomsNewModel> hotRooms) {
            this.hotRooms = hotRooms;
        }

        public List<User> getPopularUsers() {
            return popularUsers;
        }

        public void setPopularUsers(List<User> popularUsers) {
            this.popularUsers = popularUsers;
        }

        private List<SearchRoomsNewModel> hotRooms;

        private List<User> popularUsers;

    }
}
