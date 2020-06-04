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
 * Created by rohantaneja on 05-Sep-2017
 */

public class CreateTextPostResponse extends BaseResponse {

    private List<Posts> data;

    public List<Posts> getData() {
        return data;
    }
}
