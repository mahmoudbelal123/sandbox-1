/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseResponse;

import java.util.List;

public class SearchPopularUserResponse extends BaseResponse {


    private List<User> data;
    private Pagination pagination;


    public List<User> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
