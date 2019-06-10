package com.bosch.pai.ipsadmin.retail.pmadminlib.detection;

import android.content.Context;

import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.callback.IBearingDetectionCallback;

import java.io.InputStream;


public interface DetectionFromLocal {

    void setServerEndPoint(String baseUrl, InputStream certificateStream, IBearingDetectionCallback.ISetBearingServerEndpointForDetection listener);

    void setDetectionMode(DetectionMode detectionMode);

    void storeBearingData(boolean trueForExternalFalseForInternal, Context context);

    void startSiteDetection(IBearingDetectionCallback.IBearingStartSiteDetectionListener listener);

    void stopSiteDetection(IBearingDetectionCallback.IBearingStopSiteDetectionListener listener);

    void startLocationDetection(IBearingDetectionCallback.IBearingStartLocationDetectionListener listener);

    void stopDetection(IBearingDetectionCallback.IBearingStopLocationDetectionListener listener);

    void shutdownDetection();
}
