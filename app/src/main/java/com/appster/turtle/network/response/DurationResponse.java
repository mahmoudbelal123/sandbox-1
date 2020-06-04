/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.Duration;
import com.appster.turtle.network.BaseResponse;

import java.util.List;

/**
 * Created by rohantaneja on 04-Sep-2017
 */

public class DurationResponse extends BaseResponse {

    private List<Duration> data;
    private Pagination pagination;

    public List<Duration> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }

}
