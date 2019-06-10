package com.bosch.pai.retail.analytics.model.entryexit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.annotations.SerializedName;

@JsonInclude(Include.NON_NULL)
public class HourDetails implements EntryExit {

    @SerializedName("hour")
    private Integer hour;

    @SerializedName("entryCount")
    private Long entryCount;

    @SerializedName("exitCount")
    private Long exitCount;

    public HourDetails() {
        //sonar
    }

    public HourDetails(Integer hour, Long entryCount, Long exitCount) {
        this.hour = hour;
        this.entryCount = entryCount;
        this.exitCount = exitCount;
    }

    public Long getEntryCount() {
        return entryCount;
    }

    public Long getExitCount() {
        return exitCount;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public void setEntryCount(Long entryCount) {
        this.entryCount = entryCount;
    }

    public void setExitCount(Long exitCount) {
        this.exitCount = exitCount;
    }

    @Override
    public String toString() {
        return "HourDetails{" +
                "hour=" + hour +
                ", hourlyEntryCount=" + entryCount() +
                ", hourlyExitCount=" + exitCount() +
                '}';
    }

    @Override
    public Long entryCount() {
        return entryCount;
    }

    @Override
    public Long exitCount() {
        return exitCount;
    }
}