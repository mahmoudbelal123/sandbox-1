/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.Interest;
import com.appster.turtle.network.BaseResponse;

import java.util.ArrayList;

public class InterestResponse extends BaseResponse {

    private ArrayList<Interest> data;

    public ArrayList<Interest> getData() {
        return data;
    }
}
