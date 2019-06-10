package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.gps_geofence;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.api.SensorObservation;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Geo fence intent service.
 */
public class GeoFenceIntentService extends IntentService {

    /**
     * The constant LOG_TAG.
     */
    protected static final String LOG_TAG = GeoFenceIntentService.class.getName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public GeoFenceIntentService() {
        super(LOG_TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = getErrorString(geofencingEvent.getErrorCode());
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
        String requestIds = getRequestIds(geofences);
        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
                || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            if (SensorObservation.getInstance().getSensorObservationCallback() != null) {
                final List<SnapshotObservation> snapshotObservations = new ArrayList<>();
                final SnapshotObservation snapshotObservation = new SnapshotObservation();
                snapshotObservation.setSensorType(BearingConfiguration.SensorType.ST_GPS);
                final List<SnapshotItem> tempList = new ArrayList<>();
                final SnapshotItem snapshotItem = new SnapshotItem();
                final String optionalMsg = requestIds + "|" + GeoFenceStatusCode.getStatusCodeString(geofenceTransition);
                final String[] customField = new String[1];
                customField[0] = optionalMsg;
                snapshotItem.setCustomField(customField);
                tempList.add(snapshotItem);
                snapshotObservation.setSnapShotItemList(tempList);
                snapshotObservations.add(snapshotObservation);
                SensorObservation.getInstance().getSensorObservationCallback().onObservationReceived(snapshotObservations);
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, LOG_TAG, "Register callback to get results.");
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, LOG_TAG, "Transition :: " + getTransitionString(geofenceTransition) + " Zone(s) :: " + requestIds);
            }
        } else {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Err :: " + geofenceTransition);
        }
    }

    private String getRequestIds(List<Geofence> geofences) {
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : geofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        return TextUtils.join(", ", triggeringGeofencesIdsList);
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "GEO_FENCE_ENTERED";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "GEO_FENCE_EXITED";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "GEO_FENCE_DWELL";
            default:
                return "";
        }
    }

    private String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GEOFENCE_NOT_AVAILABLE";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "GEOFENCE_TOO_MANY_GEOFENCES";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            default:
                return "Unknown error: the Geofence service is not available now";
        }
    }
}
