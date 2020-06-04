/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.network.BaseResponse;

/**
 * Created by atul on 15/09/17.
 * To inject activity reference.
 */

public class IdResponse extends BaseResponse {

    private EmailExist data;

    public EmailExist getData() {
        return data;
    }

}
