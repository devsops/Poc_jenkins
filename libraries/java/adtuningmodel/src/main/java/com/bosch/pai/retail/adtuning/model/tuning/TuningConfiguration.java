package com.bosch.pai.retail.adtuning.model.tuning;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TuningConfiguration {

    @SerializedName("interval")
    private int interval;
    @SerializedName("timeUnit")
    private String timeUnit;
    @SerializedName("isDuplicate")
    private Boolean isDuplicate;
    @SerializedName("adTuningConfig")
    private List<TuningData> adTuningConfig= new ArrayList<>();

    public TuningConfiguration() {
        //sonar
    }

    public TuningConfiguration(int interval, Boolean isDuplicate) {
        this.interval = interval;
        this.isDuplicate = isDuplicate;
    }

    public TuningConfiguration(int interval, String timeUnit, Boolean isDuplicate) {
        this.interval = interval;
        this.timeUnit = timeUnit;
        this.isDuplicate = isDuplicate;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public Boolean getDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(Boolean duplicate) {
        isDuplicate = duplicate;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public List<TuningData> getAdTuningConfig() {
        return new ArrayList<>(adTuningConfig);
    }

    public void setAdTuningConfig(List<TuningData> adTuningConfig) {
        this.adTuningConfig = adTuningConfig != null ? adTuningConfig : new ArrayList<TuningData>() ;
    }

    @Override
    public String toString() {
        return "TuningConfiguration{" +
                "interval=" + interval +
                ", timeUnit=" + timeUnit +
                ", isDuplicate=" + isDuplicate +
                ", adTuningConfig=" + adTuningConfig +
                '}';
    }
}
