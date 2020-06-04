package com.appster.turtle.network.response;

import com.appster.turtle.model.Comment;
import com.appster.turtle.network.BaseResponse;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 22/03/18.
 */

public class GetCommentResponse extends BaseResponse {

    private Comment data;

    public Comment getData() {
        return data;
    }
}
