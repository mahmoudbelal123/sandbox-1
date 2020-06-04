package com.appster.turtle.network.response;

import com.appster.turtle.model.MajorModel;
import com.appster.turtle.network.BaseResponse;

import java.util.List;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 08/12/17.
 */

public class GetMajorResponse extends BaseResponse {

    private List<MajorModel> data;
    private Pagination pagination;


    public List<MajorModel> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
