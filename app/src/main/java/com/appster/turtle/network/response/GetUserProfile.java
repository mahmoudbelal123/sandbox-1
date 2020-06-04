package com.appster.turtle.network.response;

import com.appster.turtle.model.UserProfileData;
import com.appster.turtle.network.BaseResponse;


/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 02/02/18.
 */

public class GetUserProfile extends BaseResponse {

    private UserProfileData data;


    public UserProfileData getData() {
        return data;
    }


}
