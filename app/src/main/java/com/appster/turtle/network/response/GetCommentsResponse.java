package com.appster.turtle.network.response;

import com.appster.turtle.model.Comment;
import com.appster.turtle.network.BaseResponse;

import java.util.ArrayList;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 22/03/18.
 */

public class GetCommentsResponse extends BaseResponse {

    private ArrayList<Comment> data;
    private Pagination pagination;

    public ArrayList<Comment> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }


}
