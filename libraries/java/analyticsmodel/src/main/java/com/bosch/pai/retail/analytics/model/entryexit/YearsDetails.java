package com.bosch.pai.retail.analytics.model.entryexit;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.annotations.SerializedName;

@JsonInclude(Include.NON_NULL)
public class YearsDetails implements EntryExit {

    @SerializedName("year")
    private Integer year;

    @SerializedName("entryCount")
    private Long entryCount;

    @SerializedName("exitCount")
    private Long exitCount;

    @SerializedName("months")
    private List<MonthsDetails> months = new ArrayList<>();

    public YearsDetails() {
        //sonar
    }

    public YearsDetails(Integer year, Long entryCount, Long exitCount, List<MonthsDetails> months) {
        this.year = year;
        this.entryCount = entryCount;
        this.exitCount = exitCount;
        this.months = months != null ? months : new ArrayList<MonthsDetails>();
    }

    @Override
    public Long entryCount() {

        if (months != null && !months.isEmpty()) {
            entryCount = 0L;
            for (MonthsDetails monthsDetails :
                    months) {
                final Long localEntryCount = monthsDetails.entryCount();
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
        if (months != null && !months.isEmpty()) {
            exitCount = 0L;
            for (MonthsDetails monthsDetails :
                    months) {
                final Long localExitCount = monthsDetails.exitCount();
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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setEntryCount(Long entryCount) {
        this.entryCount = entryCount;
    }

    public void setExitCount(Long exitCount) {
        this.exitCount = exitCount;
    }

    public List<MonthsDetails> getMonths() {
        return new ArrayList<>(months);
    }

    public void setMonths(List<MonthsDetails> months) {
        this.months = months != null ? months : new ArrayList<MonthsDetails>();
    }

    @Override
    public String toString() {
        return "YearsDetails{" +
                "year=" + year +
                ", yearlyEntryCount=" + entryCount() +
                ", yearlyExitCount=" + exitCount() +
                ", months=" + months +
                '}';
    }


}
