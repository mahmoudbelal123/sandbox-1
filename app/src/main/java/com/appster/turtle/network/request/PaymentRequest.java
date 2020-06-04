/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.request;

public class PaymentRequest {

    private String cardId;
    private String customerId;

    public PaymentRequest(String cardId, String customerId) {
        this.cardId = cardId;
        this.customerId = customerId;
    }
}
