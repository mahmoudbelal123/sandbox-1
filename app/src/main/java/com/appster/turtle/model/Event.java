/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.model;

@SuppressWarnings("ALL")
public class Event {
    int eventId;
    String eventMsg;

    public int getEventId() {
        return eventId;
    }

    public String getEventMsg() {
        return eventMsg;
    }

    public Event(int eventId) {
        this.eventId = eventId;
    }

    public Event(int eventId, String eventMsg) {
        this.eventId = eventId;
        this.eventMsg = eventMsg;
    }
}