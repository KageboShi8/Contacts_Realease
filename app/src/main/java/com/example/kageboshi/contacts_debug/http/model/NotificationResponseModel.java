package com.example.kageboshi.contacts_debug.http.model;

public class NotificationResponseModel {


    /**
     * code : 0
     * message : 你好，app!
     */

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
