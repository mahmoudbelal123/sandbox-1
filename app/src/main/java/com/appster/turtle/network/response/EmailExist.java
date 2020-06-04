/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.University;

/**
 * Created by atul on 15/09/17.
 * To inject activity reference.
 */

public class EmailExist {

    private boolean success;
    private String message;
    private University universityBaseModel;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public University getUniversityBaseModel() {
        return universityBaseModel;
    }

}
