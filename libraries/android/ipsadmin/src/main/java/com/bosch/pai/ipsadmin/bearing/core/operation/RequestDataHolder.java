package com.bosch.pai.ipsadmin.bearing.core.operation;


import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The type Request data holder.
 */
public class RequestDataHolder {

    /**
     * The enum Observation data type.
     */
    public enum ObservationDataType {
        /**
         * Site record observation data type.
         */
        SITE_RECORD, /**
         * Site record append observation data type.
         */
        SITE_RECORD_APPEND, /**
         * Location data record observation data type.
         */
        LOCATION_DATA_RECORD, /**
         * Site detection observation data type.
         */
        SITE_DETECTION, /**
         * Location detection observation data type.
         */
        LOCATION_DETECTION, /**
         * Thresh detection observation data type
         */
        THRESH_DETECTION, /**
         * Stop location detection observation data type.
         */
        STOP_LOCATION_DETECTION, /**
         * Stop site detection observation data type.
         */
        STOP_SITE_DETECTION, /**
         * Stop thresh detection observation data type.
         */
        STOP_THRESH_DETECTION
    }

    private ObservationDataType observationDataType;
    private ObservationHandlerAndListener observationHandlerAndListener;
    private boolean isActiveModeOn;
    private String siteName;
    private String locationName;
    private BearingConfiguration.Approach approach;
    private int noOfFloors;
    private List<BearingConfiguration.SensorType> sensorTypeList = new ArrayList<>();
    private UUID requestId;

    /**
     * Instantiates a new Request data holder.
     *
     * @param requestId                     each request maps to a request id
     * @param observationDataType           the observation data type
     * @param observationHandlerAndListener the observation handler and listener
     */
    public RequestDataHolder(UUID requestId, ObservationDataType observationDataType, ObservationHandlerAndListener observationHandlerAndListener) {
        this.requestId = requestId;
        this.observationDataType = observationDataType;
        this.observationHandlerAndListener = observationHandlerAndListener;
    }

    /**
     * Gets observation data type.
     *
     * @return the observation data type
     */
    public ObservationDataType getObservationDataType() {
        return observationDataType;
    }

    /**
     * Gets observation handler and listener.
     *
     * @return the observation handler and listener
     */
    public ObservationHandlerAndListener getObservationHandlerAndListener() {
        return observationHandlerAndListener;
    }

    /**
     * Sets active mode on.
     *
     * @param activeModeOn the active mode on
     */
    public void setActiveModeOn(boolean activeModeOn) {
        isActiveModeOn = activeModeOn;
    }

    /**
     * Is active mode on boolean.
     *
     * @return the boolean
     */
    public boolean isActiveModeOn() {
        return isActiveModeOn;
    }

    /**
     * Sets site name.
     *
     * @param siteName the site name
     */
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    /**
     * Sets location name.
     *
     * @param locationName the location name
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     * Gets site name.
     *
     * @return the site name
     */
    public String getSiteName() {
        return siteName;
    }


    /**
     * Gets location name.
     *
     * @return the location name
     */
    public String getLocationName() {
        return locationName;
    }


    /**
     * Gets approach.
     *
     * @return the approach
     */
    /*Multiple request can be invoked with different approaches and sensortypes*/
    public BearingConfiguration.Approach getApproach() {
        return approach;
    }

    /**
     * Sets approach.
     *
     * @param approach the approach
     */
    public void setApproach(BearingConfiguration.Approach approach) {
        this.approach = approach;
    }

    /**
     * Gets no of floors.
     *
     * @return the no of floors
     */
    public int getNoOfFloors() {
        return noOfFloors;
    }

    /**
     * Gets sensor type list.
     *
     * @return the sensor type list
     */
    public List<BearingConfiguration.SensorType> getSensorTypeList() {
        return Collections.unmodifiableList(sensorTypeList);
    }

    /**
     * Sets sensor type list.
     *
     * @param sensorTypeList the sensor type list
     */
    public void setSensorTypeList(List<BearingConfiguration.SensorType> sensorTypeList) {
        this.sensorTypeList = sensorTypeList != null ? new ArrayList<>(sensorTypeList) :
                new ArrayList<BearingConfiguration.SensorType>();
    }

    /*Each api request will map to UUID for the request*/

    /**
     * Gets request id.
     *
     * @return the request id
     */
    public UUID getRequestId() {
        return requestId;
    }

    /**
     * Sets no of floors.
     *
     * @param noOfFloors the no of floors
     */
    public void setNoOfFloors(int noOfFloors) {
        this.noOfFloors = noOfFloors;
    }
}