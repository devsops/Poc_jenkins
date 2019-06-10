package com.bosch.pai.ipsadminapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bosch.pai.ipsadmin.retail.pmadminlib.Util;
import com.bosch.pai.ipsadminapp.IPSAdminApplication;
import com.bosch.pai.ipsadminapp.R;
import com.bosch.pai.ipsadminapp.constants.Constant;
import com.bosch.pai.ipsadminapp.receivers.BluetoothStatusReceiver;
import com.bosch.pai.ipsadminapp.receivers.GPSConnectionReceiver;
import com.bosch.pai.ipsadminapp.receivers.InternetConnectionReceiver;
import com.bosch.pai.ipsadminapp.utilities.DialogUtil;
import com.bosch.pai.ipsadminapp.utilities.ProximityAdminSharedPreference;
import com.bosch.pai.ipsadminapp.utilities.SettingsPreferences;
import com.bosch.pai.ipsadminapp.utilities.UtilMethods;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity implements View.OnKeyListener, InternetConnectionReceiver.InternetConnectionReceiverListner,
        GPSConnectionReceiver.IGPSReceiverReceiverListner, BluetoothStatusReceiver.IBluetoothStatusListener {

    private final String LOG_TAG = LoginActivity.class.getName();

    @BindView(R.id.al_company_actv)
    protected EditText companyIDEditTextView;

    @BindView(R.id.al_username_et)
    protected EditText usernameEditTextView;

    @BindView(R.id.al_password_et)
    protected EditText passwordEditTextView;

    private static final int REQUEST_ENABLE_BT = 1456;
    private static final int REQUEST_CHECK_SETTINGS = 7777;
    private boolean isGpsRequestPermissionPopupVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        IPSAdminApplication.getInstance().setInternetConnectivityListener(this);
        IPSAdminApplication.getInstance().setGPSReceiverReceiverListner(this);
        IPSAdminApplication.getInstance().setBluetoothReceiverListner(this);

        loadIeroIcon();

        companyIDEditTextView.setSingleLine(true);
        companyIDEditTextView.setMaxLines(1);
        companyIDEditTextView.setFilters(new InputFilter[]{UtilMethods.getEditTextInputFilter()});

        usernameEditTextView.setSingleLine(true);
        usernameEditTextView.setMaxLines(1);
        usernameEditTextView.setFilters(new InputFilter[]{UtilMethods.getEditTextInputFilter()});

        passwordEditTextView.setSingleLine(true);
        passwordEditTextView.setMaxLines(1);

        passwordEditTextView.setOnKeyListener(this);
        passwordEditTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        checkConnection();
        checkGPSConnection();
        checkBluetoothConnection();
    }

    private void checkGPSConnection() {
        final boolean isConnected = GPSConnectionReceiver.isGPSConnected();
        onGPSonnectionChanged(isConnected);
    }

    private void checkConnection() {
        final boolean isConnected = InternetConnectionReceiver.isInternetConnected();
        if (!isConnected) {
            showToast("Sorry! Not connected to internet");
        }
    }

    private void checkBluetoothConnection() {
        BluetoothStatusReceiver.isBluetoothConnected();
    }

    @OnClick(R.id.al_login_btn)
    public void onLoginClick() {
        validateLogin();
    }

    private void loadIeroIcon() {
        new Handler().postDelayed(() -> {
            final ProximityAdminSharedPreference preference =
                    ProximityAdminSharedPreference.getInstance(getApplicationContext());
            if (preference.getLogInStatus()) {
                triggerMainActivity(false);
            } else {
                final RelativeLayout launchingierolayout =
                        findViewById(R.id.al_joni_rl);
                final LinearLayout loginlayout =
                        findViewById(R.id.al_login_rl);
                launchingierolayout.setVisibility(View.GONE);
                loginlayout.setVisibility(View.VISIBLE);
            }
        }, 2000);
    }

    private void validateLogin() {
        final String company = companyIDEditTextView.getText().toString();
        final String username = usernameEditTextView.getText().toString();
        final String password = passwordEditTextView.getText().toString();

        if (company.isEmpty()) {
            companyIDEditTextView.setError("CompanyID should not be empty");
            return;
        }

        if (username.isEmpty()) {
            usernameEditTextView.setError("Username should not be empty");
            return;
        }

        if (password.isEmpty()) {
            passwordEditTextView.setError("Password should not be empty");
            return;
        }

        login(company, username, password);
    }

    private void login(String company, String userName, String userPassword) {
        saveCredentials(company, userName, userPassword);
        final Dialog dialog = DialogUtil.showProgreeBarPopup(LoginActivity.this);
        authenticate(new UtilMethods.IAuthenticationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(LoginActivity.this, "LOGIN SUCCESS", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                ProximityAdminSharedPreference.getInstance(getApplicationContext()).setLogInStatus(true);
                triggerMainActivity(true);
            }

            @Override
            public void onFailure(String errMessage) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, "LOGIN FAILED!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void triggerMainActivity(boolean isDataSyncNeeded) {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.isDataSyncNeeded, isDataSyncNeeded);
        startActivity(intent);
        finish();
    }

    private void saveCredentials(String company, String userName, String userPassword) {
        final ProximityAdminSharedPreference preference =
                ProximityAdminSharedPreference.getInstance(getApplicationContext());
        preference.setCompany(company);
        preference.setUserName(Util.getSHA256Conversion(userName));
        preference.setUserPassword(Util.getSHA256Conversion(userPassword));
        preference.setBearingUrl(Constant.getServerUrl());
        preference.setProximityUrl(Constant.getServerUrl());
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                keyCode == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

            validateLogin();

        }
        return false;
    }

    @Override
    public void onBluetoothConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void deviceNotSupported() {
        //Not used
    }

    @Override
    public void onGPSonnectionChanged(boolean isConnected) {
        if (!isConnected) {
            displayEnableGPSLocationSettingsRequest();
        }
    }

    private void displayEnableGPSLocationSettingsRequest() {
        if (!isGpsRequestPermissionPopupVisible) {

            isGpsRequestPermissionPopupVisible = true;

            final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).build();
            googleApiClient.connect();

            final LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(10000 / 2);

            final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(locationSettingsResult -> {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(LOG_TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(LOG_TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(LoginActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(LOG_TAG, "PendingIntent unable to execute request." + e, e);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(LOG_TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                    default:
                        break;
                }
            });
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        final String message;
        if (isConnected) {
            message = "Network is back! Try login now.";
            showToast(message);
        } else {
            message = "Sorry! Not connected to internet";
            showToast(message);
        }
    }

    private void authenticate(UtilMethods.IAuthenticationCallback callback) {
        final SettingsPreferences settingsPreferences1 = new SettingsPreferences(this);

        final String bearingServiceURL = settingsPreferences1.getBearingServiceURL();
        final String proximityServiceURL = settingsPreferences1.getProximityServiceURL();

        if (bearingServiceURL.equals(proximityServiceURL)) {
            UtilMethods.authenticateProximity(this, callback);
        } else {
            UtilMethods.authenticateProximity(this, new UtilMethods.IAuthenticationCallback() {
                @Override
                public void onSuccess() {
                    UtilMethods.authenticateBearing(LoginActivity.this, new UtilMethods.IAuthenticationCallback() {
                        @Override
                        public void onSuccess() {
                            callback.onSuccess();
                        }

                        @Override
                        public void onFailure(String errMessage) {
                            callback.onFailure(errMessage);
                        }
                    });
                }

                @Override
                public void onFailure(String errMessage) {
                    callback.onFailure(errMessage);
                }
            });
        }
    }

    public void showToast(String message) {
        final Toast toast = Toast
                .makeText(LoginActivity.this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }
}
