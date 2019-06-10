package com.bosch.pai.retail.adtuning.model.offer;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by chu7kor on 2/7/2018.
 */

public class OfferResponseStatus implements Serializable {


    @SerializedName("status")
    private STATUS status;

    public OfferResponseStatus(STATUS status) {
        this.status = status;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OfferResponseStatus{" +
                "status=" + status +
                '}';
    }

    public enum STATUS {

        ACCEPTED("accepted"),
        REJECTED("rejected"),
        NO_ACTION("no_action");

        private String status;

        STATUS(String message) {
            this.status = message;

        }

        public String getStatus() {
            return status;
        }

    }

}
