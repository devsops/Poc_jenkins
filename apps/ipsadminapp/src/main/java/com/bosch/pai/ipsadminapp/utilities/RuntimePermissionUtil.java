package com.bosch.pai.ipsadminapp.utilities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.bosch.pai.ipsadminapp.R;

/**
 * Created by sjn8kor on 1/17/2018.
 */

public class RuntimePermissionUtil {

    private static final String LOG_TAG = RuntimePermissionUtil.class.getSimpleName();

    private RuntimePermissionUtil() {

    }

    public static boolean checkLocationPermission(Context context) {
        final int accessFineLocation = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION);

        final int accessCoarseLocation = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION);

        return accessFineLocation == PackageManager.PERMISSION_GRANTED &&
                accessCoarseLocation == PackageManager.PERMISSION_GRANTED ;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(LOG_TAG, "settings not found. ", e);
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;


    }

    public static void setLocationEnable(final Context context) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Please turn on location ");
        alertDialog.setPositiveButton(
                context.getResources().getString(R.string.ok),
                (DialogInterface dialog, int which) -> {
                    Intent intent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);

                });

        alertDialog.show();
    }


}
