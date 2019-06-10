package com.bosch.pai.ipswrapper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.bosch.pai.IeroIPSPlatformListener;
import com.bosch.pai.IeroIPSPlatform;
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
import com.bosch.pai.bearing.detect.BearingDetector;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.detection.Util;
import com.bosch.pai.detection.authentication.AuthenticationCallback;
import com.bosch.pai.detection.authentication.AuthenticationManager;
import com.bosch.pai.detection.config.CommonUtil;
import com.bosch.pai.detection.config.DetectionConfig;
import com.bosch.pai.detection.models.StatusMessage;
import com.bosch.pai.session.SessionHandler;
import com.bosch.pai.session.SubSessionInfo;
import com.bosch.pai.util.Constant;
import com.bosch.pai.util.CrashUploader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Ips detector.
 */
class IeroIPSPlatformImpl implements IeroIPSPlatform {

    private static final String LOG_TAG = "[IeroIPSPlatform]: ";

    private static final String SITE_UNKNOWN = "SITE_UNKNOWN";
    private static final String LOCATION_UNKNOWN = "LOCATION_UNKNOWN";
    private static final int FAILURE = 404;
    private static final int SUCCESS = 200;
    private static final int SITE_DETECTION_SUCCESS = 100;
    private static final int LOCATION_DETECTION_SUCCESS = 300;

    private static final int siteUnknownCallbackCounter = 5;
    private int siteUnknownCounter = 0;

    private IeroIPSPlatformListener listener;
    private final BearingDetector bearingDetector;
    private final DetectionConfig detectionConfig;
    private final Map<Config.Key, Object> configMap;
    private String companyId;
    private String username;
    private String ipsFlavor;
    private Context context;

    private boolean isAuthenticated = false;
    private String sessionId;
    private String locationDetectionStartedSite = "";
    private String previousSite = "";
    private String previousLocation = "";
    private boolean isDetectionStarted;

    /**
     * Instantiates a new Ips detector.
     *
     * @param appContext              the app context
     * @param ieroIPSPlatformListener the ieroIPSPlatformListener
     */
    IeroIPSPlatformImpl(Context appContext, IeroIPSPlatformListener ieroIPSPlatformListener) {
        this.configMap = new HashMap<>();
        this.bearingDetector = BearingDetector.getInstance(appContext);
        this.context = appContext;
        this.detectionConfig = new DetectionConfig();
        this.listener = ieroIPSPlatformListener;
    }

    protected void setListener(IeroIPSPlatformListener ieroIPSPlatformListener) {
        this.listener = ieroIPSPlatformListener;
    }

    protected void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void register(Map<Config.Key, Object> configMap) {
        if (configMap == null || configMap.isEmpty()) {
            listener.onFailure("Config map can not be Null or Empty");
            return;
        }
        if (isDetectionStarted) {
            listener.onFailure("An active session of IPS is already running. " +
                    "Please unregister, before starting new session");
            return;
        }
        if (!checkLocationPermission(this.context)) {
            this.listener.onFailure("Grant location permission from app settings");
            return;
        }
        final Object tempUserName = configMap.get(Config.Key.UNIQUE_CLIENT_ID) != null ? configMap.get(Config.Key.UNIQUE_CLIENT_ID) : "";
        final Object tempCompanyId = configMap.get(Config.Key.COMPANY_ID) != null ? configMap.get(Config.Key.COMPANY_ID) : "";
        try {
            this.companyId = tempCompanyId.toString();
            this.username = tempUserName.toString();
        } catch (ClassCastException e) {
            listener.onFailure("CompanyID/UserName should be of type String.class");
            return;
        }
        if (username.trim().isEmpty() || companyId.trim().isEmpty()) {
            this.listener.onFailure("CompanyID or UserName can not be empty");
            return;
        }

        this.ipsFlavor = configMap.get(Config.Key.FLAVOR) != null ? configMap.get(Config.Key.FLAVOR).toString() : "";

        IeroIPSPlatformImpl.this.configMap.clear();
        IeroIPSPlatformImpl.this.configMap.putAll(configMap);
        IeroIPSPlatformImpl.this.siteUnknownCounter = 0;

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final StatusMessage statusMessage = (StatusMessage) message.obj;
                    listener.onFailure(statusMessage.getMessage());
                } else if (message.what == SITE_DETECTION_SUCCESS) {
                    final String siteName = (String) message.obj;
                    listener.onSiteDetected(siteName);
                } else if (message.what == LOCATION_DETECTION_SUCCESS) {
                    final Map<String, Double> locationProbabilityMap = (Map<String, Double>) message.obj;
                    listener.onLocationDetected(locationProbabilityMap);
                }
                return false;
            }
        });


        setBearingDataStorePrefs();
        setServerEndPoint();

        authenticateAndDownloadSourceIdMap(new AuthenticationCallback() {
            @Override
            public void onAuthenticationSuccess() {
                CrashUploader.uploadCrashReports(context);
                uploadSessionData();
                setBearingSensorPrefs();

                final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachListMap = detectionConfig.getApproachListMap(IeroIPSPlatformImpl.this.configMap, listener);
                if (approachListMap == null || approachListMap.isEmpty()) {
                    listener.onFailure("Please add valid SensorType config for configMap");
                    return;
                }

                IeroIPSPlatformImpl.this.isDetectionStarted = true;
                final BearingConfiguration configuration = new BearingConfiguration(
                        BearingConfiguration.OperationType.DETECT_SITE,
                        approachListMap);

                bearingDetector.invoke(true, configuration, null, new BearingCallBack() {
                    @Override
                    public void onLocationResponse(final BearingOutput bearingOutput) {

                        try {
                            if (isDetectionStarted) {
                                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {

                                    final String siteName = bearingOutput.getBody().getOutput();

                                    Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "Site detection : " + siteName, null);

                                    if (!SITE_UNKNOWN.equalsIgnoreCase(siteName)) {

                                        siteUnknownCounter = 0;
                                        handler.sendMessage(handler.obtainMessage(SITE_DETECTION_SUCCESS, siteName));
                                        Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "Site detection callback triggered: " + siteName, null);

                                        if (!siteName.equals(previousSite)) {

                                            previousSite = siteName;

                                            if (!siteName.equals(locationDetectionStartedSite)) {

                                                stopLocationDetection();

                                                locationDetectionStartedSite = siteName;

                                                startLocationDetection(siteName,
                                                        detectionConfig.getApproachListMap(IeroIPSPlatformImpl.this.configMap, listener),
                                                        handler);
                                            }
                                        }

                                    } else {

                                        siteUnknownCounter++;

                                        if (siteUnknownCounter >= siteUnknownCallbackCounter) {

                                            handler.sendMessage(handler.obtainMessage(SITE_DETECTION_SUCCESS, siteName));
                                            Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "Site detection callback triggered: " + siteName, null);

                                            stopLocationDetection();
                                            locationDetectionStartedSite = "";
                                            previousSite = siteName;
                                        }

                                    }

                                } else {
                                    String errorMessage = bearingOutput.getBody().getErrorMessage();

                                    final StatusMessage statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, errorMessage);
                                    handler.sendMessage(handler.obtainMessage(FAILURE, statusMessage));

                                }
                            }
                        } catch (Exception e) {

                            Util.addLogs(Util.LOG_STATUS.ERROR, LOG_TAG, "Error : " + e, e);

                            final StatusMessage statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, e + " ");
                            handler.sendMessage(handler.obtainMessage(FAILURE, statusMessage));
                        }
                    }
                });
            }

            @Override
            public void onAuthenticationFail(String errormessage) {
                final StatusMessage statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, errormessage);
                handler.sendMessage(handler.obtainMessage(FAILURE, statusMessage));
            }
        });

        listener.onSuccess("Detection started successfully!");
    }

    private boolean checkLocationPermission(Context context) {
        final int accessFineLocation = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION);

        final int accessCoarseLocation = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION);

        return accessFineLocation == PackageManager.PERMISSION_GRANTED &&
                accessCoarseLocation == PackageManager.PERMISSION_GRANTED;
    }

    private void authenticateAndDownloadSourceIdMap(final AuthenticationCallback listener) {

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String error = (String) message.obj;
                    listener.onAuthenticationFail(error);
                } else if (message.what == SUCCESS) {
                    listener.onAuthenticationSuccess();
                }
                return false;
            }
        });


        if (!isAuthenticated) {

            InputStream cert = null;
            if (Util.isHttpsURL(Constant.getServerUrl())) {
                cert = Util.getCertificate(context);
            }

            final AuthenticationManager authenticationManagerBearing = new AuthenticationManager(
                    companyId, Util.getSHA256Conversion(username),
                    Util.getSHA256Conversion("iot_client_user"), Constant.getServerUrl());
            authenticationManagerBearing.authenticateUser(cert, new AuthenticationCallback() {
                @Override
                public void onAuthenticationSuccess() {
                    // Role is not checked now, uncomment this code and delete line 279 and 280 when in need
                    /*authenticationManagerBearing.checkUserRole(context, new AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSuccess() {
                            IeroIPSPlatformImpl.this.isAuthenticated = true;
                            downloadSourceIdMap(handler);
                        }

                        @Override
                        public void onAuthenticationFail(String message) {
                            IeroIPSPlatformImpl.this.isAuthenticated = false;
                            handler.sendMessage(handler.obtainMessage(FAILURE, message));
                        }
                    });*/
                    IeroIPSPlatformImpl.this.isAuthenticated = true;
                    downloadSourceIdMap(handler);
                }

                @Override
                public void onAuthenticationFail(final String message) {
                    handler.sendMessage(handler.obtainMessage(FAILURE, message));

                    IeroIPSPlatformImpl.this.isAuthenticated = false;
                }
            });
        } else {

            downloadSourceIdMap(handler);

        }

    }

    private void downloadSourceIdMap(final Handler handler) {
        final BearingConfiguration bearingConfiguration =
                new BearingConfiguration(BearingConfiguration.OperationType.READ_SOURCE_ID_MAP);

        bearingDetector.download(bearingConfiguration, null, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {
                Header header = bearingOutput.getHeader();
                if (header != null && StatusCode.OK.equals(header.getStatusCode())) {
                    handler.sendMessage(handler.obtainMessage(SUCCESS));
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

    //##########################################################################################################
    //Default it's INTERNAL storage, uncomment this code when {@link Config.Key} is given with STORAGE prefs key
    //##########################################################################################################
    private void setBearingDataStorePrefs(/* boolean trueForExternalFalseForInternal */) {
        /*if (trueForExternalFalseForInternal) {
            ConfigurationSettings.saveConfigObject(ConfigurationSettings.getConfiguration().
                    withDataStoragePathPreference(ConfigurationSettings.DataStorePathPreference.EXTERNAL));
        } else {*/
        ConfigurationSettings.saveConfigObject(ConfigurationSettings.getConfiguration().
                withDataStoragePathPreference(ConfigurationSettings.DataStorePathPreference.INTERNAL));
        /*}*/
    }

    private void setBearingSensorPrefs() {
        final Sensor sensor = ConfigurationSettings.getConfiguration().getSensorPreferences()
                .withProperty(Property.Sensor.WIFI_ACTIVE_MODE, ConfigurationSettings.ActiveMode.RECURSIVE)
                .withProperty(Property.Sensor.WIFI_ACTIVE_MODE_INTERVAL, 5000.0);
        final AlgorithmConfiguration algorithmConfiguration = ConfigurationSettings.getConfiguration()
                .getAlgorithmConfigurationPreferences();
        algorithmConfiguration.withProperty(Property.Algorithm.SNAPSHOT_THRESHOLD, -100.0);
        final ConfigurationSettings configurationSettings = ConfigurationSettings.getConfiguration()
                .withSensorPreferences(sensor).withAlgorithmPreferences(algorithmConfiguration);
        ConfigurationSettings.saveConfigObject(configurationSettings);
    }

    private void uploadSessionData() {
        try {
            if ("IPS_PM".equals(ipsFlavor))
                return;
            final SessionHandler sessionHandler = SessionHandler.getInstance(context);
            sessionHandler.uploadPreviousSessionDataIfAvailable(context, companyId);
        } catch (Exception e) {
            Util.addLogs(Util.LOG_STATUS.ERROR, LOG_TAG, "Error : " + e, e);
        }
    }

    private void saveSessionData(String username, String currentSiteName, String currentLocation) {
        if ("IPS_PM".equals(ipsFlavor))
            return;
        final SessionHandler sessionHandler = SessionHandler.getInstance(context);
        String storeId = "";
        if (currentSiteName != null && !currentSiteName.trim().isEmpty()) {
            storeId = currentSiteName.substring(0, currentSiteName.indexOf('_'));
        }
        //Converting username to SHA 256 hash
        final String userNameHashString = Util.getSHA256Conversion(username);
        Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "saveSessionData: saving session data for user:" + username + " hash: " + userNameHashString, null);
        if (sessionId == null) {
            sessionId = sessionHandler.startSession(userNameHashString, storeId);
        }
        final SubSessionInfo subSessionInfo = new SubSessionInfo(userNameHashString, currentSiteName, currentLocation);
        subSessionInfo.setStoreId(storeId);
        sessionHandler.startSubSession(sessionId, subSessionInfo);
    }

    private void endSession() {
        if ("IPS_PM".equals(ipsFlavor))
            return;
        try {
            SessionHandler sessionHandler = SessionHandler.getInstance(context);
            if (sessionId != null) {
                sessionHandler.endSession(sessionId);
                sessionId = null;
            }
        } catch (Exception e) {
            Util.addLogs(Util.LOG_STATUS.ERROR, LOG_TAG, "Error : " + e, e);
        }
    }

    private boolean setServerEndPoint() {
        final String serverEP = Constant.getServerUrl() + CommonUtil.getBearingServerEndPoint();

        InputStream certificate;
        if (URLUtil.isHttpsUrl(Constant.getServerUrl())) {
            certificate = Util.getCertificate(context);
        } else {
            certificate = null;
        }

        final BearingConfiguration setEndPointConfig =
                new BearingConfiguration(BearingConfiguration.OperationType.SET_SERVER_ENDPOINT, serverEP, certificate);

        BearingOutput setServerIpOutput = null;
        if (bearingDetector != null) {
            setServerIpOutput = bearingDetector.read(setEndPointConfig, null, false, null);
        }

        return setServerIpOutput != null && setServerIpOutput.getHeader().getStatusCode().equals(StatusCode.OK);
    }

    private void startLocationDetection(final String siteName, final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> map, final Handler handler) {

        final BearingConfiguration locConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_LOC,
                map);
        final SiteMetaData siteMetaData = new SiteMetaData(siteName);
        final BearingData bearingData = new BearingData(siteMetaData);

        bearingDetector.invoke(true, locConfiguration, bearingData, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput output) {
                try {
                    if (isDetectionStarted) {
                        if (output.getHeader().getStatusCode().equals(StatusCode.OK)) {

                            final List<DetectionDataForApproach> detectionDataForApproaches = output.getBody().getLocationDetectionOutput();

                            if (detectionDataForApproaches != null && !detectionDataForApproaches.isEmpty()) {

                                boolean shouldAllowFingerprinting = false;
                                DetectionDataForApproach detectionDataForApproachForFingerprinting = null;

                                Map<String, Double> locationToProbabilityMap = null;


                                for (DetectionDataForApproach detectionDataForApproach : detectionDataForApproaches) {

                                    if (detectionDataForApproach != null) {
                                        final BearingConfiguration.Approach approach = detectionDataForApproach.getApproach();
                                        if (approach != null) {
                                            if (approach.equals(BearingConfiguration.Approach.THRESHOLDING)) {
                                                // proccess further

                                                locationToProbabilityMap = detectionDataForApproach.getCellConfidence();
                                                if (locationToProbabilityMap != null && !locationToProbabilityMap.isEmpty()) {
                                                    shouldAllowFingerprinting = locationToProbabilityMap.containsKey(LOCATION_UNKNOWN);
                                                }
                                            } else if (approach.equals(BearingConfiguration.Approach.FINGERPRINT)) {
                                                shouldAllowFingerprinting = true;
                                                detectionDataForApproachForFingerprinting = detectionDataForApproach;
                                            }
                                        }
                                    }
                                }

                                if (shouldAllowFingerprinting && detectionDataForApproachForFingerprinting != null) {
                                    locationToProbabilityMap = detectionDataForApproachForFingerprinting.getCellConfidence();
                                }

                                if (locationToProbabilityMap == null) {
                                    locationToProbabilityMap = new HashMap<>();
                                    locationToProbabilityMap.put(LOCATION_UNKNOWN, 0.0);
                                } else if (locationToProbabilityMap.isEmpty()) {
                                    locationToProbabilityMap.put(LOCATION_UNKNOWN, 0.0);
                                }

                                Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "Location detection callback triggered : " + locationToProbabilityMap, null);
                                final String tempLocName = locationToProbabilityMap.keySet().iterator().next();
                                if (!LOCATION_UNKNOWN.equals(tempLocName) && !previousLocation.equals(tempLocName)) {
                                    previousLocation = tempLocName;
                                    saveSessionData(username, siteName, tempLocName);
                                }
                                handler.sendMessage(handler.obtainMessage(LOCATION_DETECTION_SUCCESS, locationToProbabilityMap));


                            } else {

                                String errorMessage = output.getBody().getErrorMessage();

                                final StatusMessage statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, errorMessage);
                                handler.sendMessage(handler.obtainMessage(FAILURE, statusMessage));
                            }
                        } else {


                            String errorMessage = output.getBody().getErrorMessage();

                            final StatusMessage statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, errorMessage);
                            handler.sendMessage(handler.obtainMessage(FAILURE, statusMessage));
                        }
                    }
                } catch (Exception e) {

                    Util.addLogs(Util.LOG_STATUS.ERROR, LOG_TAG, "Error : " + e, e);

                    final StatusMessage statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, e + " ");
                    handler.sendMessage(handler.obtainMessage(FAILURE, statusMessage));
                }
            }
        });
    }

    private void stopLocationDetection() {
        if (!TextUtils.isEmpty(locationDetectionStartedSite)) {
            IeroIPSPlatformImpl.this.endSession();
            final BearingConfiguration configurationLoc =
                    new BearingConfiguration(BearingConfiguration.OperationType.DETECT_LOC,
                            detectionConfig.getApproachListMap(IeroIPSPlatformImpl.this.configMap, listener));
            SiteMetaData siteMetaData = new SiteMetaData(locationDetectionStartedSite);

            bearingDetector.invoke(false, configurationLoc,
                    new BearingData(siteMetaData), new BearingCallBack() {
                        @Override
                        public void onLocationResponse(BearingOutput bearingOutput) {
                            if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
                                Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "Location detection stopped ", null);
                            } else {
                                final String errorMessage = bearingOutput.getBody().getErrorMessage();
                                Util.addLogs(Util.LOG_STATUS.ERROR, LOG_TAG, "Location detection stop failed :  " + errorMessage, null);
                            }
                        }
                    });
        }
    }

    @Override
    public void unregister(Map<Config.Key, Object> configMap) {
        if (IeroIPSPlatformImpl.this.isDetectionStarted) {
            final BearingConfiguration configurationLoc =
                    new BearingConfiguration(BearingConfiguration.OperationType.DETECT_LOC,
                            detectionConfig.getApproachListMap(IeroIPSPlatformImpl.this.configMap, listener));
            SiteMetaData siteMetaData = new SiteMetaData(locationDetectionStartedSite);

            bearingDetector.invoke(false, configurationLoc, new BearingData(siteMetaData), new BearingCallBack() {
                @Override
                public void onLocationResponse(BearingOutput bearingOutput) {
                    if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
                        Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "Location detection stopped ", null);
                    } else {
                        final String errorMessage = bearingOutput.getBody().getErrorMessage();
                        Util.addLogs(Util.LOG_STATUS.ERROR, LOG_TAG, "Location detection stop failed :  " + errorMessage, null);
                    }
                }
            });

            final BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_SITE,
                    detectionConfig.getApproachListMap(IeroIPSPlatformImpl.this.configMap, listener));
            bearingDetector.invoke(false, configuration, null, new BearingCallBack() {
                @Override
                public void onLocationResponse(BearingOutput bearingOutput) {
                    if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
                        Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "site detection stopped ", null);
                    } else {
                        final String errorMessage = bearingOutput.getBody().getErrorMessage();
                        Util.addLogs(Util.LOG_STATUS.ERROR, LOG_TAG, "site detection stop failed :  " + errorMessage, null);
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
            endSession();
            uploadSessionData();
            final AuthenticationManager authenticationManagerBearing = new AuthenticationManager(
                    companyId, Util.getSHA256Conversion(username),
                    Util.getSHA256Conversion("iot_client_user"), Constant.getServerUrl());
            authenticationManagerBearing.clearUserSession();
        }
        this.configMap.clear();
        this.previousSite = "";
        this.previousLocation = "";
        this.locationDetectionStartedSite = "";

        this.isDetectionStarted = false;
        this.siteUnknownCounter = 0;
        this.isAuthenticated = false;
    }
}
