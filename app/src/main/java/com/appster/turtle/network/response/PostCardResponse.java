/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.CardModel;
import com.appster.turtle.network.BaseResponse;

public class PostCardResponse extends BaseResponse {

    private CardModel data;

    public CardModel getData() {
        return data;
    }
}
