/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.Attachment;
import com.appster.turtle.network.BaseResponse;

public class AttachmentResponse extends BaseResponse {

    private Attachment data;

    public Attachment getData() {
        return data;
    }

}
