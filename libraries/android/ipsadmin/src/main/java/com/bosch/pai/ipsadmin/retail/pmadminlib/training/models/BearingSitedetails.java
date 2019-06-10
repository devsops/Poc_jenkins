package com.bosch.pai.ipsadmin.retail.pmadminlib.training.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sjn8kor on 6/1/2018.
 */

public class BearingSitedetails implements Serializable {

    @SerializedName("siteName")
    private String siteName;

    @SerializedName("wifiLocationNames")
    private Set<String> wifiLocationNames;

    @SerializedName("bleLocationNames")
    private Set<String> bleLocationNames;

    public BearingSitedetails() {
    }

    public BearingSitedetails(String siteName, Set<String> wifiLocationNames, Set<String> bleLocationNames) {
        this.siteName = siteName;
        this.wifiLocationNames = wifiLocationNames != null ? new HashSet<>(wifiLocationNames)
                : new HashSet<>();
        this.bleLocationNames = bleLocationNames != null ? new HashSet<>(bleLocationNames)
                : new HashSet<>();;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Set<String> getWifiLocationNames() {
        return Collections.unmodifiableSet(wifiLocationNames);
    }

    public void setWifiLocationNames(Set<String> wifiLocationNames) {
        this.wifiLocationNames = wifiLocationNames != null ? new HashSet<>(wifiLocationNames)
                : new HashSet<>();
    }

    public Set<String> getBleLocationNames() {
        return Collections.unmodifiableSet(bleLocationNames);
    }

    public void setBleLocationNames(Set<String> bleLocationNames) {
        this.bleLocationNames = bleLocationNames != null ? new HashSet<>(bleLocationNames)
                : new HashSet<>();
    }

    @Override
    public String toString() {
        return "BearingSitedetails{" +
                "siteName='" + siteName + '\'' +
                ", wifiLocationNames=" + wifiLocationNames +
                ", bleLocationNames=" + bleLocationNames +
                '}';
    }
}
