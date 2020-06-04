/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.network.BaseResponse;

public class BankVerificationImageResponse extends BaseResponse {

    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {

        private String docId;


        public String getDocId() {
            return docId;
        }
    }
}
