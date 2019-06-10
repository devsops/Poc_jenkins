package com.bosch.pai.ipsadminapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;

import com.bosch.pai.ipsadminapp.IPSAdminApplication;

public class GPSConnectionReceiver extends BroadcastReceiver {

    private static IGPSReceiverReceiverListner igpsReceiverReceiverListner;

    public static void setIgpsReceiverReceiverListner(IGPSReceiverReceiverListner igpsReceiverReceiverListner) {
        GPSConnectionReceiver.igpsReceiverReceiverListner = igpsReceiverReceiverListner;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        new Handler().postDelayed(() -> {
            final String action = intent.getAction();
            if (action != null && action.matches("android.location.PROVIDERS_CHANGED")) {
                final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (igpsReceiverReceiverListner != null) {
                    if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        igpsReceiverReceiverListner.onGPSonnectionChanged(true);
                    } else {
                        igpsReceiverReceiverListner.onGPSonnectionChanged(false);
                    }
                }
            }
        }, 5000);
    }


    public static boolean isGPSConnected() {
        final LocationManager locationManager = (LocationManager) IPSAdminApplication.getInstance().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public interface IGPSReceiverReceiverListner {
        void onGPSonnectionChanged(boolean isConnected);
    }

}
