/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.UserAction;
import com.appster.turtle.network.BaseResponse;

/**
 * Created by rohantaneja on 12-Sep-2017
 */

public class UserActionResponse extends BaseResponse {

    private UserAction data;

    public UserAction getData() {
        return data;
    }
}
