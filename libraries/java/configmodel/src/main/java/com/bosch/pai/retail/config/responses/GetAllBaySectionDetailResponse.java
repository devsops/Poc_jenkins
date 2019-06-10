package com.bosch.pai.retail.config.responses;



import com.bosch.pai.retail.common.responses.StatusMessage;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by SJN8KOR on 1/25/2017.
 */
public class GetAllBaySectionDetailResponse {

    @SerializedName("baySectionDetailMap")
    private Map<String, String> baySectionDetailMap;
    @SerializedName("statusMessage")
    private StatusMessage statusMessage;

    public GetAllBaySectionDetailResponse() {
        //Required by jacksonmessageconverter
    }

    public Map<String, String> getBaySectionDetailMap() {
        return baySectionDetailMap;
    }

    public void setBaySectionDetailMap(Map<String, String> baySectionDetailMap) {
        this.baySectionDetailMap = baySectionDetailMap;
    }

    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(StatusMessage statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Override
    public String toString() {
        return "GetAllBaySectionDetailResponse{" +
                "baySectionDetailMap=" + baySectionDetailMap +
                ", statusMessage=" + statusMessage +
                '}';
    }
}
