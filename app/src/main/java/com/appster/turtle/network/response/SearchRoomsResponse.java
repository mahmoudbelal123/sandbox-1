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
 * Created  on 02/11/17 .
 */

public class SearchRoomsResponse extends BaseResponse {

    private List<SearchRoomsModel> data;
    private Pagination pagination;

    public List<SearchRoomsModel> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }

}
