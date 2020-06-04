package com.appster.turtle.network.request;

import java.util.ArrayList;

/**
 * Created  on 04/05/18 .
 */

public class DeleteNotificationRequest {
    public DeleteNotificationRequest(ArrayList<String> notificationIds) {
        this.notificationIds = notificationIds;
    }

    public ArrayList<String> getNotificationIds() {
        return notificationIds;
    }

    public void setNotificationIds(ArrayList<String> notificationIds) {
        this.notificationIds = notificationIds;
    }

    private ArrayList<String> notificationIds;
}
