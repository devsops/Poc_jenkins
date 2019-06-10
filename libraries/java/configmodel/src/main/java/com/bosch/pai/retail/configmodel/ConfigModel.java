package com.bosch.pai.retail.configmodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class ConfigModel implements Serializable{

    @SerializedName("companyId")
    private String companyId;

    @SerializedName("storeId")
    private String storeId;

    @SerializedName("siteName")
    private String siteName;

    @SerializedName("siteConfigMap")
    private Map<String,String> siteConfigMap;

    public ConfigModel() {
        //sonar
    }

    public ConfigModel(String companyId, String storeId, String site, Map<String, String> bearingConfigMap) {
        this.companyId = companyId;
        this.storeId = storeId;
        this.siteName = site;
        this.siteConfigMap = bearingConfigMap;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Map<String, String> getSiteConfigMap() {
        return siteConfigMap;
    }

    public void setSiteConfigMap(Map<String, String> siteConfigMap) {
        this.siteConfigMap = siteConfigMap;
    }

    @Override
    public String toString() {
        return "ConfigModel{" +
                "companyId='" + companyId + '\'' +
                ", storeId='" + storeId + '\'' +
                ", siteName='" + siteName + '\'' +
                ", siteConfigMap=" + siteConfigMap +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigModel that = (ConfigModel) o;

        if (companyId != null ? !companyId.equals(that.companyId) : that.companyId != null)
            return false;
        if (storeId != null ? !storeId.equals(that.storeId) : that.storeId != null) return false;
        if (siteName != null ? !siteName.equals(that.siteName) : that.siteName != null) return false;
        return siteConfigMap != null ? siteConfigMap.equals(that.siteConfigMap) : that.siteConfigMap == null;

    }

    @Override
    public int hashCode() {
        int result = companyId != null ? companyId.hashCode() : 0;
        result = 31 * result + (storeId != null ? storeId.hashCode() : 0);
        result = 31 * result + (siteName != null ? siteName.hashCode() : 0);
        result = 31 * result + (siteConfigMap != null ? siteConfigMap.hashCode() : 0);
        return result;
    }
}
