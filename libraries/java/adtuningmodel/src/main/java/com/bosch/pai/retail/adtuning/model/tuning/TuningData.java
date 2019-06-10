package com.bosch.pai.retail.adtuning.model.tuning;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chu7kor on 11/22/2017.
 */

public class TuningData {
    @SerializedName("start")
    private int start;
    @SerializedName("end")
    private int end;
    @SerializedName("index")
    private int index;
    @SerializedName("adCount")
    private int adCount;

    public TuningData() {
        //empty constructor for tuning data
    }

    public TuningData(int start, int end, int index, int adCount) {
        this.start = start;
        this.end = end;
        this.index = index;
        this.adCount = adCount;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getAdCount() {
        return adCount;
    }

    public void setAdCount(int adCount) {
        this.adCount = adCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TuningData that = (TuningData) o;

        if (start != that.start) return false;
        if (end != that.end) return false;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + end;
        result = 31 * result + index;
        return result;
    }

    @Override
    public String toString() {
        return "TuningData{" +
                "start=" + start +
                ", end=" + end +
                ", index=" + index +
                ", adCount=" + adCount +
                '}';
    }
}
