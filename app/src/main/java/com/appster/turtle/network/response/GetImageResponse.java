/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.AvatarModel;
import com.appster.turtle.network.BaseResponse;

import java.util.ArrayList;

public class GetImageResponse extends BaseResponse {


    private ArrayList<AvatarModel> data;

    public ArrayList<AvatarModel> getData() {
        return data;
    }

    public void setData(ArrayList<AvatarModel> data) {
        this.data = data;
    }
}
