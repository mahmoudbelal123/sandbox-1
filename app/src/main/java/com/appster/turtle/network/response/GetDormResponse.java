package com.appster.turtle.network.response;

import com.appster.turtle.model.Dorm;
import com.appster.turtle.network.BaseResponse;

import java.util.List;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 11/12/17.
 */

public class GetDormResponse extends BaseResponse {

    private List<Dorm> data;
    private Pagination pagination;


    public List<Dorm> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
