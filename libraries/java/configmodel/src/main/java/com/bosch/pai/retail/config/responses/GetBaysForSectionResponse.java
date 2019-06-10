package com.bosch.pai.retail.config.responses;


import com.bosch.pai.retail.common.responses.StatusMessage;
import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.Set;

public class GetBaysForSectionResponse {

    @SerializedName("bays")
    private Set<String> bays = new HashSet<>();
    @SerializedName("statusMessage")
    private StatusMessage statusMessage;


    public GetBaysForSectionResponse() {
        //Required by jacksonmessageconverter
    }

    public Set<String> getBays() {
        return new HashSet<>(bays);
    }

    public void setBays(Set<String> bays) {
        this.bays = bays !=null ? bays : new HashSet<String>();
    }

    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(StatusMessage statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Override
    public String toString() {
        return "GetOfferResponse [bays=" + bays + ", statusMessage="
                + statusMessage + "]";
    }


}
