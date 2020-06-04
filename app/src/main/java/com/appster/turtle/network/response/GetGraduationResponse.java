package com.appster.turtle.network.response;

import com.appster.turtle.model.GraduationYear;
import com.appster.turtle.network.BaseResponse;

import java.util.List;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 12/12/17.
 */

public class GetGraduationResponse extends BaseResponse {
    private List<GraduationYear> data;
    private Pagination pagination;


    public List<GraduationYear> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }


}
