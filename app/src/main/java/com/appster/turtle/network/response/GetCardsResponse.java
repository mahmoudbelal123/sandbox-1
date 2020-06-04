/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.model.CardModel;
import com.appster.turtle.network.BaseResponse;

import java.util.ArrayList;

public class GetCardsResponse extends BaseResponse {


    private ArrayList<Data> data;
    private Pagination pagination;

    public ArrayList<Data> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public class Data {

        private ArrayList<CardModel> cardModels;
        private String customerId;

        public ArrayList<CardModel> getData() {
            return cardModels;
        }

        public void setData(ArrayList<CardModel> cardModels) {
            this.cardModels = cardModels;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }
    }
}
