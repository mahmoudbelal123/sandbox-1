/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.Interest;
import com.appster.turtle.network.BaseResponse;

public class AddInterestResponse extends BaseResponse {

    private Interest data;

    public Interest getData() {
        return data;
    }
}
