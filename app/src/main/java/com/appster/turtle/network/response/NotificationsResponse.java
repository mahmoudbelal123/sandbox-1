/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.Notification;
import com.appster.turtle.network.BaseResponse;

import java.util.List;

/**
 * Created  on 10/10/17 .
 */

public class NotificationsResponse extends BaseResponse {

    private List<Notification> data;
    private Pagination pagination;

    public List<Notification> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }

}
