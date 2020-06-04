/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.request.auth;

public class ChangePasswordRequest {

    String oldPass;
    String newPass;
    String confirmPass;

    public ChangePasswordRequest() {
    }

    public void setOldPass(String oldPass) {
        this.oldPass = oldPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }
}
