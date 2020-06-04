package com.appster.turtle.network.response;

import com.appster.turtle.model.GreekLife;
import com.appster.turtle.network.BaseResponse;

import java.util.List;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 11/12/17.
 */

public class GetGreekLifeResponse extends BaseResponse {

    private List<GreekLife> data;
    private Pagination pagination;


    public List<GreekLife> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }
}