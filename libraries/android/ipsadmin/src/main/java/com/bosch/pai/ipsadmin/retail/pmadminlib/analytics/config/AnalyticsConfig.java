package com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.config;

import android.support.annotation.NonNull;

public class AnalyticsConfig {

    private static final String ALL = "All";
    private static final String COMPANIES = "companies/";
    private static final String STORES = "/stores/";
    private static final String SITES = "/sites";
    private static final String LOCATIONS = "/locations/";
    private static final String LOCATIONS_WO_SLASH = "/locations";

    private AnalyticsConfig() {
        //default constuctor
    }

    @NonNull
    public static String getDwellTimeUrl(String companyId, String storeId, String siteName, String locationName) {
        String dwellTimeUrl = COMPANIES + companyId + STORES + storeId + SITES;
        final String dwellTimeEndPoint = "/DwellTime/";
        if (siteName != null && !siteName.isEmpty()) {
            if (locationName != null && !locationName.isEmpty() && !locationName.equals(ALL)) {
                dwellTimeUrl += "/" + siteName + LOCATIONS + locationName + dwellTimeEndPoint;
            } else {
                dwellTimeUrl += "/" + siteName + LOCATIONS_WO_SLASH + dwellTimeEndPoint;
            }
        } else {
            dwellTimeUrl += dwellTimeEndPoint;
        }
        return dwellTimeUrl;
    }

    public static String getHeatmapEndpoint(String company, String store, String siteName, String locationName) {
        String dwellTimeUrl = COMPANIES + company + STORES + store + SITES;
        final String dwellTimeEndPoint = "/HeatMap/";
        if (siteName != null && !siteName.isEmpty()) {
            if (locationName != null && !locationName.isEmpty() && !locationName.equals(ALL)) {
                dwellTimeUrl += "/" + siteName + LOCATIONS + locationName + dwellTimeEndPoint;
            } else {
                dwellTimeUrl += "/" + siteName + LOCATIONS_WO_SLASH + dwellTimeEndPoint;
            }
        } else {
            dwellTimeUrl += dwellTimeEndPoint;
        }
        return dwellTimeUrl;
    }

    public static String getOfferAnalyticsEndpoint(String company, String store, String siteName, String locationName) {
        String dwellTimeUrl = COMPANIES + company + STORES + store + SITES;
        final String dwellTimeEndPoint = "/OfferAnalytics/";
        if (siteName != null && !siteName.isEmpty()) {
            if (locationName != null && !locationName.isEmpty() && !locationName.equals(ALL)) {
                dwellTimeUrl += "/" + siteName + LOCATIONS + locationName + dwellTimeEndPoint;
            } else {
                dwellTimeUrl += "/" + siteName + LOCATIONS_WO_SLASH + dwellTimeEndPoint;
            }
        } else {
            dwellTimeUrl += dwellTimeEndPoint;
        }
        return dwellTimeUrl;
    }

    public static String getEntryExitEndpoint(String company, String store) {
        final String entryExitUrl = COMPANIES + company + STORES + store;
        final String entryExitEndPoint = "/getEntryExit/";
        return entryExitUrl + entryExitEndPoint;
    }
}
