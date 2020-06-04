/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.model;

import java.util.List;

/**
 * Created  on 10/10/17 .
 */

public class MessageMeta {

    private String message;

    public void setData(List<Integer> data) {
        this.data = data;
    }

    private List<Integer> data;
    private String type;
    private String url;
    private int profileUrlType;

    public int getProfileUrlType() {
        return profileUrlType;
    }

    public String getMessage() {
        return message;
    }

    public List<Integer> getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}
