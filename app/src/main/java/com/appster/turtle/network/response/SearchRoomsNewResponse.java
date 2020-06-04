/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.SearchRoomsNewModel;
import com.appster.turtle.network.BaseResponse;

import java.util.List;

/**
 * Created  on 02/11/17 .
 */

public class SearchRoomsNewResponse extends BaseResponse {

    private List<SearchRoomsNewModel> data;
    private Pagination pagination;

    public List<SearchRoomsNewModel> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }

}
