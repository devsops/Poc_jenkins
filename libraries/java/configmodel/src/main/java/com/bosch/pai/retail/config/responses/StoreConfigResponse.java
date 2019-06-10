package com.bosch.pai.retail.config.responses;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.configmodel.StoreConfig;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hug5kor on 2/14/2018.
 */

public class StoreConfigResponse {
    @SerializedName("storeConfig")
    private List<StoreConfig> storeConfig= new ArrayList<>();
    @SerializedName("statusMessage")
    private StatusMessage statusMessage;

    public List<StoreConfig> getStoreConfig() {
        return new ArrayList<>(storeConfig);
    }

    public void setStoreConfig(List<StoreConfig> storeConfig) {
        this.storeConfig = storeConfig != null ? storeConfig : new ArrayList<StoreConfig>();
    }

    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(StatusMessage statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Override
    public String toString() {
        return "StoreConfigResponse{" +
                "storeConfig=" + storeConfig +
                ", statusMessage=" + statusMessage +
                '}';
    }
}
