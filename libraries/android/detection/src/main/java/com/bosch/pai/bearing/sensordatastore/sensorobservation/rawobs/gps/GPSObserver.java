package com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.gps;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.Nullable;

import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.RawSnapshotConvertor;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;

import java.util.List;

/**
 * The type Gps observer.
 */
public class GPSObserver {
    private static final String LOG_TAG = "[" + GPSObserver.class.getSimpleName() + "]";
    private final Context context;
    private LocationManager locationManager;
    private final ResourceDataManager resourceDataManager;

    /**
     * Instantiates a new Gps observer.
     *
     * @param context             the context
     * @param resourceDataManager the resource data manager
     */
    public GPSObserver(Context context, ResourceDataManager resourceDataManager) {
        this.context = context;
        this.resourceDataManager = resourceDataManager;
        if (locationManager == null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }


    /**
     * Sets up gps.
     *
     * @return the up gps
     */
    public boolean setUpGPS() {
        if (locationManager == null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, LOG_TAG, "GPS not enabled");
            return false;
        }
        return true;
    }

    /**
     * Start boolean.
     *
     * @return the boolean
     */
// Synchronous method call. Once called the result is available in {@link #getLastKnownLocation()}
    public boolean start() {
        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final Location lastKnownLocation = getLocation();
            if (lastKnownLocation != null) {
                //GeoFence Radius in metres (1.5 KM)
                final List<SnapshotObservation> observationGPS =
                        RawSnapshotConvertor.createSnapshotObservationforGPS(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), getModelName());
                resourceDataManager.onResponseReceived(observationGPS);
                return true;
            } else {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "No location data found yet!!!");
                return false;
            }
        } else {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, LOG_TAG, "Location provider is not enabled. Enable GPS!!");
            return false;
            //TODO callback onErr
        }
    }

    @Nullable
    private Location getLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String s : providers) {
            Location location = locationManager.getLastKnownLocation(s);
            if (location == null) {
                continue;
            }
            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                // Find the best last known location
                bestLocation = location;
            }
        }
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.INFO, LOG_TAG, "Found location :: " + bestLocation);
        return bestLocation;
    }

    private static String getModelName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model.toUpperCase();
        } else {
            return (manufacturer.toUpperCase() + model.toUpperCase());
        }
    }

}
