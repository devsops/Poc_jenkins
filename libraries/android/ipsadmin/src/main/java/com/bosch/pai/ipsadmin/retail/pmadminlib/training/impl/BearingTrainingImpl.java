package com.bosch.pai.ipsadmin.retail.pmadminlib.training.impl;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.URLUtil;

import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.config.Sensor;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.datamodel.BearingOutput;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.LocationMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.SiteMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.apimodels.output.body.Body;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.StatusCode;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.bearing.persistence.datastore.DataStore;
import com.bosch.pai.ipsadmin.bearing.train.BearingTrainer;
import com.bosch.pai.ipsadmin.retail.pmadminlib.Util;
import com.bosch.pai.ipsadmin.retail.pmadminlib.common.CommonUtil;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.Training;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.callback.IBearingTrainingCallback;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.config.TrainingConfig;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.helper.TrainingHelper;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.BearingSitedetails;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.ScannedBleDetails;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.SnapshotItemWithSensorType;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class BearingTrainingImpl implements Training {

    private static final String ERROR = "Error: ";
    private final String LOG_TAG = BearingTrainingImpl.class.getSimpleName();

    private int counter = 0;
    private static final int FAILURE = 404;
    private static final int SUCCESS = 200;


    private static Training bearingTraining;
    private int siteCounter = 0;
    private BearingTrainer bearingTrainer;
    private TrainingConfig trainingConfig;

    private BearingTrainingImpl(Context appContext, String serverEndPoint) {
        this.bearingTrainer = BearingTrainer.getInstance(appContext);
        setScanInterval();
        this.trainingConfig = new TrainingConfig();
        if (serverEndPoint != null && !serverEndPoint.isEmpty()) {
            InputStream certificate;
            if (URLUtil.isHttpsUrl(serverEndPoint)) {
                certificate = Util.getCertificate(appContext);
            } else {
                certificate = null;
            }
            setServerEndPoint(serverEndPoint, certificate, null);
        }
    }

    public static synchronized Training getInstance(Context appContext, String serverEndPoint) {
        if (bearingTraining == null) {
            bearingTraining = new BearingTrainingImpl(appContext, serverEndPoint);
        }
        return bearingTraining;
    }

    @Override
    public void setBearingServerEndPoint(String serverEndPoint, InputStream certificateStream, IBearingTrainingCallback.IBearingSetServerEndpointListener listener) {
        if (serverEndPoint != null && !serverEndPoint.isEmpty()) {
            setServerEndPoint(serverEndPoint, certificateStream, listener);
        } else {
            listener.onFailure("Server Endpoint should not be null");
        }
    }

    private void setServerEndPoint(String serverEndPoint, InputStream certificateStream, IBearingTrainingCallback.IBearingSetServerEndpointListener listener) {

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    listener.onSuccess();
                }
                return false;
            }
        });

        final String finalServerEndpoint = serverEndPoint + CommonUtil.getBearingServerEndPoint();

        final BearingConfiguration configurationUploadIp =
                new BearingConfiguration(BearingConfiguration.OperationType.SET_SERVER_ENDPOINT, finalServerEndpoint, certificateStream);
        final boolean isValid = bearingTrainer.upload(configurationUploadIp, null, null);
        if (listener != null) {
            if (!isValid) {
                String name = "Invalid Ip";
                handler.sendMessage(handler.obtainMessage(FAILURE, name));
            } else {
                handler.sendMessage(handler.obtainMessage(SUCCESS));
            }
        }
    }

    @Override
    public void storeBearingData(boolean trueForExternalFalseForInternal, Context context) {

        bearingTrainer = BearingTrainer.getInstance(context);
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
                    .withProperty(Property.Sensor.BLE_SCAN_TIMEOUT, 9000).withProperty(Property.Sensor.WIFI_ACTIVE_MODE_INTERVAL, 8000)
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
    public void trainSite(final String siteName, int numberOfFloors, int rssiValue, boolean isWifiSensor, final IBearingTrainingCallback.ITrainsite listener) {

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == 1111) {
                    final List<SnapshotObservation> observations = (List<SnapshotObservation>) message.obj;
                    final List<SnapshotItemWithSensorType> snapshotItemsWithSensorTypes = getSnapshotItemsWithSensorTypes(Collections.unmodifiableList(observations));
                    listener.onWifiSignalCapture(observations, Collections.unmodifiableList(snapshotItemsWithSensorTypes));
                } else if (message.what == 1112) {
                    final List<SnapshotObservation> observations = (List<SnapshotObservation>) message.obj;
                    final List<SnapshotItem> items = TrainingHelper.getBLESnapshotItems(observations);

                    final List<ScannedBleDetails> bleSnapshotItems = TrainingHelper.getBLESourceIdsAsList(items);

                    listener.onBleSignalCapture(observations, Collections.unmodifiableList(bleSnapshotItems));
                }
                return false;
            }
        });

        final SiteMetaData siteMetaData = new SiteMetaData(siteName);
        siteMetaData.setNumberOfFloors(numberOfFloors);
        siteMetaData.setNumberOfFloors(2);

        List<BearingConfiguration.SensorType> sensorTypes = null;
        final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachListHashMap = new HashMap<>();
        if (isWifiSensor) {
            sensorTypes = trainingConfig.getWifiSensorTypes();
            approachListHashMap.put(BearingConfiguration.Approach.DATA_CAPTURE, sensorTypes);
        } else {
            sensorTypes = trainingConfig.getBleSensorTypes();
            approachListHashMap.put(BearingConfiguration.Approach.THRESHOLDING, sensorTypes);
        }
        final BearingData bearingData = new BearingData(siteMetaData);
        final BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.TRAIN_SITE, approachListHashMap);


        bearingTrainer.create(configuration, bearingData, false, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {
                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {

                    updateSiteConfig(siteName, rssiValue);

                    if (isWifiSensor) {
                        final BearingConfiguration configurationSignalRetrieve =
                                new BearingConfiguration(BearingConfiguration.OperationType.SNAPSHOT_FETCH);
                        BearingData bearingDataForSignalRetrieve = new BearingData(new SiteMetaData(siteName));

                        final BearingOutput retrieve = bearingTrainer.retrieve(configurationSignalRetrieve, bearingDataForSignalRetrieve, false, null);
                        if (retrieve.getHeader().getStatusCode().equals(StatusCode.OK)) {
                            final List<SnapshotObservation> observations = retrieve.getBody().getSnapshotObservations();

                            if (observations != null) {
                                handler.sendMessage(handler.obtainMessage(1111, observations));
                            } else {
                                final Body body = retrieve.getBody();
                                if (body != null) {
                                    handler.sendMessage(handler.obtainMessage(FAILURE, body.getErrorMessage()));
                                }
                            }
                        } else {
                            final String errorMessage = retrieve.getHeader().getStatusCode().toString();
                            handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                        }

                    } else {
                        final BearingData bearingDataForSignalRetrieve = new BearingData(new SiteMetaData(siteName));
                        final BearingConfiguration bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.SNAPSHOT_FETCH);
                        final BearingOutput retrieve = bearingTrainer.retrieve(bearingConfiguration, bearingDataForSignalRetrieve, false, null);
                        if (retrieve.getHeader().getStatusCode().equals(StatusCode.OK)) {
                            final List<SnapshotObservation> observations = retrieve.getBody().getSnapshotObservations();

                            if (observations != null) {
                                handler.sendMessage(handler.obtainMessage(1112, observations));
                            } else {
                                final Body body = retrieve.getBody();
                                if (body != null) {
                                    handler.sendMessage(handler.obtainMessage(FAILURE, body.getErrorMessage()));
                                }
                            }
                        } else {
                            final String errorMessage = retrieve.getHeader().getStatusCode().toString();
                            handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                        }
                    }

                } else {
                    final String errorMessage = bearingOutput.getBody().getErrorMessage();
                    handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                }
            }
        });
    }

    @Override
    public boolean updateSiteConfig(final String siteName, final int rssiThresh) {
        final BearingData bearingDataOld = new BearingData(new SiteMetaData(siteName));
        final SiteMetaData siteMetaData = new SiteMetaData(siteName);
        siteMetaData.setRssiThreshHold(rssiThresh);
        final BearingData bearingDataNew = new BearingData(siteMetaData);
        final BearingConfiguration bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.UPDATE_SITE_CONFIG);
        return bearingTrainer.update(bearingConfiguration, bearingDataOld, bearingDataNew, false, null);
    }

    @Override
    public void snapshotFeatchSoonAfterTrainSite(String siteName,
                                                 IBearingTrainingCallback.IBearingSiteSignalMergeListener listener) {

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    final List<SnapshotObservation> observations = (List<SnapshotObservation>) message.obj;
                    final List<SnapshotItemWithSensorType> snapshotItemsWithSensorTypes = getSnapshotItemsWithSensorTypes(Collections.unmodifiableList(observations));
                    listener.onSuccess(siteName, observations, Collections.unmodifiableList(snapshotItemsWithSensorTypes));
                }
                return false;
            }
        });

        final BearingConfiguration configurationSignalRetrieve =
                new BearingConfiguration(BearingConfiguration.OperationType.SNAPSHOT_FETCH);
        BearingData bearingDataForSignalRetrieve = new BearingData(new SiteMetaData(siteName));

        final BearingOutput retrieve = bearingTrainer.retrieve(configurationSignalRetrieve, bearingDataForSignalRetrieve, false, null);
        if (retrieve.getHeader().getStatusCode().equals(StatusCode.OK)) {
            final List<SnapshotObservation> observations = retrieve.getBody().getSnapshotObservations();

            if (observations != null) {
                handler.sendMessage(handler.obtainMessage(SUCCESS, observations));
            } else {
                final Body body = retrieve.getBody();
                if (body != null) {
                    handler.sendMessage(handler.obtainMessage(FAILURE, body.getErrorMessage()));
                }
            }
        } else {
            final String errorMessage = retrieve.getHeader().getStatusCode().toString();
            handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
        }
    }

    @Override
    public boolean siteBleUpdateOnMerge(String siteName, List<SnapshotObservation> snapshotObservations, Set<String> sourceIds) {
        final List<SnapshotObservation> snapshotObservationListWithSelectedIds = TrainingHelper.updateSnapshotObservationsWithSelectedIds(sourceIds,
                snapshotObservations);
        final BearingData bearingDataOld = new BearingData(new SiteMetaData(siteName));
        final SiteMetaData siteMetaData = new SiteMetaData(siteName);
        siteMetaData.setSnapshotObservations(snapshotObservationListWithSelectedIds);
        final BearingData bearingDataNew = new BearingData(siteMetaData);
        final BearingConfiguration bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.SITE_UPDATE_ON_MERGE);
        return bearingTrainer.update(bearingConfiguration, bearingDataOld, bearingDataNew, false, null);
    }

    private List<SnapshotItemWithSensorType> getSnapshotItemsWithSensorTypes(
            List<SnapshotObservation> snapshotObservations) {
        final List<SnapshotItemWithSensorType> snapshotItemWithSensorTypesList = new ArrayList<>();

        for (SnapshotObservation snapshotObservation : snapshotObservations) {
            for (BearingConfiguration.SensorType sensorType : this.trainingConfig.getWifiSensorTypes()) {

                if (snapshotObservation.getSensorType().equals(sensorType)) {
                    for (SnapshotItem snapshotItem : snapshotObservation.getSnapShotItemList()) {
                        SnapshotItemWithSensorType snapshotItemWithSensorType =
                                new SnapshotItemWithSensorType(sensorType, snapshotItem);
                        snapshotItemWithSensorTypesList.add(snapshotItemWithSensorType);
                    }
                    break;
                }
            }
        }
        return snapshotItemWithSensorTypesList;
    }

    @Override
    public boolean updateSnapshot(String siteName, List<SnapshotObservation> snapshotObservations,
                                  List<SnapshotItemWithSensorType> snapshotItemWithSensorTypeList) {
        final List<SnapshotObservation> observations =
                getSnapshotObservationFromSnapshotItemWithSensorList(snapshotObservations, snapshotItemWithSensorTypeList);

        final BearingConfiguration configuration =
                new BearingConfiguration(BearingConfiguration.OperationType.SNAPSHOT_UPDATE);
        final BearingData bearingDataOld = new BearingData(new SiteMetaData(siteName));
        final SiteMetaData siteMetaData = new SiteMetaData(siteName);
        siteMetaData.setSnapshotObservations(observations);
        final BearingData bearingDataNew = new BearingData(siteMetaData);

        return bearingTrainer.update(configuration, bearingDataOld, bearingDataNew, false, null);
    }

    @Override
    public boolean bleUpdateSnapshot(String siteName, List<SnapshotObservation> snapshotObservations, Set<String> sourceIds) {
        final List<SnapshotObservation> snapshotObservationListWithSelectedIds = TrainingHelper.updateSnapshotObservationsWithSelectedIds(sourceIds,
                snapshotObservations);
        final BearingData bearingDataOld = new BearingData(new SiteMetaData(siteName));
        final SiteMetaData siteMetaData = new SiteMetaData(siteName);
        siteMetaData.setSnapshotObservations(snapshotObservationListWithSelectedIds);
        final BearingData bearingDataNew = new BearingData(siteMetaData);
        final BearingConfiguration bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.SNAPSHOT_UPDATE);
        return bearingTrainer.update(bearingConfiguration, bearingDataOld, bearingDataNew, false, null);
    }

    private List<SnapshotObservation> getSnapshotObservationFromSnapshotItemWithSensorList(
            List<SnapshotObservation> snapshotObservations,
            List<SnapshotItemWithSensorType> snapshotItemWithSensorTypeList) {


        for (SnapshotObservation snapshotObservation : snapshotObservations) {
            for (BearingConfiguration.SensorType sensorType : this.trainingConfig.getWifiSensorTypes()) {
                final List<SnapshotItem> tempSnapshotItems = new ArrayList<>();
                if (snapshotObservation.getSensorType().equals(sensorType)) {
                    for (SnapshotItemWithSensorType snapshotItemWithSensorType : snapshotItemWithSensorTypeList) {

                        if (sensorType.equals(snapshotItemWithSensorType.getSensorType())) {
                            tempSnapshotItems.add(snapshotItemWithSensorType.getSnapshotItem());
                        }
                    }
                    snapshotObservation.setSnapShotItemList(tempSnapshotItems);
                    break;
                }
            }
        }

        return snapshotObservations;
    }

    @Override
    public void mergeSite(final String siteName, final IBearingTrainingCallback.IBearingSiteSignalMergeListener listener) {

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    final List<SnapshotObservation> observations = (List<SnapshotObservation>) message.obj;
                    final List<SnapshotItemWithSensorType> snapshotItemsWithSensorTypes = getSnapshotItemsWithSensorTypes(Collections.unmodifiableList(observations));
                    listener.onSuccess(siteName, observations, Collections.unmodifiableList(snapshotItemsWithSensorTypes));
                }
                return false;
            }
        });

        Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachesList = new HashMap<>();

        approachesList.put(BearingConfiguration.Approach.DATA_CAPTURE, this.trainingConfig.getWifiSensorTypes());

        final BearingConfiguration configuration =
                new BearingConfiguration(BearingConfiguration.OperationType.RESCAN_SIGNAL_FOR_DELTA, approachesList);
        BearingData bearingDataSite = new BearingData(new SiteMetaData(siteName));

        bearingTrainer.retrieve(configuration, bearingDataSite, false, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {

                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {

                    final List<SnapshotObservation> observations = bearingOutput.getBody().getSnapshotObservations();

                    if (observations != null) {
                        handler.sendMessage(handler.obtainMessage(SUCCESS, observations));
                    } else {
                        final Body body = bearingOutput.getBody();
                        if (body != null) {
                            handler.sendMessage(handler.obtainMessage(FAILURE, body.getErrorMessage()));
                        }
                    }

                } else {
                    final String errorMessage = ERROR + bearingOutput.getBody().getErrorMessage();
                    handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                }
            }
        });
    }


    @Override
    public void mergeBLESite(String site, IBearingTrainingCallback.IBearingBleSiteSignalMergeListener listener) {

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    final List<SnapshotObservation> observations = (List<SnapshotObservation>) message.obj;
                    final List<SnapshotItem> items = TrainingHelper.getBLESnapshotItems(observations);


                    for (SnapshotItem snapshotItem : items) {
                        Util.addLogs(Util.LOG_STATUS.DEBUG, LOG_TAG, "Item list : " + snapshotItem, null);
                    }
                    final List<ScannedBleDetails> bleSnapshotItems = TrainingHelper.getBLESourceIdsAsList(items);

                    listener.onSuccess(observations, Collections.unmodifiableList(bleSnapshotItems));
                }
                return false;
            }
        });

        final BearingData bearingDataForSignalRetrieve = new BearingData(new SiteMetaData(site));

        Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> map = new HashMap<>();
        map.put(BearingConfiguration.Approach.THRESHOLDING, trainingConfig.getBleSensorTypes());

        final BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.RESCAN_SIGNAL_FOR_DELTA, map);
        bearingTrainer.retrieve(configuration, bearingDataForSignalRetrieve, false, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {
                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {

                    final List<SnapshotObservation> observations = bearingOutput.getBody().getSnapshotObservations();

                    if (observations != null) {
                        handler.sendMessage(handler.obtainMessage(SUCCESS, observations));
                    } else {
                        final Body body = bearingOutput.getBody();
                        if (body != null) {
                            handler.sendMessage(handler.obtainMessage(FAILURE, body.getErrorMessage()));
                        }
                    }

                } else {
                    final String errorMessage = ERROR + bearingOutput.getBody().getErrorMessage();
                    handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                }
            }
        });
    }

    @Override
    public boolean siteUpdateOnMerge(String siteName, List<SnapshotObservation> snapshotObservations, List<SnapshotItemWithSensorType> snapshotItemWithSensorTypeList) {
        final List<SnapshotObservation> observations =
                getSnapshotObservationFromSnapshotItemWithSensorList(snapshotObservations, snapshotItemWithSensorTypeList);

        final BearingConfiguration configuration =
                new BearingConfiguration(BearingConfiguration.OperationType.SITE_UPDATE_ON_MERGE);
        final BearingData bearingDataOld = new BearingData(new SiteMetaData(siteName));
        final SiteMetaData siteMetaData = new SiteMetaData(siteName);
        siteMetaData.setSnapshotObservations(observations);
        final BearingData bearingDataNew = new BearingData(siteMetaData);

        return bearingTrainer.update(configuration, bearingDataOld, bearingDataNew, false, null);

    }

    @Override
    public void trainLocation(String siteName, String locationName, final IBearingTrainingCallback.IBearingOnLocationTrainAndRetrain listener) {


        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    final Integer progress = (Integer) message.obj;
                    listener.onSuccess(progress);
                }
                return false;
            }
        });

        final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachListHashMap = new HashMap<>();

        approachListHashMap.put(BearingConfiguration.Approach.DATA_CAPTURE, this.trainingConfig.getWifiSensorTypes());

        final BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.TRAIN_LOCATION, approachListHashMap);
        final LocationMetaData locationMetaData = new LocationMetaData(locationName);
        final SiteMetaData siteMetaData = new SiteMetaData(siteName);

        final List<LocationMetaData> locationMetaDataList = new ArrayList<>();
        locationMetaDataList.add(locationMetaData);
        siteMetaData.setLocationMetaData(locationMetaDataList);

        final BearingData bearingData = new BearingData(siteMetaData);

        bearingTrainer.create(configuration, bearingData, false, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {

                switch (bearingOutput.getHeader().getStatusCode()) {
                    case INTERNAL_ERROR:
                    case BAD_REQUEST:
                        handler.sendMessage(handler.obtainMessage(FAILURE, bearingOutput.getBody().getErrorMessage()));
                        break;
                    case OK:
                        handler.sendMessage(handler.obtainMessage(SUCCESS, 100));
                        break;
                    case MULTIPLE_RESPONSE:
                        Integer progress = Integer.parseInt(bearingOutput.getBody().getOutput());
                        handler.sendMessage(handler.obtainMessage(SUCCESS, progress));
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    public void retrainLocation(String siteName, String location, final IBearingTrainingCallback.IBearingOnLocationTrainAndRetrain listener) {

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    final Integer progress = (Integer) message.obj;
                    listener.onSuccess(progress);
                }
                return false;
            }
        });


        Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachListHashMap = new HashMap<>();
        approachListHashMap.put(BearingConfiguration.Approach.DATA_CAPTURE, this.trainingConfig.getWifiSensorTypes());
        BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.RETRAIN_LOCATION, approachListHashMap);


        final SiteMetaData siteMetaData = new SiteMetaData(siteName);
        final LocationMetaData locationMetaData = new LocationMetaData(location);

        final List<LocationMetaData> locationMetaDataList = new ArrayList<>();
        locationMetaDataList.add(locationMetaData);
        siteMetaData.setLocationMetaData(locationMetaDataList);

        final BearingData bearingData = new BearingData(siteMetaData);

        bearingTrainer.create(configuration, bearingData, false, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {

                switch (bearingOutput.getHeader().getStatusCode()) {
                    case INTERNAL_ERROR:
                    case BAD_REQUEST:
                        handler.sendMessage(handler.obtainMessage(FAILURE, bearingOutput.getBody().getErrorMessage()));
                        break;
                    case OK:
                        handler.sendMessage(handler.obtainMessage(SUCCESS, 100));
                        break;
                    case MULTIPLE_RESPONSE:
                        Integer progress = Integer.parseInt(bearingOutput.getBody().getOutput());
                        handler.sendMessage(handler.obtainMessage(SUCCESS, progress));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void uploadSite(String siteName, final IBearingTrainingCallback.IBearingOnUpload listener) {
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    listener.onSuccess();
                }
                return false;
            }
        });


        final BearingConfiguration configurationToUploadSite = new BearingConfiguration(BearingConfiguration.OperationType.UPLOAD_SITE);
        final BearingData bearingDataToUpload = new BearingData(new SiteMetaData(siteName));

        bearingTrainer.upload(configurationToUploadSite, bearingDataToUpload, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {

                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
                    handler.sendMessage(handler.obtainMessage(SUCCESS));
                } else {
                    final String errorMessage = ERROR + bearingOutput.getBody().getErrorMessage();
                    handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                }
            }
        });
    }

    @Override
    public void uploadLocations(String siteName, /*boolean blereTrained, boolean wifireTrained,*/ final IBearingTrainingCallback.IBearingOnUpload listener) {

        counter = 0;

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    counter++;
                    if (counter == 2) {
                        listener.onSuccess();
                    }
                }
                return false;
            }
        });

        final BearingSitedetails allLocationNamesForSiteFromLocal = getAllLocationNamesForSiteFromLocal(siteName);
        if (!allLocationNamesForSiteFromLocal.getBleLocationNames().isEmpty() /*&& blereTrained*/) {
            uploadBlethreshLocations(siteName, handler);
        } else {
            counter++;
        }

        if (!allLocationNamesForSiteFromLocal.getWifiLocationNames().isEmpty() /*&& wifireTrained*/) {
            uploadLocationCSVs(siteName, handler);
        } else {
            counter++;
        }

        if (counter == 2) {
            listener.onSuccess();
        }

    }

    private void uploadLocationCSVs(String siteName, Handler handler) {
        final BearingConfiguration configurationToUploadLocations = new BearingConfiguration(BearingConfiguration.OperationType.UPLOAD_LOCATION_CSV);
        final BearingData bearingDataToUploadLocations = new BearingData(new SiteMetaData(siteName));

        bearingTrainer.upload(configurationToUploadLocations, bearingDataToUploadLocations, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {
                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
                    handler.sendMessage(handler.obtainMessage(SUCCESS));
                } else {

                    final String errorMessage = ERROR + bearingOutput.getBody().getErrorMessage();
                    handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                }
            }
        });
    }

    private void uploadBlethreshLocations(String siteName, final Handler handler) {

        final BearingConfiguration configurationToUploadLocations = new BearingConfiguration(BearingConfiguration.OperationType.UPLOAD_SITE_THRESH_DATA);
        final BearingData bearingDataToUploadLocations = new BearingData(new SiteMetaData(siteName));

        bearingTrainer.upload(configurationToUploadLocations, bearingDataToUploadLocations, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {
                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
                    handler.sendMessage(handler.obtainMessage(SUCCESS));
                } else {
                    final String errorMessage = ERROR + bearingOutput.getBody().getErrorMessage();
                    handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                }
            }
        });
    }

    @Override
    public void generateClaasifier(String sitename, final IBearingTrainingCallback.IBearingOnUpload listener) {

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    listener.onSuccess();
                }
                return false;
            }
        });

        Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachListMap =
                new HashMap<>();
        List<BearingConfiguration.SensorType> sensorTypesList = new ArrayList<>();
        sensorTypesList.add(BearingConfiguration.SensorType.ST_WIFI);

        approachListMap.put(BearingConfiguration.Approach.FINGERPRINT, sensorTypesList);

        final BearingConfiguration configurationFingerPrint = new BearingConfiguration(BearingConfiguration.OperationType.TRAIN_SITE, approachListMap);
        final BearingData bearingDataFingerPrint = new BearingData(new SiteMetaData(sitename));

        bearingTrainer.create(configurationFingerPrint, bearingDataFingerPrint, true, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {

                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
                    handler.sendMessage(handler.obtainMessage(SUCCESS));
                } else {
                    final String errorMessage = ERROR + bearingOutput.getBody().getErrorMessage();
                    handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                }
            }
        });
    }

    @Override
    public void uploadSiteLocations(final String siteName, /*boolean blereTrained, boolean wifireTrained,*/ final IBearingTrainingCallback.IBearingOnUpload listener) {
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    listener.onSuccess();
                }
                return false;
            }
        });

        uploadSite(siteName, new IBearingTrainingCallback.IBearingOnUpload() {
            @Override
            public void onSuccess() {
                uploadLocations(siteName, /*blereTrained, wifireTrained,*/ new IBearingTrainingCallback.IBearingOnUpload() {
                    @Override
                    public void onSuccess() {
                        handler.sendMessage(handler.obtainMessage(SUCCESS));
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
            }
        });
    }

    @Override
    public void uploadSiteLocationAndGenerateClassifier(final String siteName, final IBearingTrainingCallback.IBearingOnUpload listener) {
       /* final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    listener.onSuccess();
                }
                return false;
            }
        });

        uploadSite(siteName, new IBearingTrainingCallback.IBearingOnUpload() {
            @Override
            public void onSuccess() {
                uploadLocations(siteName, new IBearingTrainingCallback.IBearingOnUpload() {
                    @Override
                    public void onSuccess() {
                        generateClaasifier(siteName, new IBearingTrainingCallback.IBearingOnUpload() {
                            @Override
                            public void onSuccess() {
                                handler.sendMessage(handler.obtainMessage(SUCCESS));
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
            }
        });*/
    }

    @Override
    public void downloadAllSitesAndLocationsFromServer(final IBearingTrainingCallback.IBearingSuncWithServerListener listener) {
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    listener.onSuccess();
                }
                return false;
            }
        });

        final BearingConfiguration allSitesBearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.READ_SITE_LIST);
        bearingTrainer.retrieve(allSitesBearingConfiguration, null, true, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {

                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {

                    final Body body = bearingOutput.getBody();
                    final List<String> siteNamesList = body.getResponseList();

                    if (siteNamesList != null && !siteNamesList.isEmpty()) {
                        final LinkedList<String> siteNames = new LinkedList<>(siteNamesList);

                        siteCounter = 0;
                        downloadAllLocationsForSite(siteNames, listener, handler);


                    } else {
                        final String name = "No sites found";
                        handler.sendMessage(handler.obtainMessage(FAILURE, name));
                    }
                } else {
                    final String errorMessage = ERROR + bearingOutput.getBody().getErrorMessage();
                    handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                }
            }
        });

    }


    public void downloadAllLocationsForSite(final LinkedList<String> siteNames,
                                            final IBearingTrainingCallback.IBearingSuncWithServerListener listener,
                                            final Handler handler) {
        if (siteCounter > -1 && siteCounter < siteNames.size()) {

            final BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.READ_LOC_LIST);
            final String siteName = siteNames.get(siteCounter);
            final BearingData bearingData = new BearingData(new SiteMetaData(siteName));

            bearingTrainer.retrieve(configuration, bearingData, true, new BearingCallBack() {
                @Override
                public void onLocationResponse(BearingOutput bearingOutput) {
                    if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {

                        downloadAllBleLocationsForSite(siteName, handler);

                        siteCounter++;
                        downloadAllLocationsForSite(siteNames, listener, handler);

                        if (siteCounter == siteNames.size()) {
                            handler.sendMessage(handler.obtainMessage(SUCCESS));
                        }

                    } else {

                        final String errorMessage = ERROR + bearingOutput.getBody().getErrorMessage();
                        handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                    }
                }
            });
        }
    }

    private void downloadAllBleLocationsForSite(String siteName, Handler handler) {
        final BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.READ_THRESH_LIST);
        final BearingData bearingData = new BearingData(new SiteMetaData(siteName));

        bearingTrainer.retrieve(configuration, bearingData, true, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {
                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {

                    handler.sendMessage(handler.obtainMessage(SUCCESS));

                } else {

                    final String errorMessage = ERROR + bearingOutput.getBody().getErrorMessage();
//                    handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                    Util.addLogs(Util.LOG_STATUS.ERROR, LOG_TAG, " Error : " + errorMessage, null);
                }
            }
        });
    }

    public Set<String> getAllSiteNamesFromLocal() {
        final Set<String> siteNames = new HashSet<>();

        final BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.READ_SITE_LIST);
        final BearingOutput bearingOutput = bearingTrainer.retrieve(configuration, null, false, null);

        if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
            final List<String> siteList = bearingOutput.getBody().getResponseList();
            if (siteList != null) {
                siteNames.addAll(siteList);
            }
        }

        return siteNames;
    }

    @Override
    public void getAllSiteNamesFromServer(final IBearingTrainingCallback.IUtilityGetSiteListListenerFromServer listener) {
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    final Set<String> set = (HashSet<String>) message.obj;
                    listener.onSuccess(Collections.unmodifiableSet(set));
                }
                return false;
            }
        });

        final BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.READ_SITE_LIST_ON_SERVER);
        bearingTrainer.retrieve(configuration, null, true, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {

                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {

                    final List<String> siteList = bearingOutput.getBody().getResponseList();
                    if (siteList != null) {
                        handler.sendMessage(handler.obtainMessage(SUCCESS, new HashSet<String>(siteList)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(SUCCESS, new HashSet<String>()));
                    }

                } else {
                    final String errorMessage = ERROR + bearingOutput.getBody().getErrorMessage();
                    handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                }
            }
        });
    }

    @Override
    public void downloadSiteAndLocations(String siteName, IBearingTrainingCallback.IDownloadSiteLocationsListener listener) {
        Log.d("NOTFOUND", "APINOTFOUND");
    }

    @Override
    public BearingSitedetails getAllLocationNamesForSiteFromLocal(String siteName) {

        final BearingSitedetails bearingSitedetails = new BearingSitedetails();
        bearingSitedetails.setSiteName(siteName);

//        final Set<String> locationNames = new HashSet<>();

        BearingData bearingData = new BearingData(new SiteMetaData(siteName));

        BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.READ_LOC_LIST);
        final BearingOutput bearingOutput = bearingTrainer.retrieve(configuration, bearingData, false, null);
        if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
            List<String> locationList = bearingOutput.getBody().getResponseList();
            if (locationList != null) {
//                locationNames.addAll(locationList);
                bearingSitedetails.setWifiLocationNames(new HashSet<>(locationList));
            } else {
                bearingSitedetails.setWifiLocationNames(new HashSet<>());
            }
        }

        BearingConfiguration configuration2 = new BearingConfiguration(BearingConfiguration.OperationType.READ_THRESH_LIST);
        BearingOutput bearingOutput2 = bearingTrainer.retrieve(configuration2, bearingData, false, null);
        if (bearingOutput2.getHeader().getStatusCode().equals(StatusCode.OK)) {
            final List<String> locationList = bearingOutput2.getBody().getResponseList();
            if (locationList != null) {
//                locationNames.addAll(locationList);
                bearingSitedetails.setBleLocationNames(new HashSet<>(locationList));
            } else {
                bearingSitedetails.setBleLocationNames(new HashSet<>());
            }
        }

        return bearingSitedetails;
    }

//    private BearingSitedetails getAllLocationNamesForSiteFromLocal(String siteName) {
//
//        final BearingSitedetails bearingSitedetails = new BearingSitedetails();
//        bearingSitedetails.setSiteName(siteName);
//
////        final Set<String> locationNames = new HashSet<>();
//
//        BearingData bearingData = new BearingData(new SiteMetaData(siteName));
//
//        BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.READ_LOC_LIST);
//        final BearingOutput bearingOutput = bearingTrainer.retrieve(configuration, bearingData, false, null);
//        if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
//            List<String> locationList = bearingOutput.getBody().getResponseList();
//            if (locationList != null) {
////                locationNames.addAll(locationList);
//                bearingSitedetails.setWifiLocationNames(new HashSet<>(locationList));
//            } else {
//                bearingSitedetails.setWifiLocationNames(new HashSet<>());
//            }
//        }
//
//        BearingConfiguration configuration2 = new BearingConfiguration(BearingConfiguration.OperationType.READ_THRESH_LIST);
//        BearingOutput bearingOutput2 = bearingTrainer.retrieve(configuration2, bearingData, false, null);
//        if (bearingOutput2.getHeader().getStatusCode().equals(StatusCode.OK)) {
//            final List<String> locationList = bearingOutput2.getBody().getResponseList();
//            if (locationList != null) {
////                locationNames.addAll(locationList);
//                bearingSitedetails.setBleLocationNames(new HashSet<>(locationList));
//            } else {
//                bearingSitedetails.setBleLocationNames(new HashSet<>());
//            }
//        }
//
//        return bearingSitedetails;
//    }

    @Override
    public void getAllLocationNamesForSiteFromServer(String siteName, final IBearingTrainingCallback.IUtilityGetLocationListListenerFromServer listener) {
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    final Set<String> set = (HashSet<String>) message.obj;
                    listener.onSuccess(Collections.unmodifiableSet(set));
                }
                return false;
            }
        });

        BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.READ_LOC_LIST_ON_SERVER);
        BearingData bearingData = new BearingData(new SiteMetaData(siteName));
        bearingTrainer.retrieve(configuration, bearingData, true, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {
                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
                    List<String> locationList = bearingOutput.getBody().getResponseList();
                    if (locationList != null) {
                        handler.sendMessage(handler.obtainMessage(SUCCESS, new HashSet<String>(locationList)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(SUCCESS, new HashSet<String>()));
                    }

                } else {
                    final String errorMessage = ERROR + bearingOutput.getBody().getErrorMessage();
                    handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                }
            }
        });
    }

    /*
    * For temparary fix
    * // TODO change the implementation
    * */
    @Override
    public void deleteBearingData(String siteName, List<String> locations, IBearingTrainingCallback.IBearingDataDelete listener) {
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    listener.onFailure();
                } else if (message.what == SUCCESS) {
                    listener.onSuccess();
                }
                return false;
            }
        });

        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        boolean status = false;


        if (siteName == null && locations == null) {
            final Set<String> sites = getAllSiteNamesFromLocal();
            if (sites.isEmpty()) {
                status = true;
            } else {
                for (String site : sites) {
                    status = persistenceHandler.deleteDataPersistenceSpace(site);
                    if (!status)
                        break;
                }
            }
        } else if (locations != null && siteName != null) {
            for (String location : locations) {
                status = persistenceHandler.deleteDataPersistenceSpace(siteName, location);
                if (!status)
                    break;
            }
        } else {
            status = persistenceHandler.deleteDataPersistenceSpace(siteName);
        }


        if (status) {
            handler.sendMessage(handler.obtainMessage(SUCCESS));
        } else {
            handler.sendMessage(handler.obtainMessage(FAILURE));
        }

    }

    @Override
    public Set<ScannedBleDetails> getBleIds(String siteName) {
        BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.SNAPSHOT_FETCH);
        BearingData bearingData = new BearingData(new SiteMetaData(siteName));
        BearingOutput bearingOutput = bearingTrainer.retrieve(configuration, bearingData, false, null);
        if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
            final List<SnapshotObservation> snapshotObservations = bearingOutput.getBody().getSnapshotObservations();
            final List<SnapshotItem> bleSnapshotItems = TrainingHelper.getBLESnapshotItems(snapshotObservations);
            final List<ScannedBleDetails> bleSourceIdsAsList = TrainingHelper.getBLESourceIdsAsList(bleSnapshotItems);
            return new HashSet<>(bleSourceIdsAsList);
        }
        return new HashSet<>();
    }

    @Override
    public void trainBleLocation(String siteName, String locationName, double bleThreshold, String bleId, IBearingTrainingCallback.ITrainBleLocation listener) {
        if (!TrainingHelper.matchesRSSIThresholdFormat(bleThreshold)) {
            throw new IllegalStateException("Threshold value should be in between -100 to 0");
        }


        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    listener.onSuccess();
                }
                return false;
            }
        });

        final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachListHashMap = new HashMap<>();
        approachListHashMap.put(BearingConfiguration.Approach.THRESHOLDING, trainingConfig.getBleSensorTypes());

        final BearingConfiguration bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.TRAIN_LOCATION, approachListHashMap);
        final LocationMetaData locationMetaData = new LocationMetaData(locationName);

        locationMetaData.setSnapshotItemList(TrainingHelper.getBLESnapshotItemListForThreshold(bleId, bleThreshold));

        final List<LocationMetaData> locationMetaDataList = new ArrayList<>();
        locationMetaDataList.add(locationMetaData);

        final SiteMetaData siteMetaData = new SiteMetaData(siteName);
        siteMetaData.setLocationMetaData(locationMetaDataList);

        final BearingData bearingData = new BearingData(siteMetaData);

        bearingTrainer.create(bearingConfiguration, bearingData, false, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {
                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
                    handler.sendMessage(handler.obtainMessage(SUCCESS));
                } else {
                    final String errorMessage = ERROR + bearingOutput.getBody().getErrorMessage();
                    handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                }
            }
        });

    }

    @Override
    public void retrainBleLocation(String siteName, String locationName, double bleThreshold, String bleId, IBearingTrainingCallback.ITrainBleLocation listener) {
        if (!TrainingHelper.matchesRSSIThresholdFormat(bleThreshold)) {
            throw new IllegalStateException("Threshold value should be in between -100 to 0");
        }


        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FAILURE) {
                    final String errorMessage = (String) message.obj;
                    listener.onFailure(errorMessage);
                } else if (message.what == SUCCESS) {
                    listener.onSuccess();
                }
                return false;
            }
        });

        final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachListHashMap = new HashMap<>();
        approachListHashMap.put(BearingConfiguration.Approach.THRESHOLDING, trainingConfig.getBleSensorTypes());

        final BearingConfiguration bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.RETRAIN_LOCATION, approachListHashMap);
        final LocationMetaData locationMetaData = new LocationMetaData(locationName);

        locationMetaData.setSnapshotItemList(TrainingHelper.getBLESnapshotItemListForThreshold(bleId, bleThreshold));

        final List<LocationMetaData> locationMetaDataList = new ArrayList<>();
        locationMetaDataList.add(locationMetaData);

        final SiteMetaData siteMetaData = new SiteMetaData(siteName);
        siteMetaData.setLocationMetaData(locationMetaDataList);

        final BearingData bearingData = new BearingData(siteMetaData);

        bearingTrainer.create(bearingConfiguration, bearingData, false, new BearingCallBack() {
            @Override
            public void onLocationResponse(BearingOutput bearingOutput) {
                if (bearingOutput.getHeader().getStatusCode().equals(StatusCode.OK)) {
                    handler.sendMessage(handler.obtainMessage(SUCCESS));
                } else {
                    final String errorMessage = ERROR + bearingOutput.getBody().getErrorMessage();
                    handler.sendMessage(handler.obtainMessage(FAILURE, errorMessage));
                }
            }
        });
    }

    @Override
    public List<BearingConfiguration.SensorType> getSensorTypes(String siteName) {
        BearingConfiguration configuration = new BearingConfiguration(BearingConfiguration.OperationType.SNAPSHOT_FETCH);

        final BearingData bearingData = new BearingData(new SiteMetaData(siteName));

        final BearingOutput bearingOutput = bearingTrainer.retrieve(configuration, bearingData, false, null);
        final List<SnapshotObservation> snapshotObservations = bearingOutput.getBody().getSnapshotObservations();

        final List<BearingConfiguration.SensorType> sensorTypes = new ArrayList<>();

        for (SnapshotObservation snapshotObservation : snapshotObservations) {
            sensorTypes.add(snapshotObservation.getSensorType());
        }

        return sensorTypes;
    }

    public Map<String, String> getBleMappingForLocation(String siteName) {

        PersistenceHandler handler = new PersistenceHandler(DataStore.StoreType.FILE);
        return handler.getBeaconIdToLocationMap(siteName);
    }

    public Map<String, Map<Double, List<String>>> getBleAndthreshMappingForLocation(String siteName) {

        PersistenceHandler handler = new PersistenceHandler(DataStore.StoreType.FILE);
        return handler.readThreshSourceIdMap(siteName);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new UnsupportedOperationException("This operation is not supported ");
    }
}
