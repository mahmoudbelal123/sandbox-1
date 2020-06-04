/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.NotesModel;
import com.appster.turtle.network.BaseResponse;

public class NoteResponse extends BaseResponse {

    private NotesModel data;

    public NotesModel getData() {
        return data;
    }
}
