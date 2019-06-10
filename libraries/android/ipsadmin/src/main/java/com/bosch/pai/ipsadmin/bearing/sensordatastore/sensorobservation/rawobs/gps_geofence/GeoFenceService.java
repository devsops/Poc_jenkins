/*
package com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.gps_geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.Configuration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.api.SensorObservation;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.api.SensorObservationListener;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.Constants;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.RawSnapshotConvertor;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;


public class GeoFenceService implements ResultCallback<Status> {
    private final String LOG_TAG = GeoFenceService.class.getName();
    protected GoogleApiClient mGoogleApiClient;
    protected Geofence mGeoFence;
    private PendingIntent mGeoFencePendingIntent;
    private boolean mGeoFencesAdded;
    private boolean mGoogleApiClientConnected = false;
    private Context context;
    private String siteName;
    private ResourceDataManager resourceDataManager;
    private double[] latLng;

    public GeoFenceService(Context context, ResourceDataManager resourceDataManager) {
        this.context = context;
        this.resourceDataManager = resourceDataManager;
    }

    public void setLatLng(double[] latLng) {
        this.latLng = latLng;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void init() {
        setUpGeoFence();
        if (!mGoogleApiClientConnected) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }
    }

    private void setUpGeoFence() {
        mGeoFence = new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(siteName)

                // Set the circular region of this geofence.
                .setCircularRegion(
                        latLng[0],
                        latLng[1],
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(LocationServices.API)
                .build();
    }

    private PendingIntent getGeoFencePendingIntent() {
        if (mGeoFencePendingIntent != null) {
            return mGeoFencePendingIntent;
        }
        Intent intent = new Intent(context, GeoFenceIntentService.class);
        mGeoFencePendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeoFencePendingIntent;
    }

    @NonNull
    private GeofencingRequest getGeoFencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_DWELL flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_DWELL notification when the geofence is added and if the device
        // is already inside that geoFence and remain to be their for the period of loitering time.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(mGeoFence);
        return builder.build();
    }

    public void addGeoFence() {
        if (!mGeoFencesAdded && mGoogleApiClientConnected) {
            if (!mGoogleApiClient.isConnected()) {
                final SensorObservationListener sensorObsListener = SensorObservation.getInstance().getSensorObservationCallback();
                if (sensorObsListener != null)
                    sensorObsListener.onSourceUnavailable(Configuration.SensorType.ST_GPS,
                            getErrorString(GeoFenceStatusCode.ERR_ADDING_GEO_FENCE));


                final List<SnapshotObservation> snapshotObservationForGeofence = RawSnapshotConvertor.createSnapshotObservationForGeofence(ERROR_ADDING_GEOFENCE);
                resourceDataManager.onResponseReceived(snapshotObservationForGeofence);
                return;
            }

            try {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeoFencingRequest(),
                        getGeoFencePendingIntent()
                ).setResultCallback(this);
            } catch (SecurityException securityException) {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                Log.e(LOG_TAG, "Security exception :: " + securityException);
            }
        } else if (mGeoFencesAdded) {
            Log.i(LOG_TAG, "GeoFence already added!!");
        } else {
            Log.i(LOG_TAG, "Wait to GoogleAPIClient to connect!!!");
        }
    }

    public void removeGeoFence() {
        if (!mGoogleApiClient.isConnected()) {
            return;
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    getGeoFencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e(LOG_TAG, "Security exception :: " + securityException.getMessage());
        }
        mGoogleApiClient.disconnect();
        mGeoFencesAdded = false;
        mGoogleApiClientConnected = false;
        Log.d(LOG_TAG, "Removed fence successfully");
    }

    public void onConnected(@Nullable Bundle bundle) {
        Log.i(LOG_TAG, "Connected to GoogleApiClient");
        mGoogleApiClientConnected = true;
        addGeoFence();
    }

    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "Connection suspended");
        mGoogleApiClientConnected = false;
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        mGoogleApiClientConnected = false;
    }

    @Override
    public void onResult(@NonNull Status status) {
        final SensorObservationListener sensorObsListener = SensorObservation.getInstance().getSensorObservationCallback();
        if (status.isSuccess()) {
            this.mGeoFencesAdded = true;
            Log.i(LOG_TAG, "GeoFence is added.");
            if (sensorObsListener != null)
                sensorObsListener.onSourceAdded(Configuration.SensorType.ST_GPS);
        } else {
            Log.e(LOG_TAG, "GeoFence is not added" + getErrorString(status.getStatusCode()));
            if (sensorObsListener != null)
                sensorObsListener.onSourceUnavailable(Configuration.SensorType.ST_GPS, getErrorString(status.getStatusCode()));
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
            case GeoFenceStatusCode.ERR_ADDING_GEO_FENCE:
                return "ERR_ADDING_GEO_FENCE";
            default:
                return "Unknown error: the Geofence service is not available now";
        }
    }
}
*/
