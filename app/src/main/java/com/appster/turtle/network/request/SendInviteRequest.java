/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.request;

import java.util.ArrayList;

public class SendInviteRequest {

    private ArrayList<Integer> userIds;
    private int id;

    public void setUserIds(ArrayList<Integer> userIds) {
        this.userIds = userIds;
    }

    public void setId(int id) {
        this.id = id;
    }
}
