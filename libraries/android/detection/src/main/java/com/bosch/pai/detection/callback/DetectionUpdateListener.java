package com.bosch.pai.detection.callback;


import com.bosch.pai.detection.models.StatusMessage;

import java.util.Map;

public interface DetectionUpdateListener {

    void onSiteDetected(String siteName);

    void onLocationDetected(Map<String, Double> locationProbabilityMap);

    void onErrorReceived(StatusMessage errorMessage);

}
