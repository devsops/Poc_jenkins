package com.bosch.pai;

import java.util.Map;

public interface IeroIPSPlatformListener {

    void onSuccess(final String message);

    void onFailure(final String failureMessage);

    void onSiteDetected(final String siteName);

    void onLocationDetected(final Map<String, Double> locationToProbabilityMap);
}
