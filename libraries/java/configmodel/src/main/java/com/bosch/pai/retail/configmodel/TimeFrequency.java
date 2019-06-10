package com.bosch.pai.retail.configmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sjn8kor on 8/17/2018.
 */

public class TimeFrequency {

    @SerializedName("startTime")
    private long startTime;
    @SerializedName("endTime")
    private long endTime;
    @SerializedName("maxOfferCount")
    private long maxOfferCount;

    public TimeFrequency() {
    }

    public TimeFrequency(long startTime, long endTime, long maxOfferCount) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxOfferCount = maxOfferCount;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getMaxOfferCount() {
        return maxOfferCount;
    }

    public void setMaxOfferCount(long maxOfferCount) {
        this.maxOfferCount = maxOfferCount;
    }

    @Override
    public String toString() {
        return "TimeFrequency{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", maxOfferCount=" + maxOfferCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeFrequency that = (TimeFrequency) o;

        if (startTime != that.startTime) return false;
        if (endTime != that.endTime) return false;
        return maxOfferCount == that.maxOfferCount;
    }

    @Override
    public int hashCode() {
        int result = (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + (int) (endTime ^ (endTime >>> 32));
        result = 31 * result + (int) (maxOfferCount ^ (maxOfferCount >>> 32));
        return result;
    }
}
