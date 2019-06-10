package com.bosch.pai.bearing.core.util;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.LocationMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.SiteMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.BearingMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Util class for parsing the input request to the desired format
 */
public final class BearingRequestParser {


    private BearingRequestParser() {

    }

    /**
     * Parse bearing data for site or location name string.
     *
     * @param bearingData the bearing data
     * @param mode        the mode
     * @return the string
     */
    public static String parseBearingDataForSiteOrLocationName(BearingData bearingData, BearingMode mode) {
        if (bearingData == null) {
            return null;
        }

        final SiteMetaData siteMetaData = bearingData.getSiteMetaData();
        if (mode == BearingMode.SITE) {
            if (siteMetaData != null) {
                return siteMetaData.getSiteName();
            }
            return null;
        } else if (mode == BearingMode.LOCATION) {
            if (siteMetaData == null) {
                return null;
            }

            final List<LocationMetaData> locationMetaDataList = siteMetaData.getLocationMetaData();
            if (locationMetaDataList == null || locationMetaDataList.isEmpty()) {
                return null;
            }

            if (locationMetaDataList.get(0) == null) {
                return null;
            }

            return locationMetaDataList.get(0).getName();
        }
        return null;
    }


    /**
     * Parse bearing data for site snapshot observations list.
     *
     * @param bearingData the bearing data
     * @return the list
     */
    public static List<SnapshotObservation> parseBearingDataForSiteSnapshotObservations(BearingData bearingData) {

        if (bearingData == null) {
            return Collections.emptyList();
        }

        final SiteMetaData siteMetaData = bearingData.getSiteMetaData();
        if (siteMetaData != null) {
            return siteMetaData.getSnapshotObservations();
        }

        return Collections.emptyList();
    }


    /**
     * Parse bearing data for snapshot observation list.
     *
     * @param bearingData the bearing data
     * @param bearingMode the bearing mode
     * @return the list
     */
    public static List<SnapshotObservation> parseBearingDataForSnapshotObservation(BearingData bearingData, BearingMode bearingMode) {

        if (bearingData == null) {
            return Collections.emptyList();
        }
        final SiteMetaData siteMetaData = bearingData.getSiteMetaData();
        if (bearingMode == BearingMode.SITE) {
            if (siteMetaData != null) {
                return siteMetaData.getSnapshotObservations();
            }
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }


    /**
     * Parse bearingConfiguration for operation type bearingConfiguration . operation type.
     *
     * @param bearingConfiguration the bearingConfiguration
     * @return the bearingConfiguration . operation type
     */
    @Nullable
    public static BearingConfiguration.OperationType parseConfigurationForOperationType(BearingConfiguration bearingConfiguration) {
        final BearingConfiguration.OperationType operationType = bearingConfiguration.getOperationType();
        if (operationType == null) {
            return null;
        }

        return operationType;
    }


    /**
     * Gets sensor list.
     *
     * @param bearingConfiguration the bearingConfiguration
     * @param approach      the approach
     * @return the sensor list
     */
    public static List<BearingConfiguration.SensorType> getSensorList(BearingConfiguration bearingConfiguration, BearingConfiguration.Approach approach) {
        final List<BearingConfiguration.SensorType> sensorOptionList = bearingConfiguration.getApproachSensorListMap().get(approach);
        if (sensorOptionList == null || sensorOptionList.isEmpty()) {
            return Collections.emptyList();
        } else {
            return sensorOptionList;
        }

    }

    /**
     * There is a difference in the way BearingData is parsed for Training and detection
     * In Detection , if there is no siteData content ,then it means its detection for site ,
     * else it means its Location Detection
     *
     * @param bearingData the bearing data
     * @return the bearing mode for detection
     */
    @Nullable
    public static BearingMode getBearingModeForDetection(BearingData bearingData) {
        if (bearingData == null) {
            return BearingMode.SITE;
        }
        final SiteMetaData siteMetaData = bearingData.getSiteMetaData();
        if (siteMetaData == null) {
            return null;
        }
        final String siteName = siteMetaData.getSiteName();
        if (siteName == null || siteName.isEmpty()) {
            return BearingMode.SITE;
        }

        return BearingMode.LOCATION;

    }

    /**
     * In training , when siteMeta data is passed with SiteName as String, it means it is for site creation
     * When locationMeta data os passed with siteName and loactionMeta Data ,it means it is for location creation
     *
     * @param bearingData the bearing data
     * @return the bearing mode for training
     */
    @Nullable
    public static BearingMode getBearingModeForTraining(BearingData bearingData) {
        if (bearingData == null) {
            return BearingMode.SITE;
        }
        final SiteMetaData siteMetaData = bearingData.getSiteMetaData();
        if (siteMetaData == null) {
            return null;
        }

        final String siteName = siteMetaData.getSiteName();
        if (siteName == null || siteName.isEmpty()) {
            return BearingMode.SITE;
        }

        final List<LocationMetaData> locationMetaDataList = siteMetaData.getLocationMetaData();
        if (locationMetaDataList == null || locationMetaDataList.isEmpty()) {
            return BearingMode.SITE;
        }

        return BearingMode.LOCATION;

    }


    /**
     * Gets location meta data list.
     *
     * @param bearingData the bearing data
     * @return the location meta data list
     */
    @Nullable
    public static List<LocationMetaData> getLocationMetaDataList(BearingData bearingData) {
        final SiteMetaData siteMetaData = bearingData.getSiteMetaData();
        if (siteMetaData == null) {
            return Collections.emptyList();
        }
        final List<LocationMetaData> locationMetaData = siteMetaData.getLocationMetaData();
        if (locationMetaData == null || locationMetaData.isEmpty()) {
            return Collections.emptyList();
        }
        return locationMetaData;

    }

    /**
     * Gets approach.
     *
     * @param bearingConfiguration the bearingConfiguration
     * @return the approach
     */
    @NonNull
    public static Set<BearingConfiguration.Approach> getApproach(BearingConfiguration bearingConfiguration) {
        final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachSensorListMap = bearingConfiguration.getApproachSensorListMap();
        if (approachSensorListMap == null) {
            return Collections.emptySet();
        }
        return approachSensorListMap.keySet();
    }


    /**
     * Validate sensor type list list.
     *
     * @param bearingConfiguration the bearingConfiguration
     * @return the list
     */
    @Nullable
    public static List<BearingConfiguration.SensorType> validateSensorTypeList(BearingConfiguration bearingConfiguration) {
        final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachSensorListMap = bearingConfiguration.getApproachSensorListMap();
        final Set<BearingConfiguration.Approach> approaches = approachSensorListMap.keySet();

        for (BearingConfiguration.Approach approach : approaches) {
            final List<BearingConfiguration.SensorType> sensorTypes = approachSensorListMap.get(approach);
            if (sensorTypes == null) {
                return null;
            }
        }

        return new ArrayList<>();
    }


    /**
     * Parse bearingConfiguration sensor for approach list.
     *
     * @param bearingConfiguration the bearingConfiguration
     * @param approach      the approach
     * @return the list
     */
    public static List<BearingConfiguration.SensorType> parseConfigurationSensorForApproach(BearingConfiguration bearingConfiguration, BearingConfiguration.Approach approach) {
        final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachSensorListMap = bearingConfiguration.getApproachSensorListMap();
        final List<BearingConfiguration.SensorType> sensorOptionList = approachSensorListMap.get(approach);
        if (sensorOptionList == null || sensorOptionList.isEmpty()) {
            return Collections.emptyList();
        } else {
            return sensorOptionList;
        }
    }


    /**
     * Parse bearingConfiguration for approach list set.
     *
     * @param bearingConfiguration the bearingConfiguration
     * @return the set
     */
    @NonNull
    public static Set<BearingConfiguration.Approach> parseConfigurationForApproachList(BearingConfiguration bearingConfiguration) {

        if (bearingConfiguration.getApproachSensorListMap() == null) {
            return Collections.emptySet();
        }
        final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> approachSensorListMap = bearingConfiguration.getApproachSensorListMap();
        return approachSensorListMap.keySet();
    }


    /**
     * Parse bearing data for floor count int.
     *
     * @param bearingData the bearing data
     * @return the int
     */
    public static int parseBearingDataForFloorCount(BearingData bearingData) {
        if (bearingData == null || bearingData.getSiteMetaData() == null) {
            return -1;
        }
        SiteMetaData siteMetaData = bearingData.getSiteMetaData();
        return siteMetaData.getNumberOfFloors();
    }


}
