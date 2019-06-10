package com.bosch.pai.ipsadminapp.constants;

public enum LOGIN_STATUS {

    INVALID_USERNAME("Invalid username"),
    INVALID_PASSWORD("Invalid password"),
    INVALID_COMPANY_NAME("Invalid company name"),
    SUCCESS("Success");

    private String statusMessage;

    LOGIN_STATUS(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public String toString() {
        return "LOGIN_STATUS{" +
                "statusMessage='" + statusMessage + '\'' +
                '}';
    }
}
