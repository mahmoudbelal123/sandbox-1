package com.appster.turtle.model;

import com.appster.turtle.network.BaseResponse;

import java.util.List;

/**
 * Created  on 17/04/18 .
 */

public class PlaceApiModel extends BaseResponse {
    private List<Prediction> predictions = null;
    private String status;

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
