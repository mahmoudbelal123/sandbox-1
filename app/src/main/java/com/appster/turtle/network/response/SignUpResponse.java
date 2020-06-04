/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.network.BaseResponse;

/**
 * Created by atul on 18/09/17.
 * To inject activity reference.
 */

public class SignUpResponse extends BaseResponse {

    private SignUpData data;

    public SignUpData getData() {
        return data;
    }
}
