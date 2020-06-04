/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.Activities;
import com.appster.turtle.network.BaseResponse;

import java.util.ArrayList;

public class GetActivitiesResponse extends BaseResponse {

    private ArrayList<Activities> data;
    private Pagination pagination;

    public ArrayList<Activities> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
