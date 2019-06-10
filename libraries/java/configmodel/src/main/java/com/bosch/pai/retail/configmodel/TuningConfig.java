package com.bosch.pai.retail.configmodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TuningConfig {

    @SerializedName("duplicateOfferAllowed")
    private boolean duplicateOfferAllowed;

    @SerializedName("timetype")
    private Timetype timetype = Timetype.MINUTES;

    @SerializedName("timeFrequencyList")
    private List<TimeFrequency> timeFrequencyList = new ArrayList<>();

    @SerializedName("locationFrequencyList")
    private List<LocationFrequency> locationFrequencyList = new ArrayList<>();

    public TuningConfig() {
    }


    public Timetype getTimetype() {
        return timetype;
    }

    public void setTimetype(Timetype timetype) {
        this.timetype = timetype;
    }

    public boolean isDuplicateOfferAllowed() {
        return duplicateOfferAllowed;
    }

    public void setDuplicateOfferAllowed(boolean duplicateOfferAllowed) {
        this.duplicateOfferAllowed = duplicateOfferAllowed;
    }

    public List<TimeFrequency> getTimeFrequencyList() {
        return new ArrayList<>(timeFrequencyList);
    }

    public void setTimeFrequencyList(List<TimeFrequency> timeFrequencyList) {
        this.timeFrequencyList = timeFrequencyList != null ? timeFrequencyList : new ArrayList<TimeFrequency>();
    }

    public List<LocationFrequency> getLocationFrequencyList() {
        return new ArrayList<>(locationFrequencyList);
    }

    public void setLocationFrequencyList(List<LocationFrequency> locationFrequencyList) {
        this.locationFrequencyList = locationFrequencyList != null ? locationFrequencyList : new ArrayList<LocationFrequency>();
    }

    @Override
    public String toString() {
        return "TuningConfig{" +
                "duplicateOfferAllowed=" + duplicateOfferAllowed +
                ", timetype=" + timetype +
                ", timeFrequencyList=" + timeFrequencyList +
                ", locationFrequencyList=" + locationFrequencyList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TuningConfig that = (TuningConfig) o;

        if (duplicateOfferAllowed != that.duplicateOfferAllowed) return false;
        if (timetype != that.timetype) return false;
        if (timeFrequencyList != null ? !timeFrequencyList.equals(that.timeFrequencyList) : that.timeFrequencyList != null)
            return false;
        return locationFrequencyList != null ? locationFrequencyList.equals(that.locationFrequencyList) : that.locationFrequencyList == null;
    }

    @Override
    public int hashCode() {
        int result = (duplicateOfferAllowed ? 1 : 0);
        result = 31 * result + (timetype != null ? timetype.hashCode() : 0);
        result = 31 * result + (timeFrequencyList != null ? timeFrequencyList.hashCode() : 0);
        result = 31 * result + (locationFrequencyList != null ? locationFrequencyList.hashCode() : 0);
        return result;
    }
}
