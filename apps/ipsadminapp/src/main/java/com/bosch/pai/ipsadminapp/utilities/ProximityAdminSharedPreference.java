package com.bosch.pai.ipsadminapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bosch.pai.ipsadminapp.R;

/**
 * Created by sjn8kor on 1/12/2018.
 */

public class ProximityAdminSharedPreference {

    private static ProximityAdminSharedPreference proximityAdminSharedPreference;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private ProximityAdminSharedPreference(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        editor.apply();
    }

    public static synchronized ProximityAdminSharedPreference getInstance(Context context) {
        if (proximityAdminSharedPreference == null) {
            proximityAdminSharedPreference = new ProximityAdminSharedPreference(context);
        }
        return proximityAdminSharedPreference;
    }

    public void setLogInStatus(boolean status) {
        editor.putBoolean(context.getString(R.string.logIn_success), status);
        editor.apply();
    }

    public boolean getLogInStatus() {
        return preferences.getBoolean(context.getString(R.string.logIn_success), false);
    }

    public String getCompany() {
        return preferences.getString(context.getString(R.string.company), "");
    }

    public void setCompany(String company) {
        editor.putString(context.getString(R.string.company), company);
        editor.apply();
    }


    public String getUserName() {
        return preferences.getString(context.getString(R.string.username), "");
    }

    public void setUserName(String company) {
        editor.putString(context.getString(R.string.username), company);
        editor.apply();
    }


    public String getUserPassword() {
        return preferences.getString(context.getString(R.string.userpassword), "");
    }

    public void setUserPassword(String company) {
        editor.putString(context.getString(R.string.userpassword), company);
        editor.apply();
    }


    public void setProximityUrl(String proximityUrl) {
        editor.putString(context.getString(R.string.proximity_url), proximityUrl);
        editor.apply();
    }

    public String getProximityUrl() {
        return this.preferences.getString(context.getString(R.string.proximity_url), "");
    }

    public void setBearingUrl(String bearingUrl) {
        editor.putString(context.getString(R.string.bearing_server_url), bearingUrl);
        editor.apply();
    }

    public String getBearingUrl() {
        return this.preferences.getString(context.getString(R.string.bearing_server_url), "");
    }

    public void clearSharedPreferences() {
        setCompany("");
        setUserName("");
        setUserPassword("");
        setLogInStatus(false);
    }
}
