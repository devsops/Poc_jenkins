package com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The type Sensor state.
 */
public class SensorState {
    private UUID uuid;

    private boolean pendingScan;
    private int activeModeRequestCount;
    private boolean isActiveModeScan;
    private List<String> activeModeList;

    /**
     * Gets active mode list.
     *
     * @return the active mode list
     */
    public List<String> getActiveModeList() {
        return Collections.unmodifiableList(activeModeList);
    }

    /**
     * Sets active mode list.
     *
     * @param activeModeList the active mode list
     */
    public void setActiveModeList(List<String> activeModeList) {
        this.activeModeList = activeModeList != null ? new ArrayList<>(activeModeList) : new ArrayList<String>();
    }


    /**
     * Is active mode scan boolean.
     *
     * @return the boolean
     */
    public boolean isActiveModeScan() {
        return isActiveModeScan;
    }

    /**
     * Sets active mode scan.
     *
     * @param activeModeScan the active mode scan
     */
    public void setActiveModeScan(boolean activeModeScan) {
        isActiveModeScan = activeModeScan;
    }

    /**
     * Gets uuid.
     *
     * @return the uuid
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Sets uuid.
     *
     * @param uuid the uuid
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    /**
     * Gets pending scan.
     *
     * @return the pending scan
     */
    public Boolean getPendingScan() {
        return pendingScan;
    }

    /**
     * Sets pending scan.
     *
     * @param pendingRequest the pending request
     */
    public void setPendingScan(Boolean pendingRequest) {
        this.pendingScan = pendingRequest;
    }

    /**
     * Gets active mode request count.
     *
     * @return the active mode request count
     */
    public Integer getActiveModeRequestCount() {
        return activeModeRequestCount;
    }

    /**
     * Sets active mode request count.
     *
     * @param activeModeRequestCount the active mode request count
     */
    public void setActiveModeRequestCount(Integer activeModeRequestCount) {
        this.activeModeRequestCount = activeModeRequestCount;
    }


}
