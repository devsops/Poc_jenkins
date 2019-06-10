package com.bosch.pai.retail.configmodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class  HierarchyDetail implements Serializable {

    @SerializedName("hierarchyName")
    private String hierarchyName;

    @SerializedName("hierarchyLevel")
    private int hierarchyLevel;

    @SerializedName("required")
    private boolean required;

    @SerializedName("entries")
    private List<String> entries= new ArrayList<>();

    public String getHierarchyName() {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(int hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<String> getEntries() {
        return new ArrayList<>(entries);
    }

    public void setEntries(List<String> entries) {
        this.entries = entries != null ? entries : new ArrayList<String>();
    }
}
