/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.network.BaseResponse;

import java.util.List;

/**
 * Created  on 12/10/17 .
 */

public class NotesReviewListResponse extends BaseResponse {

    private List<NotesReview> data;
    private Pagination pagination;

    public List<NotesReview> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
