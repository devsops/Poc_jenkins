package com.bosch.pai.ipsadminapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bosch.pai.ipsadminapp.R;

public class SettingsPreferences {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private ProximityAdminSharedPreference preference;

    public SettingsPreferences(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        editor = sharedPreferences.edit();
        editor.apply();
        preference = ProximityAdminSharedPreference.getInstance(context);
    }

    private void clearProximityServiceURL() {
        this.editor.putString(context.getString(R.string.proximityurl), null);
        this.editor.apply();
    }

    public String getProximityServiceURL() {
        return this.sharedPreferences.getString(context.getString(R.string.proximityurl), preference.getProximityUrl());
    }

    private void clearBearingServiceURL() {
        this.editor.putString(context.getString(R.string.bearingurl), null);
        this.editor.apply();
    }

    public String getBearingServiceURL() {
        return this.sharedPreferences.getString(context.getString(R.string.bearingurl), preference.getBearingUrl());
    }

    private void clearBearingStorageMode() {
        this.editor.putString(context.getString(R.string.bearing_storage_mode), null);
        this.editor.apply();
    }

    public String getBearingStorageMode() {
        return this.sharedPreferences.getString(context.getString(R.string.bearing_storage_mode), context.getString(R.string.internal));
    }

    public void clearSharedPreferences() {
        clearBearingServiceURL();
        clearProximityServiceURL();
        clearBearingStorageMode();

        preference.clearSharedPreferences();
    }

}
