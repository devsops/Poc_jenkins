package com.bosch.pai.retail.analytics.responses;


import com.google.gson.annotations.SerializedName;

public class QuickAssistAnalyticsResponse {

    @SerializedName("quickAssistRequestCount")
    private long quickAssistRequestCount;
    @SerializedName("servedQuickAssistCount")
    private long servedQuickAssistCount;

    public QuickAssistAnalyticsResponse() {
        //sonar
    }

    public long getQuickAssistRequestCount() {
        return quickAssistRequestCount;
    }

    public void setQuickAssistRequestCount(long quickAssistRequestCount) {
        this.quickAssistRequestCount = quickAssistRequestCount;
    }

    public long getServedQuickAssistCount() {
        return servedQuickAssistCount;
    }

    public void setServedQuickAssistCount(long servedQuickAssistCount) {
        this.servedQuickAssistCount = servedQuickAssistCount;
    }

    @Override
    public String toString() {
        return "OfferAnalyticsResponse{" +
                "quickAssistRequestCount=" + quickAssistRequestCount +
                ", servedQuickAssistCount=" + servedQuickAssistCount +
                '}';
    }
}
