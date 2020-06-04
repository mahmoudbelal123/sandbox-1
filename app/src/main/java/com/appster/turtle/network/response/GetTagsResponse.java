/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.Tag;
import com.appster.turtle.network.BaseResponse;

import java.util.List;

public class GetTagsResponse extends BaseResponse {

    private List<Tag> data;
    private Pagination pagination;


    public List<Tag> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }


}
