package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.logger.Logger;
import com.bosch.pai.ipsadmin.bearing.benchmark.bearinglogger.profiling.ResourceProfiler;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.RawSnapshotConvertor;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Wifi receiver.
 */
public class WifiReceiver extends BroadcastReceiver {
    private ResourceDataManager resourceDataManager;
    private WifiManager wifiManager;
    private Logger logger;
    private List<ScanResult> scanResult;
    private static final String TAG= WifiReceiver.class.getSimpleName();
    private int WIFI_RESULTS=0;
    private String TIME_STAMP ="TIME_STAMP";
    private int WIFI_SIGNAL_STRENGTH=0;


    /**
     * Instantiates a new Wifi receiver.
     *
     * @param resourceDataManager the resource data manager
     * @param wifiManager         the wifi manager
     * @param logger              the logger
     */
    public WifiReceiver(ResourceDataManager resourceDataManager, WifiManager wifiManager, Logger logger){
        this.resourceDataManager= resourceDataManager;
        this.wifiManager= wifiManager;
        this.logger= logger;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (!action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
            return;

        if (wifiManager.getScanResults() == null) {
            setScanResult(Collections.<ScanResult>emptyList());
        } else {
            setScanResult(wifiManager.getScanResults());
        }

        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Size of Scan: Data " + scanResult.size());


        if (resourceDataManager != null) {
            new ResourceProfiler().writeDeviceInfo(context);
            List<SnapshotObservation> snapshotObservationWIFI = RawSnapshotConvertor.createSnapshotObservationforWIFI(getScanResult());
            resourceDataManager.onResponseReceived(snapshotObservationWIFI);
            logData(snapshotObservationWIFI);

        }

        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Wifi scan complete");

    }

    private void logData(List<SnapshotObservation> snapshotObservations) {

        if (snapshotObservations.isEmpty())
            return;
        final List<SnapshotItem> snapshotItems = snapshotObservations.get(WIFI_RESULTS).getSnapShotItemList();
        final Map<String, String> accessPointToSignalLevelMap = new HashMap<>();
        if (snapshotItems.isEmpty())
            return;
        for (SnapshotItem snapshotItem : snapshotItems) {
            final String sourceId = String.valueOf(snapshotItem.getSourceId());
            final String signalLevel = String.valueOf(snapshotItem.getMeasuredValues()[WIFI_SIGNAL_STRENGTH]);
            accessPointToSignalLevelMap.put(sourceId, signalLevel);
        }
        final String[] headers = new String[accessPointToSignalLevelMap.keySet().size()];
        accessPointToSignalLevelMap.keySet().toArray(headers);
        logger.updateHeader(headers);
        final String[] existingHeaderFromFile = logger.getHeader();
        final String[] signalLevels = new String[existingHeaderFromFile.length];
        int count = 0;
        for (String ap : existingHeaderFromFile) {
            if (!TIME_STAMP.equals(ap)) {
                signalLevels[count++] = accessPointToSignalLevelMap.keySet().contains(ap) ?
                        accessPointToSignalLevelMap.get(ap) : String.valueOf(-100);
            }
        }
        logger.log(signalLevels);
    }


    /**
     * Gets scan result.
     *
     * @return the scan result
     */
    public List<ScanResult> getScanResult() {
        return Collections.unmodifiableList(scanResult);
    }

    /**
     * Sets scan result.
     *
     * @param scanResult the scan result
     */
    public void setScanResult(List<ScanResult> scanResult) {
        this.scanResult = new ArrayList<>(scanResult);
    }



}
