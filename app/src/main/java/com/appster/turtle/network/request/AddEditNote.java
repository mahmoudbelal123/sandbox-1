/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.request;

public class AddEditNote {

    private String className;
    private String details;
    private Integer id;
    private Double price;
    private String title;

    public void setClassName(String className) {
        this.className = className;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
