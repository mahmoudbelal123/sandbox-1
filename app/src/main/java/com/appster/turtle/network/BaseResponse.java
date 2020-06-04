package com.appster.turtle.network;

/**
 *
 */
@SuppressWarnings("ALL")
public class BaseResponse {

    private boolean success;
    private boolean isError;
    private Meta meta;


    public Meta getMeta() {
        return meta;
    }

    public boolean isSuccess() {
        return success;
    }

    public BaseResponse() {
        this.isError = isError;
    }

    public BaseResponse(boolean isError) {
        this.isError = isError;
    }

    public boolean isError() {
        return isError;
    }

    public class Meta {

        private String message;
        private int code;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

    }
}
