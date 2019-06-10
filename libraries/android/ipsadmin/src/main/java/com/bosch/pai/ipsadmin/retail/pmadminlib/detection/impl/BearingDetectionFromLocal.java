package com.bosch.pai.ipsadmin.retail.pmadminlib.detection.impl;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.URLUtil;

import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.config.Sensor;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.datamodel.BearingOutput;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.SiteMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.apimodels.output.body.DetectionDataForApproach;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.StatusCode;
import com.bosch.pai.ipsadmin.bearing.detect.BearingDetector;
import com.bosch.pai.ipsadmin.bearing.detect.errorcode.Constants;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.ipsadmin.retail.pmadminlib.Util;
import com.bosch.pai.ipsadmin.retail.pmadminlib.common.CommonUtil;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.DetectionFromLocal;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.DetectionMode;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.callback.IBearingDetectionCallback;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.config.DetectionConfig;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.models.LocationDetectionResponse;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.models.SiteDetectionResponse;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public final class BearingDetectionFromLocal implements DetectionFromLocal {


    private static final String LOCATION_UNKNOWN = "LOCATION_UNKNOWN";
    private static final int FAILURE = 404;
    private static final int SUCCESS = 200;

    private static final String LOG_TAG = BearingDetectionFromLocal.class.getSimpleName();


    private static DetectionFromLocal bearingDetection;
    private BearingDetector bearingDetector;
    private DetectionConfig detectionConfig;


    private static final String SITE_UNKNOWN = "SITE_UNKNOWN";

    private Context context;
    private String previousSite = "";
    private String locationDetectionStartedSite = "";
    private static final int siteUnknownCallbackCounter = 6;
    private static int siteUnkonwnCounter = 0;

    private boolean isDetectionStarted;


    private BearingDetectionFromLocal(Context appContext, DetectionMode detectionMode) {
        this.context = appContext;
        this.bearingDetector = BearingDetector.getInstance(appContext);
        this.detectionConfig = new DetectionConfig(detectionMode);
    }

    public static synchronized DetectionFromLocal getInstance(Context appContext, DetectionMode detectionMode) {
        if (bearingDetection == null) {
            bearingDetection = new BearingDetectionFromLocal(appContext, detectionMode);
        }
        return bearingDetection;
    }

    private static void setSiteUnkonwnCounter(int siteUnkonwnCounter) {
        BearingDetectionFromLocal.siteUnkonwnCounter = siteUnkonwnCounter;
    }

    @Override
    public void setServerEndPoint(String urlEndPoint, InputStream certificateStream, IBearingDetectionCallback.ISetBearingServerEndpointForDetection listener) {

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    listener.status(false);
                } else if (message.what == SUCCESS) {
                    listener.status(true);
                }
                return false;
            }
        });

        InputStream certificate;
        if (URLUtil.isHttpsUrl(urlEndPoint)) {
            certificate = Util.getCertificate(context);
        } else {
            certificate = null;
        }


        final String serverEP = urlEndPoint + CommonUtil.getBearingServerEndPoint();

        final BearingConfiguration setEndPointConfig =
                new BearingConfiguration(BearingConfiguration.OperationType.SET_SERVER_ENDPOINT, serverEP, certificate);

        BearingOutput setServerIpOutput = null;
        if (bearingDetector != null) {
            setServerIpOutput = bearingDetector.read(setEndPointConfig, null, false, null);
        }

        boolean value = setServerIpOutput != null && setServerIpOutput.getHeader().getStatusCode().equals(StatusCode.OK);
        if (value) {
            handler.sendMessage(handler.obtainMessage(SUCCESS));
        } else {
            handler.sendMessage(handler.obtainMessage(FAILURE));
        }
    }


    @Override
    public void setDetectionMode(DetectionMode detectionMode) {
        this.detectionConfig.setDetectionMode(detectionMode);
    }

    @Override
    public void storeBearingData(boolean trueForExternalFalseForInternal, Context context) {

        this.bearingDetector = BearingDetector.getInstance(context);

        if (trueForExternalFalseForInternal) {
            ConfigurationSettings.saveConfigObject(ConfigurationSettings.getConfiguration().
                    withDataStoragePathPreference(ConfigurationSettings.DataStorePathPreference.EXTERNAL));
        } else {
            ConfigurationSettings.saveConfigObject(ConfigurationSettings.getConfiguration().
                    withDataStoragePathPreference(ConfigurationSettings.DataStorePathPreference.INTERNAL));
        }
    }

    @Override
    public void startSiteDetection(final IBearingDetectionCallback.IBearingStartSiteDetectionListener listener) {

        setSiteUnkonwnCounter(0);
        isDetectionStarted = true;

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onStartSiteDetectionFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    final SiteDetectionResponse siteDetectionResponse = (SiteDetectionResponse) message.obj;
                    final SiteDetectionResponse newSiteDetectionResponse = new SiteDetectionResponse(siteDetectionResponse);
                    listener.onStartSiteDetectionSuccess(newSiteDetectionResponse);
                }
                return false;
            }
        });

        final BearingConfiguration configuration = new BearingConfiguration(
                BearingConfiguration.OperationType.DETECT_SITE, this.detectionConfig.getApproachListMapForDetection());

        bearingDetector.invoke(true, configuration, null, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {

                if (isDetectionStarted) {
                    List<DetectionDataForApproach> locationDetectionOutput = bearingOutput.getBody().getLocationDetectionOutput();
                    Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "location detection output : " + locationDetectionOutput, null);

                    if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {

                        final String siteName = bearingOutput.getBody().getOutput();
                        final String timestamp = bearingOutput.getBody().getTimestamp();
                        final SiteDetectionResponse siteDetectionResponse = new SiteDetectionResponse(siteName, timestamp);

                        Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "Site detected : " + siteName, null);

                        if (!SITE_UNKNOWN.equalsIgnoreCase(siteName)) {
                            setSiteUnkonwnCounter(0);
                            handler.sendMessage(handler.obtainMessage(SUCCESS, siteDetectionResponse));
                        } else {
                            setSiteUnkonwnCounter(siteUnkonwnCounter + 1);

                            if (siteUnkonwnCounter >= siteUnknownCallbackCounter) {
                                handler.sendMessage(handler.obtainMessage(SUCCESS, siteDetectionResponse));
                            }
                        }


                    } else {
                        handler.sendMessage(handler.obtainMessage(FAILURE, bearingOutput.getBody().getErrorMessage()));
                    }
                }
            }
        });

    }

    @Override
    public void stopSiteDetection(final IBearingDetectionCallback.IBearingStopSiteDetectionListener listener) {

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onStopSiteDetectionFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    listener.onStopSiteDetectionSuccess();
                }
                return false;
            }
        });

        final BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_SITE, this.detectionConfig.getApproachListMapForDetection());
        final int wifiStopStatus = bearingDetector.invoke(false, configuration, null, null);
        // Shutdown bearing to clear all running instance cache and process
        bearingDetector.invoke(false,
                new BearingConfiguration(BearingConfiguration.OperationType.STOP_DETECTION), null, new BearingCallBack() {
                    @Override
                    public void onLocationResponse(BearingOutput bearingOutput) {
                        //Not used
                    }
                });

        if (wifiStopStatus == Constants.RESPONSE_OK) {
            handler.sendMessage(handler.obtainMessage(SUCCESS));
        } else {
            handler.sendMessage(handler.obtainMessage(FAILURE, "Site detection stop failed."));
        }

        isDetectionStarted = false;
        setSiteUnkonwnCounter(0);
    }

    @Override
    public void startLocationDetection(final IBearingDetectionCallback.IBearingStartLocationDetectionListener listener) {

        setSiteUnkonwnCounter(0);
        isDetectionStarted = true;

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onStartLocationDetectionFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    final LocationDetectionResponse locationDetectionResponse = (LocationDetectionResponse) message.obj;
                    final LocationDetectionResponse ldr = new LocationDetectionResponse(locationDetectionResponse);
                    listener.onStartLocationDetectionSuccess(ldr);
                }
                return false;
            }
        });


        final BearingConfiguration configuration = new BearingConfiguration(
                BearingConfiguration.OperationType.DETECT_SITE, this.detectionConfig.getApproachListMapForDetection());

        bearingDetector.invoke(true, configuration, null, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {
                if (isDetectionStarted) {
                    if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
                        final String siteName = bearingOutput.getBody().getOutput();

                        Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "Site detected : " + siteName, null);

                        if (!SITE_UNKNOWN.equalsIgnoreCase(siteName)) {
                            setSiteUnkonwnCounter(0);
                            if (!siteName.equals(previousSite)) {

                                previousSite = siteName;

                                if (!siteName.equals(locationDetectionStartedSite)) {

                                    stopLocationDetection();

                                    locationDetectionStartedSite = siteName;

                                    Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "started location detection for site : " + siteName, null);

                                    startLocationDetection(siteName, handler);
                                }
                            }
                        } else {

                            setSiteUnkonwnCounter(siteUnkonwnCounter + 1);

                            if (siteUnkonwnCounter >= siteUnknownCallbackCounter) {
                                stopLocationDetection();
                                locationDetectionStartedSite = "";
                                previousSite = siteName;

                                handler.sendMessage(handler.obtainMessage(SUCCESS, getDefaultLocationDetectionResponse(siteName)));
                            }
                        }
                    } else {
                        handler.sendMessage(handler.obtainMessage(FAILURE, bearingOutput.getBody().getErrorMessage()));
                    }
                }
            }
        });

    }

    @NonNull
    private LocationDetectionResponse getDefaultLocationDetectionResponse(String siteName) {
        final LocationDetectionResponse locationDetectionResponse = new LocationDetectionResponse();
        locationDetectionResponse.setSiteName(siteName);
        final HashMap<String, Double> locationUpdateMap = new HashMap<>();
        locationUpdateMap.put(LOCATION_UNKNOWN, 0.0);
        locationDetectionResponse.setLocationUpdateMap(locationUpdateMap);

        String localTime = getTimestamp();
        locationDetectionResponse.setTimestamp(localTime);
        return locationDetectionResponse;
    }

    private void startLocationDetection(String siteName,
                                        final Handler handler) {


        final BearingConfiguration locConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_LOC,
                this.detectionConfig.getApproachListMapForDetection());
        final SiteMetaData siteMetaData = new SiteMetaData(siteName);
        final BearingData bearingData = new BearingData(siteMetaData);

        Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "Detection triggered : ", null);

        bearingDetector.invoke(true, locConfiguration, bearingData, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput output) {
                if (isDetectionStarted) {

                    Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "Detection output : " + output, null);

                    if (output.getHeader().getStatusCode().equals(StatusCode.OK)) {

                        final List<DetectionDataForApproach> detectionDataForApproaches = output.getBody().getLocationDetectionOutput();

                        Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "location detection output : " + detectionDataForApproaches, null);

                        if (detectionDataForApproaches != null && !detectionDataForApproaches.isEmpty()) {

                            boolean shouldAllowFingerprinting = false;
                            DetectionDataForApproach detectionDataForApproachForFingerprinting = null;

                            LocationDetectionResponse locationDetectionResponse = null;

                            if (detectionDataForApproaches.size() > 1) {
                                for (DetectionDataForApproach detectionDataForApproach : detectionDataForApproaches) {

                                    if (detectionDataForApproach != null) {
                                        final BearingConfiguration.Approach approach = detectionDataForApproach.getApproach();
                                        if (approach != null) {
                                            if (approach.equals(BearingConfiguration.Approach.THRESHOLDING)) {
                                                // proccess further

                                                final Map<String, Double> locationToProbabilityMap = detectionDataForApproach.getCellConfidence();
                                                if (locationToProbabilityMap != null && !locationToProbabilityMap.isEmpty()) {

                                                    boolean found = locationToProbabilityMap.containsKey(LOCATION_UNKNOWN);
                                                    if (found) {
                                                        shouldAllowFingerprinting = true;
                                                    } else {
                                                        final String timestamp = detectionDataForApproach.getLocalTime();

                                                        final Map.Entry<String, Double> next = locationToProbabilityMap.entrySet().iterator().next();

                                                        locationDetectionResponse =
                                                                new LocationDetectionResponse(siteName, next.getKey(), timestamp, next.getValue(), locationToProbabilityMap);
                                                    }

                                                }
                                            } else if (approach.equals(BearingConfiguration.Approach.FINGERPRINT)) {
                                                detectionDataForApproachForFingerprinting = detectionDataForApproach;
                                            }
                                        }

                                    }
                                }
                            } else {
                                final DetectionDataForApproach detectionDataForApproach = detectionDataForApproaches.get(0);
                                final BearingConfiguration.Approach approach = detectionDataForApproach.getApproach();
                                if (approach.equals(BearingConfiguration.Approach.THRESHOLDING)) {
                                    shouldAllowFingerprinting = false;

                                    final Map<String, Double> locationToProbabilityMap = detectionDataForApproach.getCellConfidence();
                                    if (locationToProbabilityMap != null && !locationToProbabilityMap.isEmpty()) {
                                        final String timestamp = detectionDataForApproach.getLocalTime();

                                        final Map.Entry<String, Double> next = locationToProbabilityMap.entrySet().iterator().next();

                                        locationDetectionResponse =
                                                new LocationDetectionResponse(siteName, next.getKey(), timestamp, next.getValue(), locationToProbabilityMap);
                                    }
                                } else if (approach.equals(BearingConfiguration.Approach.FINGERPRINT)) {
                                    shouldAllowFingerprinting = true;
                                    detectionDataForApproachForFingerprinting = detectionDataForApproach;
                                }
                            }

                            if (shouldAllowFingerprinting && detectionDataForApproachForFingerprinting != null) {
                                final Map<String, Double> locationToProbabilityMap = detectionDataForApproachForFingerprinting.getCellConfidence();
                                if (locationToProbabilityMap != null && !locationToProbabilityMap.isEmpty()) {

                                    final String timestamp = detectionDataForApproachForFingerprinting.getLocalTime();

                                    final Map.Entry<String, Double> next = locationToProbabilityMap.entrySet().iterator().next();

                                    locationDetectionResponse =
                                            new LocationDetectionResponse(siteName, next.getKey(), timestamp, next.getValue(), locationToProbabilityMap);

                                }
                            }

                            if (locationDetectionResponse == null) {
                                locationDetectionResponse = getDefaultLocationDetectionResponse(siteName);
                            }

                            String localTime = getTimestamp();
                            locationDetectionResponse.setTimestamp(localTime);

                            Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "#######" + detectionDataForApproaches + "\n" + "#######" + shouldAllowFingerprinting + " Wifi if its true", null);

                            handler.sendMessage(handler.obtainMessage(SUCCESS, locationDetectionResponse));
                        } else {

                            String errorMessage = output.getBody().getErrorMessage();

                            handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                        }
                    } else {
                        String errorMessage = output.getHeader().getStatusCode().toString();

                        handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                    }
                }
            }
        });
    }

    private String getTimestamp() {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        sdf.setTimeZone(tz);
        return sdf.format(new Date());
    }

    @Override
    public void stopDetection(final IBearingDetectionCallback.IBearingStopLocationDetectionListener listener) {

        final BearingConfiguration configurationLoc =
                new BearingConfiguration(BearingConfiguration.OperationType.DETECT_LOC, detectionConfig.getApproachListMapForDetection());
        SiteMetaData siteMetaData = new SiteMetaData(locationDetectionStartedSite);

        bearingDetector.invoke(false, configurationLoc, new BearingData(siteMetaData), new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {
                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
                    Log.d(LOG_TAG, "Location detection stopped ");
                } else {
                    final String errorMessage = bearingOutput.getBody().getErrorMessage();
                    Log.e(LOG_TAG, "Location detection stop failed :  " + errorMessage);
                }
            }
        });

        final BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_SITE, this.detectionConfig.getApproachListMapForDetection());
        bearingDetector.invoke(false, configuration, null, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {
                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
                    Log.d(LOG_TAG, "site detection stopped ");
                } else {
                    final String errorMessage = bearingOutput.getBody().getErrorMessage();
                    Log.e(LOG_TAG, "site detection stop failed :  " + errorMessage);
                }
            }
        });

        // Shutdown bearing to clear all running instance cache and process
        bearingDetector.invoke(false,
                new BearingConfiguration(BearingConfiguration.OperationType.STOP_DETECTION), null, new BearingCallBack() {
                    @Override
                    public void onLocationResponse(BearingOutput bearingOutput) {
                        //Not used
                    }
                });

        previousSite = "";
        locationDetectionStartedSite = "";

        isDetectionStarted = false;

        setSiteUnkonwnCounter(0);

    }

    private void stopLocationDetection() {
        if (locationDetectionStartedSite != null && !locationDetectionStartedSite.isEmpty()) {
            final BearingConfiguration configurationLoc =
                    new BearingConfiguration(BearingConfiguration.OperationType.DETECT_LOC, detectionConfig.getApproachListMapForDetection());
            SiteMetaData siteMetaData = new SiteMetaData(locationDetectionStartedSite);

            bearingDetector.invoke(false, configurationLoc,
                    new BearingData(siteMetaData), null);
        }

    }

    @Override
    public void shutdownDetection() {
        // Shutdown bearing to clear all running instance cache and process
        bearingDetector.invoke(false,
                new BearingConfiguration(BearingConfiguration.OperationType.STOP_DETECTION), null, new BearingCallBack() {
                    @Override
                    public void onLocationResponse(BearingOutput bearingOutput) {
                        //Not used
                    }
                });
    }
}
