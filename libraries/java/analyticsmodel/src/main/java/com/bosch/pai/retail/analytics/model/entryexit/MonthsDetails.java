package com.bosch.pai.retail.analytics.model.entryexit;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.annotations.SerializedName;

@JsonInclude(Include.NON_NULL)
public class MonthsDetails implements EntryExit {

    @SerializedName("month")
    private Integer month;

    @SerializedName("entryCount")
    private Long entryCount;

    @SerializedName("exitCount")
    private Long exitCount;

    @SerializedName("days")
    private List<DayDetails> days = new ArrayList<>();

    public MonthsDetails() {
        //sonar
    }

    public MonthsDetails(Integer month, Long entryCount, Long exitCount, List<DayDetails> days) {
        this.month = month;
        this.entryCount = entryCount;
        this.exitCount = exitCount;
        this.days = days != null ? days : new ArrayList<DayDetails>();
    }

    @Override
    public Long entryCount() {
        if (days != null && !days.isEmpty()) {
            entryCount = 0L;
            for (DayDetails dayDetails :
                    days) {
                final Long localEntryCount = dayDetails.entryCount();
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

        if (days != null && !days.isEmpty()) {
            exitCount = 0L;
            for (DayDetails dayDetails :
                    days) {
                final Long localExitCount = dayDetails.exitCount();
                if (localExitCount != null) {
                    exitCount += localExitCount;
                }
            }
            return exitCount;
        }
        return exitCount;
    }

    public Long getEntryCount() {
        return entryCount;
    }

    public Long getExitCount() {
        return exitCount;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }


    public void setEntryCount(Long entryCount) {
        this.entryCount = entryCount;
    }

    public void setExitCount(Long exitCount) {
        this.exitCount = exitCount;
    }

    public List<DayDetails> getDays() {
        return new ArrayList<>(days);
    }

    public void setDays(List<DayDetails> days) {
        this.days = days != null ? days : new ArrayList<DayDetails>();
    }

    @Override
    public String toString() {
        return "MonthsDetails{" +
                "month=" + month +
                ", monthlyEntryCount=" + entryCount() +
                ", monthlyExitCount=" + exitCount() +
                ", days=" + days +
                '}';
    }


}