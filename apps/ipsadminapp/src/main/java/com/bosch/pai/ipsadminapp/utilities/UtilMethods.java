package com.bosch.pai.ipsadminapp.utilities;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.Analytics;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.callback.IAnalyticsCallbacks;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.impl.AnalyticsImpl;
import com.bosch.pai.ipsadmin.retail.pmadminlib.authentication.AuthenticationCallback;
import com.bosch.pai.ipsadmin.retail.pmadminlib.authentication.AuthenticationManager;

public class UtilMethods {

    private static final String LOG_TAG = UtilMethods.class.getSimpleName();
    private static final String VALIDATION_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@!_#$&])[A-Za-z\\d@!_#$&]{8,}$";

    private UtilMethods() {
        //default constructor
    }

    public interface IAuthenticationCallback {

        void onSuccess();

        void onFailure(String errMessage);

    }

    public static void authenticateProximity(final Activity context, @Nullable final IAuthenticationCallback listener) {
        final ProximityAdminSharedPreference preference =
                ProximityAdminSharedPreference.getInstance(context);

        final SettingsPreferences settingsPreferences = new SettingsPreferences(context);
        String proximityServiceURL = settingsPreferences.getProximityServiceURL();
        if (proximityServiceURL != null && !proximityServiceURL.isEmpty()) {
            final Analytics analytics = AnalyticsImpl.getInstance(proximityServiceURL);
            analytics.onAuthentication(context, preference.getCompany(), preference.getUserName(),
                    preference.getUserPassword(), proximityServiceURL,
                    new IAnalyticsCallbacks.IAuthenticationListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(LOG_TAG, "Authentication Success");
                            checkUserRole(context, preference.getProximityUrl(), preference.getCompany(), preference.getUserName(), listener);
                        }

                        @Override
                        public void failure(String errorMessage) {
                            DialogUtil.showAlertDialogOnError(context, "LOGIN FAILURE!\n" + errorMessage);
                            if (listener != null) {
                                listener.onFailure(errorMessage);
                            }
                        }
                    });
        }
    }

    private static void checkUserRole(final Activity context, final String url, final String company, final String userName, final IAuthenticationCallback listener) {
        final AuthenticationManager authenticationManager = new AuthenticationManager(company, userName, null, url);
        authenticationManager.checkUserRole(context, new AuthenticationCallback() {
            @Override
            public void onAuthenticationSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onAuthenticationFail(String message) {
                DialogUtil.showAlertDialogOnError(context, "LOGIN FAILURE!\n" + message);
                if (listener != null) {
                    listener.onFailure(message);
                }
            }
        });
    }

    public static void changePassword(Context context, String oldPassword, String newPassword, AuthenticationCallback callback) {
        final ProximityAdminSharedPreference preference =
                ProximityAdminSharedPreference.getInstance(context);
        if(!preference.getUserPassword().equals(oldPassword)) {
            callback.onAuthenticationFail("Old Password is incorrect!");
            return;
        }
        final AuthenticationManager authenticationManager = new AuthenticationManager(preference.getCompany(),
                preference.getUserName(), null, preference.getProximityUrl());
        authenticationManager.changePassword(context, oldPassword, newPassword, callback);
    }

    public static boolean isAValidPassword(String passwordForValidation) {
        return passwordForValidation.matches(VALIDATION_REGEX);
    }

    public static void authenticateBearing(final Activity context, IAuthenticationCallback callback) {
        final ProximityAdminSharedPreference preference =
                ProximityAdminSharedPreference.getInstance(context);

        final SettingsPreferences settingsPreferences = new SettingsPreferences(context);
        String bearingServiceURL = settingsPreferences.getBearingServiceURL();
        if (bearingServiceURL != null && !bearingServiceURL.isEmpty()) {
            final Analytics analytics = AnalyticsImpl.getInstance(bearingServiceURL);
            analytics.onAuthentication(context, preference.getCompany(), preference.getUserName(),
                    preference.getUserPassword(), bearingServiceURL,
                    new IAnalyticsCallbacks.IAuthenticationListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(LOG_TAG, "Bearing Authentication Success");
                            callback.onSuccess();
                        }

                        @Override
                        public void failure(String errorMessage) {
                            callback.onFailure(errorMessage);
                        }
                    });
        }
    }

    public static String getStoreIdFromSiteName(String siteName) {
        try {
            final String[] split = siteName.split("_");
            if (split.length >= 2) {
                return split[0];
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e, e);
            return null;
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager)
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        final View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null && inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    public static boolean isValidinputString(String validinput) {
        if (validinput.contains(" "))
            return false;
        final String regexStr = "^[a-zA-Z0-9]*$";
        return validinput.matches(regexStr);
    }


    public static InputFilter getEditTextInputFilter() {
        //Allow anly Alphanumeric and underscore
        return (source, start, end, dest, dstart, dend) -> {
            for (int i = start;i < end;i++) {
                if (!Character.isLetterOrDigit(source.charAt(i)) &&
                        !Character.toString(source.charAt(i)).equals("_"))
                {
                    return "";
                }
            }
            return null;
        };
    }
}
