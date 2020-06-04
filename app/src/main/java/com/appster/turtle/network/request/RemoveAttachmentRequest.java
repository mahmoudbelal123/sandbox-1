/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.request;

public class RemoveAttachmentRequest {

    int id;
    int notesId;

    public void setId(int id) {
        this.id = id;
    }

    public void setNotesId(int notesId) {
        this.notesId = notesId;
    }
}
