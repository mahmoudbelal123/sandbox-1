/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.NotesModel;
import com.appster.turtle.network.BaseResponse;

import java.util.List;

public class NotesResponse extends BaseResponse {

    private List<NotesModel> data;

    public List<NotesModel> getData() {
        return data;
    }

    private Pagination pagination;

    public Pagination getPagination() {
        return pagination;
    }
}
