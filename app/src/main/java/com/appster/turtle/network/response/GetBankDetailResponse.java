/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.BankModel;
import com.appster.turtle.network.BaseResponse;

public class GetBankDetailResponse extends BaseResponse {

    private BankModel data;

    public BankModel getData() {
        return data;
    }

    public void setData(BankModel data) {
        this.data = data;
    }
}
