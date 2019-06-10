package com.bosch.pai.ipsadmin.bearing.sensordatastore.util;


import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.Snapshot;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.entity.Sensor;
import com.bosch.pai.bearing.entity.Site;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ClientUtil {

    private static final String TAG = ClientUtil.class.getSimpleName();

    private ClientUtil() {
        //To hide the public constructor
    }

    /**
     * Gets property.
     *
     * @param key the key
     * @return the property
     * @throws IOException the io exception
     */
    public static String getProperty(String key) throws IOException {
        ConfigurationSettings configurationSettings = ConfigurationSettings.getConfiguration();
        final String serverURL = configurationSettings.getServerURL();
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "getProperty: " + serverURL);
        return serverURL;
    }

    /**
     * Create server site from trained site data site.
     *
     * @param siteName        the site name
     * @param snapshotForSite the snapshot for site
     * @return the site
     */
    public static Site createServerSiteFromTrainedSiteData(String siteName, Snapshot snapshotForSite) {
        Site site = new Site();
        //site.setActive(true);
        site.setDocVersion(snapshotForSite.getDocVersion());
        site.setNoOfFloors(snapshotForSite.getNoOfFloors());
        //site.setEpochMilliSeconds(new Long(trainedSiteData.getTimestamp()));
        site.setSchemaVersion(snapshotForSite.getSchemaVersion());
        site.setSiteName(siteName);
        site.setRssiThreshhold(snapshotForSite.getRssiThreshHold());
        site.setProbabilityPercentage(snapshotForSite.getProbabilityPercentage());
        List<Sensor> sensors = new ArrayList<>();
        List<SnapshotObservation> snapshotObservationList = snapshotForSite.getSensors();
        for (SnapshotObservation snapshotObservation : snapshotObservationList) {
            Sensor sensor = new Sensor();
            sensor.setDetectionLevel(BearingConfiguration.DetectionLevel.valueOf(snapshotObservation.getDetectionLevel().toString()));
            String sensorType;
            if (snapshotObservation.getSensorType() == null) {
                sensorType = BearingConfiguration.SensorType.ST_WIFI.toString();
                sensor.setSensorType(BearingConfiguration.SensorType.valueOf(sensorType));
            }
            sensor.setSensorType(BearingConfiguration.SensorType.valueOf(snapshotObservation.getSensorType().toString()));
            List<com.bosch.pai.bearing.entity.SnapShotItem> listOfSnapshotItem = new ArrayList<>();
            for (SnapshotItem snapShotItem : snapshotObservation.getSnapShotItemList()) {
                com.bosch.pai.bearing.entity.SnapShotItem snapShotItemEntity = new com.bosch.pai.bearing.entity.SnapShotItem();
                snapShotItemEntity.setCustomFields(Arrays.asList(snapShotItem.getCustomField()));
                List<Double> listOfMeasuredValues = new ArrayList<>();
                for (int i = 0; i < snapShotItem.getMeasuredValues().length; i++) {
                    listOfMeasuredValues.add(snapShotItem.getMeasuredValues()[i]);
                }
                snapShotItemEntity.setMeasuredValues(listOfMeasuredValues);
                snapShotItemEntity.setSourceId(snapShotItem.getSourceId());
                listOfSnapshotItem.add(snapShotItemEntity);
            }
            sensor.setSnapShotItemList(listOfSnapshotItem);
            sensors.add(sensor);
        }
        site.setSensors(sensors);
        return site;
    }


}
