package com.bosch.pai.ipsadmin.retail.pmadminlib.detection.callback;

import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.models.LocationDetectionResponse;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.models.SiteDetectionResponse;

/**
 * Created by sjn8kor on 1/9/2018.
 */

public interface IBearingDetectionCallback {

    @FunctionalInterface
    interface ISetBearingServerEndpointForDetection {

        void status(boolean value);

    }

    interface IBearingStartSiteDetectionListener {

        void onStartSiteDetectionSuccess(SiteDetectionResponse siteDetectionResponse);

        void onStartSiteDetectionFailure(String errorMessage);

    }

    interface IBearingStopSiteDetectionListener {

        void onStopSiteDetectionSuccess();

        void onStopSiteDetectionFailure(String errorMessage);

    }

    interface IBearingStartLocationDetectionListener {

        void onStartLocationDetectionSuccess(LocationDetectionResponse locationDetectionResponse);

        void onStartLocationDetectionFailure(String errorMessage);

    }

    interface IBearingStopLocationDetectionListener {

        void onStopLocationDetectionSuccess();

        void onStopLocationDetectionFailure(String errorMessage);

    }

    interface IDownloadSiteSnapshot {

        void onSuccess();

        void onFailure(String errormessage);

    }
}
