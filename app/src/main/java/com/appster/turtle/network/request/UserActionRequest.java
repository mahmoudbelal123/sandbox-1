/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.request;

import java.util.List;

/**
 * Created by rohantaneja on 12-Sep-2017
 */

public class UserActionRequest {

    private Integer id;
    private List<Integer> userIds;
    private String reason;


    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setRoomId(Integer id) {
        this.id = id;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }

}
