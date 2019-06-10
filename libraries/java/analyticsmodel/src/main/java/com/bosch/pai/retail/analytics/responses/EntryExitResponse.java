package com.bosch.pai.retail.analytics.responses;

import com.bosch.pai.retail.analytics.model.entryexit.EntryExitDetails;
import com.bosch.pai.retail.analytics.model.entryexit.IntervalDetails;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.google.gson.annotations.SerializedName;


public class EntryExitResponse {

    @SerializedName("statusMessage")
    private StatusMessage statusMessage;

    @SerializedName("intervalDetails")
    private IntervalDetails intervalDetails;

    @SerializedName("entryExitDetails")
    private EntryExitDetails entryExitDetails;

    public EntryExitResponse() {
        //sonar
    }

    public EntryExitResponse(IntervalDetails intervalDetails, EntryExitDetails entryExitDetails, StatusMessage statusMessage) {
        this.intervalDetails = intervalDetails;
        this.entryExitDetails = entryExitDetails;
        this.statusMessage = statusMessage;
    }

    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(StatusMessage statusMessage) {
        this.statusMessage = statusMessage;
    }

    public IntervalDetails getIntervalDetails() {
        return intervalDetails;
    }

    public void setIntervalDetails(IntervalDetails intervalDetails) {
        this.intervalDetails = intervalDetails;
    }

    public EntryExitDetails getEntryExitDetails() {
        return entryExitDetails;
    }

    public void setEntryExitDetails(EntryExitDetails entryExitDetails) {
        this.entryExitDetails = entryExitDetails;
    }

    @Override
    public String toString() {
        return "EntryExitResponse{" +

                "statusMessage=" + statusMessage +
                ", intervalDetails=" + intervalDetails +
                ", entryExitDetails=" + entryExitDetails +
                '}';
    }
}
