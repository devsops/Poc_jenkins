package com.bosch.pai.ipsadmin.bearing.train.operations;


import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.ipsadmin.bearing.core.BearingHandler;
import com.bosch.pai.ipsadmin.bearing.core.event.GenerateClassifierEvent;
import com.bosch.pai.ipsadmin.bearing.core.event.ThreshDataEntryEvent;
import com.bosch.pai.ipsadmin.bearing.core.util.BearingRequestParser;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.LocationMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.enums.BearingMode;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.event.DataCaptureRequestEvent;
import com.bosch.pai.ipsadmin.bearing.train.errorcode.Codes;

import java.util.ArrayList;
import java.util.List;


/**
 * Create api triggers Site , Location and Cell hierarchy creation..
 * <p>
 * ***************************************************
 * The site creation api captures the site snapshot.
 * syncWithServer ->false
 * OperationType - > TRAIN_SITE
 * APPROACH       ->DATA_CAPTURE
 * ****************************************************
 * The location creation api captures the location csv.
 * syncWithServer ->false
 * OperationTypr -> TRAIN_LOCATION
 * APPROACH      ->DATA_CAPTURE
 * <p>
 * ******************************************************
 * The Cell Hierarchy creation for th site can be triggered both on local and on server.
 * The server triggered for cell hierarchy created is triggered with
 * syncWithServer -> true
 * OperationType ->TRAIN_SITE
 * APPROACH      ->FINGERPRINT(for fingerprinting)/ THRESHOLDING (for BLE Thresholding)
 * *******************************************************
 * <p>
 * Each create api will return a validation for the input synchronously. All callbacks are asynchronous.
 * The content of BearingData is used to understand if the api is meant for site or for a location.
 * If the BearingData contains siteMetaData ,then the data is used to create a site.
 * If the BearingData contains siteMetaData and locationMetaData , then the data is used to create a location.
 */
public class Create {
    private BearingHandler bearingHandler;

    /**
     * Instantiates a new Create.
     *
     * @param bearingHandler the bearing handler
     */
    public Create(BearingHandler bearingHandler) {
        this.bearingHandler = bearingHandler;
    }


    /**
     * Trigger site creation int.
     *
     * @param requestID       the request id
     * @param bearingConfiguration   the bearingConfiguration
     * @param bearingData     the bearing data
     * @param bearingApproach the bearing approach
     * @param syncWithServer  the sync with server
     * @param bearingCallBack the bearing call back
     * @return the int
     */
    public int triggerSiteCreation(String requestID, BearingConfiguration bearingConfiguration, BearingData bearingData, BearingConfiguration.Approach bearingApproach, boolean syncWithServer, BearingCallBack bearingCallBack) {
        final String siteName = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingData, BearingMode.SITE);
        if (bearingApproach.equals(BearingConfiguration.Approach.DATA_CAPTURE) ||
                bearingApproach.equals(BearingConfiguration.Approach.THRESHOLDING)) {
            return triggerDataCaptureEventForSite(requestID, siteName, bearingConfiguration, bearingData, bearingApproach, bearingCallBack);
        } else {
            return generateClassifierEventForCellHierarchy(requestID, siteName, syncWithServer, bearingApproach, bearingCallBack);
        }
    }

    /**
     * Trigger location creation int.
     *
     * @param requestID       the request id
     * @param bearingConfiguration   the bearingConfiguration
     * @param bearingData     the bearing data
     * @param bearingApproach the bearing approach
     * @param syncWithServer  the sync with server
     * @param bearingCallBack the bearing call back
     * @return the int
     */
    public int triggerLocationCreation(String requestID, BearingConfiguration bearingConfiguration, BearingData bearingData, BearingConfiguration.Approach bearingApproach, boolean syncWithServer, BearingCallBack bearingCallBack) {
        final String siteName = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingData, BearingMode.SITE);
        if (bearingApproach.equals(BearingConfiguration.Approach.DATA_CAPTURE)) {
            return triggerDataCaptureEventForLocation(requestID, siteName, bearingConfiguration, bearingData, bearingApproach, bearingCallBack);
        } else if (bearingApproach.equals(BearingConfiguration.Approach.THRESHOLDING)) {
            return triggerDataEntryForThreshLocation(requestID, bearingConfiguration, bearingData, bearingApproach, siteName, bearingCallBack);
        }
        return Codes.BAD_REQUEST;
    }


    private int generateClassifierEventForCellHierarchy(String requestID, String siteName, boolean syncWithServer, BearingConfiguration.Approach approach, BearingCallBack bearingCallBack) {

        final GenerateClassifierEvent generateClassifierEvent = new GenerateClassifierEvent(requestID, EventType.TRIGGER_TRAINING, approach, bearingCallBack);
        generateClassifierEvent.setSiteName(siteName);
        generateClassifierEvent.setApproach(approach);
        generateClassifierEvent.setGenerateOnServer(syncWithServer);
        bearingHandler.enqueue(requestID, generateClassifierEvent, EventType.TRIGGER_TRAINING);
        return Codes.RESPONSE_OK;
    }


    private int triggerDataCaptureEventForSite(String requestID, String siteName, BearingConfiguration bearingConfiguration, BearingData bearingData, BearingConfiguration.Approach approach, BearingCallBack bearingCallBack) {

        final List<BearingConfiguration.SensorType> sensorTypeList = BearingRequestParser.getSensorList(bearingConfiguration, approach);

        final DataCaptureRequestEvent dataCaptureRequestEvent = new DataCaptureRequestEvent(requestID, EventType.CAPTURE_DATA_EVENT, bearingCallBack);
        dataCaptureRequestEvent.setSiteName(siteName);
        dataCaptureRequestEvent.setSensors(sensorTypeList);
        dataCaptureRequestEvent.setSite(true);
        if (bearingConfiguration.isAutoMergeEnable()) {
            dataCaptureRequestEvent.setSiteMerge(true);
            dataCaptureRequestEvent.setAutoMergeEnable(true);
        }
        /*Check the default number of locations. If the number of locations is less that 2, then set the default as 2*/
        final int noOfFloors = BearingRequestParser.parseBearingDataForFloorCount(bearingData) < 2 ? 2 : BearingRequestParser.parseBearingDataForFloorCount(bearingData);
        dataCaptureRequestEvent.setNoOfFloors(noOfFloors);
        dataCaptureRequestEvent.setApproach(approach);
        bearingHandler.enqueue(requestID, dataCaptureRequestEvent, EventType.CAPTURE_DATA_EVENT);
        return Codes.RESPONSE_OK;
    }


    private int triggerDataCaptureEventForLocation(String requestID, String siteName, BearingConfiguration bearingConfiguration, BearingData bearingData, BearingConfiguration.Approach approach, BearingCallBack bearingCallBack) {

        String locationName = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingData, BearingMode.LOCATION);
        final List<BearingConfiguration.SensorType> sensorTypeList = BearingRequestParser.getSensorList(bearingConfiguration, approach);

        final DataCaptureRequestEvent dataCaptureRequestEvent = new DataCaptureRequestEvent(requestID, EventType.CAPTURE_DATA_EVENT, bearingCallBack);
        dataCaptureRequestEvent.setSiteName(siteName);
        final List<String> locationNames = new ArrayList<>();
        locationNames.add(locationName);
        dataCaptureRequestEvent.setLocations(locationNames);
        dataCaptureRequestEvent.setSensors(sensorTypeList);
        dataCaptureRequestEvent.setSite(false);
        dataCaptureRequestEvent.setAutoMergeEnable(bearingConfiguration.isAutoMergeEnable());
        if (BearingConfiguration.OperationType.RETRAIN_LOCATION.equals(bearingConfiguration.getOperationType())) {
            dataCaptureRequestEvent.setLocationRetrain(true);
        }
        dataCaptureRequestEvent.setApproach(BearingConfiguration.Approach.DATA_CAPTURE);
        bearingHandler.enqueue(requestID, dataCaptureRequestEvent, EventType.CAPTURE_DATA_EVENT);
        return Codes.RESPONSE_OK;
    }


    private int triggerDataEntryForThreshLocation(String requestID, BearingConfiguration bearingConfiguration, BearingData bearingData, BearingConfiguration.Approach approach, String siteName, BearingCallBack bearingCallBack) {

        String locationName = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingData, BearingMode.LOCATION);
        final List<BearingConfiguration.SensorType> sensorTypeList = BearingRequestParser.getSensorList(bearingConfiguration, approach);

        final List<LocationMetaData> locationMetaDatas = BearingRequestParser.getLocationMetaDataList(bearingData);
        if (locationMetaDatas == null || locationMetaDatas.isEmpty())
            return Codes.BAD_REQUEST;
        final LocationMetaData locationMetaDataForThresh = locationMetaDatas.get(0);
        if (locationMetaDataForThresh == null || locationMetaDataForThresh.getSnapshotItemList() == null) {
            return Codes.BAD_REQUEST;
        }
        final List<SnapshotItem> locationMetaData = locationMetaDataForThresh.getSnapshotItemList();
        final ThreshDataEntryEvent threshDataEntryEvent = new ThreshDataEntryEvent(requestID, EventType.THRESH_DATA_ENTRY, bearingCallBack);
        threshDataEntryEvent.setSiteName(siteName);
        threshDataEntryEvent.setSnapshotItems(locationMetaData);
        threshDataEntryEvent.setLocationName(locationName);
        threshDataEntryEvent.setSensors(sensorTypeList);
        threshDataEntryEvent.setApproach(approach);
        if(bearingConfiguration.getOperationType() == BearingConfiguration.OperationType.RETRAIN_LOCATION) {
            threshDataEntryEvent.setDataReEntry(true);
        }
        bearingHandler.enqueue(requestID, threshDataEntryEvent, EventType.THRESH_DATA_ENTRY);
        return Codes.RESPONSE_OK;
    }
}
