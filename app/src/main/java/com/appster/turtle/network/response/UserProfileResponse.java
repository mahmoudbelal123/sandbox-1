/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.network.BaseResponse;

public class UserProfileResponse extends BaseResponse {

    private UserBaseModel data;


    public UserBaseModel getData() {
        return data;
    }


}
