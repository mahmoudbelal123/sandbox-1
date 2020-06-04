package com.appster.turtle.network.response;

import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.network.BaseResponse;

/**
 * Created  on 11/02/18 .
 */

public class ChooseAvatarResponse extends BaseResponse {
    private UserBaseModel data;

    public UserBaseModel getData() {
        return data;
    }

    public void setData(UserBaseModel data) {
        this.data = data;
    }


}
