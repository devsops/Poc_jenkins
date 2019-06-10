package com.bosch.pai.retail.analytics.model.entryexit;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.annotations.SerializedName;

@JsonInclude(Include.NON_NULL)
public class DayDetails implements EntryExit {

    @SerializedName("day")
    // day indicates day of month
    private Integer day;

    @SerializedName("entryCount")
    private Long entryCount;

    @SerializedName("exitCount")
    private Long exitCount;

    @SerializedName("hours")
    private List<HourDetails> hours = new ArrayList<>();

    public DayDetails() {
        //sonar
    }


    public DayDetails(Integer day, Long entryCount, Long exitCount, List<HourDetails> hours) {
        this.day = day;
        this.entryCount = entryCount;
        this.exitCount = exitCount;
        this.hours = hours != null ? hours : new ArrayList<HourDetails>();
    }

    public Integer getDay() {
        return day;
    }

    public Long getEntryCount() {
        return entryCount;
    }

    public Long getExitCount() {
        return exitCount;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public void setEntryCount(Long entryCount) {
        this.entryCount = entryCount;
    }

    public void setExitCount(Long exitCount) {
        this.exitCount = exitCount;
    }

    public List<HourDetails> getHours() {
        return new ArrayList<>(hours);
    }

    public void setHours(List<HourDetails> hours) {
        this.hours = hours !=null ? hours : new ArrayList<HourDetails>();
    }

    @Override
    public String toString() {
        return "DayDetails{" +
                "day=" + day +
                ", dailyEntryCount=" + entryCount() +
                ", dailyExitCount=" + exitCount() +
                ", hours=" + hours +
                '}';
    }

    @Override
    public Long entryCount() {


        if (hours != null && !hours.isEmpty()) {
            entryCount =0L;
            for (HourDetails hourDetails :
                    hours) {
                final Long localEntryCount = hourDetails.entryCount();
                if (localEntryCount != null) {
                    entryCount += localEntryCount;
                }
            }
            return entryCount;
        }
        return entryCount;

    }

    @Override
    public Long exitCount() {

        if (hours != null && !hours.isEmpty()) {
            exitCount = 0L;
            for (HourDetails hourDetails :
                    hours) {
                final Long localExitCount = hourDetails.exitCount();
                if (localExitCount != null) {
                    exitCount += localExitCount;
                }
            }
            return exitCount;
        }
        return exitCount;
    }

}