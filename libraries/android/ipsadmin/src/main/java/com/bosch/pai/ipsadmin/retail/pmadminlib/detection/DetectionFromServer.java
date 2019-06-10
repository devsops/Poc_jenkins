package com.bosch.pai.ipsadmin.retail.pmadminlib.detection;

import android.content.Context;

import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.callback.IBearingDetectionCallback;

import java.io.InputStream;

/**
 * Created by sjn8kor on 5/28/2018.
 */

public interface DetectionFromServer {

    void setServerEndPoint(String baseUrl, InputStream certificateStream, IBearingDetectionCallback.ISetBearingServerEndpointForDetection listener);

    void storeBearingData(boolean trueForExternalFalseForInternal, Context context);

    void startSiteDetection(IBearingDetectionCallback.IBearingStartSiteDetectionListener listener);

    void stopSiteDetection(IBearingDetectionCallback.IBearingStopSiteDetectionListener listener);

    void startDetection(IBearingDetectionCallback.IBearingStartLocationDetectionListener listener);

    void stopDetection();

}
