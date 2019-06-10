package com.bosch.pai.retail.analytics.model.entryexit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntryExitDetails implements EntryExit {

    @SerializedName("years")
    private List<YearsDetails> years = new ArrayList<>();;

    public EntryExitDetails() {
        //sonar
    }

    public EntryExitDetails(List<YearsDetails> years) {
        this.years = years != null ? years: new ArrayList<YearsDetails>();
    }

    public List<YearsDetails> getYears() {
        return new ArrayList<>(years);
    }

    public void setYears(List<YearsDetails> years) {
        this.years = years != null ? years : new ArrayList<YearsDetails>();
    }

    public Long getTotalEntryCount() {
        return entryCount();
    }

    public Long getTotalExitCount() {
        return exitCount();
    }

    @Override
    public String toString() {
        return "EntryExitDetails{" +
                "years=" + years +
                "totalEntryCount" + entryCount() +
                "totalExitCount" + exitCount() +
                '}';
    }

    @Override
    public Long entryCount() {

        Long entryCount = 0L;
        if (years != null && !years.isEmpty()) {
            for (YearsDetails yearsDetails :
                    years) {
                final Long localEntryCount = yearsDetails.entryCount();
                if (localEntryCount != null) {
                    entryCount += localEntryCount;
                }
            }
        }
        return entryCount;
    }

    @Override
    public Long exitCount() {
        Long exitCount = 0L;

        if (years != null && !years.isEmpty()) {
            for (YearsDetails yearsDetails :
                    years) {
                final Long localExitCount = yearsDetails.exitCount();
                if (localExitCount != null) {
                    exitCount += localExitCount;
                }
            }
        }
        return exitCount;
    }
}
