package com.bosch.pai.ipsadmin.retail.pmadminlib.detection.impl;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.URLUtil;

import com.bosch.pai.bearing.config.AlgorithmConfiguration;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.config.Sensor;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.datamodel.BearingOutput;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.SiteMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.apimodels.output.body.Body;
import com.bosch.pai.bearing.datamodel.apimodels.output.body.DetectionDataForApproach;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.Header;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.StatusCode;
import com.bosch.pai.ipsadmin.bearing.detect.BearingDetector;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.ipsadmin.retail.pmadminlib.Util;
import com.bosch.pai.ipsadmin.retail.pmadminlib.common.CommonUtil;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.DetectionFromServer;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.DetectionMode;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.callback.IBearingDetectionCallback;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.config.DetectionConfig;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.models.LocationDetectionResponse;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.models.SiteDetectionResponse;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public final class BearingDetectionFromServer implements DetectionFromServer {

    private static final String LOG_TAG = BearingDetectionFromServer.class.getSimpleName();

    private static final int FAILURE = 404;
    private static final int SUCCESS = 200;

    private static DetectionFromServer indoorDetection;
    private BearingDetector bearingDetector;
    private DetectionConfig detectionConfig;


    private static final String SITE_UNKNOWN = "SITE_UNKNOWN";

    private Context context;
    private String previousSite = "";
    private String locationDetectionStartedSite = "";
    private static final int siteUnknownCallbackCounter = 6;
    private static int siteUnkonwnCounter = 0;

    private boolean isDetectionStarted;

    private BearingDetectionFromServer(Context appContext,DetectionMode detectionMode) {
        this.context = appContext;
        this.bearingDetector = BearingDetector.getInstance(appContext);
        this.detectionConfig = new DetectionConfig(detectionMode);
    }

    @Nullable
    public static synchronized DetectionFromServer getInstance(Context appContext, DetectionMode detectionMode) {
        if (indoorDetection == null) {
            indoorDetection = new BearingDetectionFromServer(appContext,detectionMode);
        }
        return indoorDetection;
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
    public void storeBearingData(boolean trueForExternalFalseForInternal, Context context) {

        this.bearingDetector = BearingDetector.getInstance(context);

        if (trueForExternalFalseForInternal) {
            ConfigurationSettings.saveConfigObject(ConfigurationSettings.getConfiguration().
                    withDataStoragePathPreference(ConfigurationSettings.DataStorePathPreference.EXTERNAL));
        } else {
            ConfigurationSettings.saveConfigObject(ConfigurationSettings.getConfiguration().
                    withDataStoragePathPreference(ConfigurationSettings.DataStorePathPreference.INTERNAL));
        }
        setScanInterval();
    }

    private void setScanInterval() {
        if (Build.VERSION.SDK_INT > 23) {
            Sensor sensor = ConfigurationSettings.getConfiguration().getSensorPreferences().withProperty(Property.Sensor.BLE_ACTIVE_MODE_INTERVAL, 10000)
                    .withProperty(Property.Sensor.BLE_SCAN_TIMEOUT, 7000).withProperty(Property.Sensor.WIFI_ACTIVE_MODE_INTERVAL, 8000)
                    .withProperty(Property.Sensor.IMU_SCAN_TIMEOUT, 8000).withProperty(Property.Sensor.MAGNETO_SCAN_TIMEOUT, 8000);
            ConfigurationSettings.saveConfigObject(ConfigurationSettings.getConfiguration().withSensorPreferences(sensor));
        } else {
            Sensor sensor = ConfigurationSettings.getConfiguration().getSensorPreferences().withProperty(Property.Sensor.BLE_ACTIVE_MODE_INTERVAL, 5000)
                    .withProperty(Property.Sensor.BLE_SCAN_TIMEOUT, 4000).withProperty(Property.Sensor.WIFI_ACTIVE_MODE_INTERVAL, 5000)
                    .withProperty(Property.Sensor.IMU_SCAN_TIMEOUT, 5000).withProperty(Property.Sensor.MAGNETO_SCAN_TIMEOUT, 5000);
            ConfigurationSettings.saveConfigObject(ConfigurationSettings.getConfiguration().withSensorPreferences(sensor));
        }
    }

    @Override
    public void startSiteDetection(IBearingDetectionCallback.IBearingStartSiteDetectionListener listener) {
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

        final BearingConfiguration bearingConfiguration =
                new BearingConfiguration(BearingConfiguration.OperationType.READ_SOURCE_ID_MAP);

        bearingDetector.download(bearingConfiguration, null, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {
                Header header = bearingOutput.getHeader();
                if (header != null && StatusCode.OK.equals(header.getStatusCode())) {

                    bearingConfigurationForProximity();

                    final BearingConfiguration configuration = new BearingConfiguration(
                            BearingConfiguration.OperationType.DETECT_SITE, detectionConfig.getApproachListMapForDetection());

                    bearingDetector.invoke(true, configuration, null, new BearingCallBack() {
                        @Override
                        public void onLocationResponse(BearingOutput bearingOutput) {
                            if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
                                final String siteName = bearingOutput.getBody().getOutput();
                                final String timestamp = bearingOutput.getBody().getTimestamp();
                                final SiteDetectionResponse siteDetectionResponse = new SiteDetectionResponse(siteName, timestamp);

                                handler.sendMessage(handler.obtainMessage(SUCCESS, siteDetectionResponse));

                            } else {
                                handler.sendMessage(handler.obtainMessage(FAILURE, bearingOutput.getBody().getErrorMessage()));
                            }
                        }
                    });

                } else {
                    Body body = bearingOutput.getBody();
                    if (body != null) {
                        final String errorMessage = "snapshot download failed " + body.getErrorMessage();
                        handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                    }
                }
            }
        });


    }

    @Override
    public void stopSiteDetection(IBearingDetectionCallback.IBearingStopSiteDetectionListener listener) {
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
        bearingDetector.invoke(false, configuration, null, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {
                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
                    handler.sendMessage(handler.obtainMessage(SUCCESS));
                } else {
                    handler.sendMessage(handler.obtainMessage(FAILURE, bearingOutput.getBody().getErrorMessage()));
                }
            }
        });
    }

    @Override
    public void startDetection(IBearingDetectionCallback.IBearingStartLocationDetectionListener listener) {
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

        final BearingConfiguration bearingConfiguration =
                new BearingConfiguration(BearingConfiguration.OperationType.READ_SOURCE_ID_MAP);

        bearingDetector.download(bearingConfiguration, null, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {
                Header header = bearingOutput.getHeader();
                if (header != null && StatusCode.OK.equals(header.getStatusCode())) {

                    bearingConfigurationForProximity();

                    final BearingConfiguration configuration = new BearingConfiguration(
                            BearingConfiguration.OperationType.DETECT_SITE, detectionConfig.getApproachListMapForDetection());

                    bearingDetector.invoke(true, configuration, null, new BearingCallBack() {
                        @Override
                        public void onLocationResponse(final BearingOutput bearingOutput) {

                            if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {

                                final String siteName = bearingOutput.getBody().getOutput();
//                            final String timestamp = bearingOutput.getBody().getTimestamp();

//                            final SiteDetectionResponse siteDetectionResponse = new SiteDetectionResponse(siteName, timestamp);

                                Log.d(LOG_TAG, "Site detection : " + siteName);

                                if (!SITE_UNKNOWN.equalsIgnoreCase(siteName)) {
                                    if (!siteName.equals(previousSite)) {

                                        startLocationDetection(siteName, handler);

                                    } else {
                                        stopLocationDetection();
                                        locationDetectionStartedSite = "";
                                        previousSite = siteName;
                                    }

                                } else {
                                    stopLocationDetection();
                                    locationDetectionStartedSite = "";
                                    previousSite = siteName;
                                }


                            } else {
                                String errorMessage = bearingOutput.getBody().getErrorMessage();

                                handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));

                            }
                        }
                    });

                } else {
                    Body body = bearingOutput.getBody();
                    if (body != null) {
                        final String errorMessage = "snapshot download failed " + body.getErrorMessage();
                        handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                    }
                }
            }
        });

    }

    public void stopLocationDetection() {
        final BearingConfiguration configurationLoc =
                new BearingConfiguration(BearingConfiguration.OperationType.DETECT_LOC, detectionConfig.getApproachListMapForDetection());
        SiteMetaData siteMetaData = new SiteMetaData(locationDetectionStartedSite);

        bearingDetector.invoke(false, configurationLoc,
                new BearingData(siteMetaData), new BearingCallBack() {
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

    }

    private void startLocationDetection(String siteName, final Handler handler) {

        if (!siteName.equals(locationDetectionStartedSite)) {

            locationDetectionStartedSite = siteName;

            final BearingConfiguration locConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_LOC,
                    detectionConfig.getApproachListMapForDetection());
            final SiteMetaData siteMetaData = new SiteMetaData(siteName);
            final BearingData bearingData = new BearingData(siteMetaData);

            bearingDetector.invoke(true, locConfiguration, bearingData, new BearingCallBack() {
                @Override
                public void onLocationResponse(BearingOutput output) {
                    if (output.getHeader().getStatusCode().equals(StatusCode.OK)) {

                        final List<DetectionDataForApproach> detectionDataForApproaches = output.getBody().getLocationDetectionOutput();
                        if (detectionDataForApproaches != null && !detectionDataForApproaches.isEmpty()) {
                            final Map<String, Double> locationToProbabilityMap = detectionDataForApproaches.get(0).getCellConfidence();
                            final String ts = detectionDataForApproaches.get(0).getLocalTime();

                            final Map.Entry<String, Double> next = locationToProbabilityMap.entrySet().iterator().next();

                            final LocationDetectionResponse locationDetectionResponse =
                                    new LocationDetectionResponse(siteName, next.getKey(), ts, next.getValue(), locationToProbabilityMap);

                            Log.d(LOG_TAG, "location detection : " + locationToProbabilityMap);

                            handler.sendMessage(handler.obtainMessage(SUCCESS, locationDetectionResponse));
                        }
                    } else {

                        String errorMessage = output.getBody().getErrorMessage();

                        handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                    }
                }
            });
        }
    }

    @Override
    public void stopDetection() {
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

        previousSite = "";
        locationDetectionStartedSite = "";

    }


    private void bearingConfigurationForProximity() {
        final Sensor sensor = ConfigurationSettings.getConfiguration().getSensorPreferences()
                .withProperty(Property.Sensor.WIFI_ACTIVE_MODE, ConfigurationSettings.ActiveMode.RECURSIVE)
                .withProperty(Property.Sensor.WIFI_ACTIVE_MODE_INTERVAL, 5000.0);
        final AlgorithmConfiguration algorithmConfiguration = ConfigurationSettings.getConfiguration()
                .getAlgorithmConfigurationPreferences();
        algorithmConfiguration.withProperty(Property.Algorithm.SNAPSHOT_THRESHOLD, -75.0);
        final ConfigurationSettings configurationSettings = ConfigurationSettings.getConfiguration()
                .withSensorPreferences(sensor).withAlgorithmPreferences(algorithmConfiguration);
        ConfigurationSettings.saveConfigObject(configurationSettings);
    }

}
