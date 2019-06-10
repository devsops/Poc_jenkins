package com.bosch.pai.detection.models;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sjn8kor on 5/25/2018.
 */

public class StatusMessage {

    @SerializedName("status")
    private STATUS status;

    @SerializedName("message")
    private String message;

    public StatusMessage() {
    }

    public StatusMessage(STATUS status, String statusMessge) {
        this.status = status;
        this.message = statusMessge;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StatusMessage{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }

    public enum STATUS {

        SUCCESS,
        FAILURE
    }
}
