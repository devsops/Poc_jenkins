package com.bosch.pai.ipsadmin.retail.pmadminlib.analytics;

import android.content.Context;

import com.bosch.pai.retail.analytics.model.entryexit.IntervalDetails;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.callback.IAnalyticsCallbacks;

public interface Analytics {

    void onAuthentication(Context context, String companyName, String userName, String password, String authenticationUrl, IAnalyticsCallbacks.IAuthenticationListener listener);

    void getDwellTimeAnalytics(String company, String store, String siteName, String locationName, Long startTime, Long endTime, IAnalyticsCallbacks.IDwelltimeListener listener);

    void getHeatMapDetails(String company, String store, String siteName, String locationName, Long startTime, Long endTime, IAnalyticsCallbacks.IHeatmapListener listener);

    void getOfferAnalytics(String company, String store, String siteName, String locationName, Long startTime, Long endTime, IAnalyticsCallbacks.IOfferAnalyticstListener listener);

    void getEntryExitDetails(String company, String store, Long startTime, Long endTime,
                             final IntervalDetails requestinterval, IAnalyticsCallbacks.IEntryExitListener listener);

}
