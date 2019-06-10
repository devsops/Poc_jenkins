package com.bosch.pai.ipsadmin.bearing.core.operation.processor;

import android.util.Log;

import com.bosch.pai.ipsadmin.bearing.core.BearingHandler;
import com.bosch.pai.ipsadmin.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.SensorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShutDownDetectionProcessor extends DetectionRequestProcessor {

    private final String TAG = ShutDownDetectionProcessor.class.getName();

    public ShutDownDetectionProcessor(String requestID, Event event, Sender sender) {
        super(requestID, event, sender);
        Thread.currentThread().setName(ShutDownDetectionProcessor.class.getName());
    }


    @Override
    public void run() {
        Log.d(TAG, "run: stopping all active detection");
        final Map<String, RequestDataHolder> uuidToRequestDataHolderMap = BearingHandler.getUuidToRequestDataHolderMap();
        final List<RequestDataHolder> requestDataHolderCollection = new ArrayList<>(uuidToRequestDataHolderMap.values());
        for (RequestDataHolder requestDataHolder : requestDataHolderCollection) {
            requestDataHolder.getObservationHandlerAndListener().
                    removeAndUnregisterSensorObservation(requestDataHolder.getApproach());
        }
        Log.d(TAG, "shutdownSensorDataCapture: stopping detection");
        SensorUtil.setShutdown(true);
    }
}
