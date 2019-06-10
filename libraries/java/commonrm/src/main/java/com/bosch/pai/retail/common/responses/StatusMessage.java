package com.bosch.pai.retail.common.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StatusMessage implements Serializable {

    @SerializedName("statusDescription")
    private String statusDescription;
    @SerializedName("status")
    private STATUS status;

    public StatusMessage() {
        //Required by jacksonmessageconverter
        this.status = STATUS.SUCCESS;
        this.statusDescription = STATUS.SUCCESS.getStatusMessage();
    }

    public StatusMessage(STATUS status, String statusDescription) {
        super();
        this.status = status;
        this.statusDescription = statusDescription;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    @Override
    public String toString() {
        return "Status [statusMessage=" + status.getStatusMessage()
                + ", code=" + status.getStatusCode()
                + ", statusDescription=" + statusDescription
                + "]";
    }

    public enum STATUS {
        SUCCESS("000", "SUCCESS"),
        FAILURE("001", "FAILURE"),
        FAILED_TO_FETCH_OFFERS("002", "Failed to fetch offers."),
        FAILED_TO_SEND_OFFER_RESPONSE("005", "Failed to send offer response."),
        FAILED_TO_FETCH_OFFER_ANALYTICS("006", "Failed  to fetch offer analytics."),
        FAILED_TO_FETCH_DWELL_TIME_ANALYTICS("007", "Failed to fetch dwell time details."),
        FAILED_TO_FETCH_HEATMAP("008", "Failed to fetch heatmap details."),
        FAILED_TO_CREATE_OR_UPDATE_SESSION("009", "Failed to create session."),
        AUTHENTICATION_FAILURE("010", "Failed to authenticate."),
        FAILED_TO_SAVE_STORE_LOCATION_MAPPING("011", "Failed to save store location details."),
        FAILED_TO_SAVE_STORE_BEARING_CONFIG("012", "Failed to save bearing configuration."),
        FAILED_TO_FETCH_BEARING_CONFIG("013", "Failed to fetch bearing configuration."),
        INVALID_INPUTS("014", "Invalid Inputs"),
        FAILED_TO_CREATE_COLLECTION("015", "Failed to create a collection"),
        FAILED_TO_SAVE_BEARING_CONFIGURATION("016", "Failed to save Bearing Configuraion details"),
        FAILED_TO_FETCH_ENTRYEXIT("017", "Failed to fetch entry exit details"),
        UNCAUGHT_EXCEPTION("018", "Uncaught Exception"),
        NO_PROMOTIONS_FOR_STORE("019", "Promotions are not yet configured for this store"),
        FAILED_TO_CONNECT_SERVER("020", "Failed to connect server"),
        NO_VALID_PROMOTIONS_FOR_STORE("021", "No valid promotions found for this store"),
        INVALID_WIFI_GPS_CONFIG("022", "Please check wifi/gps settings"),
        BAYMAP_ALREADY_EXIST("023", "baymap already exist"),
        FAILED_TO_UPLOAD_CRASH_REPORTS("012", "Failed to authenticate."),
        LOCATION_LIST_SHOULD_NOT_BE_EMPTY("024", "location list should not be empty");

        private String statusMessage;
        private String statusCode;

        STATUS(String code, String message) {

            this.statusCode = code;
            this.statusMessage = message;

        }

        public String getStatusMessage() {
            return statusMessage;
        }


        public String getStatusCode() {
            return statusCode;
        }

    }

}