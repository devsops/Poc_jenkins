package com.bosch.pai.ipsadminapp.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bosch.pai.ipsadmin.retail.pmadminlib.authentication.AuthenticationManager;
import com.bosch.pai.ipsadminapp.IPSAdminApplication;
import com.bosch.pai.ipsadminapp.R;
import com.bosch.pai.ipsadminapp.fragments.DetectionFragment;
import com.bosch.pai.ipsadminapp.fragments.DwelltimeFragment;
import com.bosch.pai.ipsadminapp.fragments.EntryexitFragment;
import com.bosch.pai.ipsadminapp.fragments.HeatmapFragment;
import com.bosch.pai.ipsadminapp.fragments.SettingsFragment;
import com.bosch.pai.ipsadminapp.fragments.SiteTrainingFragment;
import com.bosch.pai.ipsadminapp.receivers.BluetoothStatusReceiver;
import com.bosch.pai.ipsadminapp.receivers.GPSConnectionReceiver;
import com.bosch.pai.ipsadminapp.receivers.InternetConnectionReceiver;
import com.bosch.pai.ipsadminapp.utilities.CrashUploader;
import com.bosch.pai.ipsadminapp.utilities.DialogUtil;
import com.bosch.pai.ipsadminapp.utilities.ProximityAdminSharedPreference;
import com.bosch.pai.ipsadminapp.utilities.RuntimePermissionUtil;
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

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        SiteTrainingFragment.OnRequestPermissionListener,
        InternetConnectionReceiver.InternetConnectionReceiverListner,
        GPSConnectionReceiver.IGPSReceiverReceiverListner, BluetoothStatusReceiver.IBluetoothStatusListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int LOCATION_REQUEST_PERMISSION = 5555;
    private static final int REQUEST_PERMISSION_SETTING = 8888;
    private static final int REQUEST_CHECK_SETTINGS = 7777;

    private static final int REQUEST_ENABLE_BT = 1456;

    private DrawerLayout drawerLayout;
    private int navItemIndex = 0;

    private String fragmentTag;
    private Fragment fragment;
    private SiteTrainingFragment siteTrainingFragment;
    private Handler handler;

    private boolean isGpsRequestPermissionPopupVisible = false;
    private boolean doubleBackToExitPressedOnce = false;

    protected static String isDataSyncNeeded = "isDataSyncNeeded";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        IPSAdminApplication.getInstance().setInternetConnectivityListener(this);
        IPSAdminApplication.getInstance().setGPSReceiverReceiverListner(this);
        IPSAdminApplication.getInstance().setBluetoothReceiverListner(this);

        final SettingsPreferences settingsPreferences = new SettingsPreferences(this);
        final ProximityAdminSharedPreference preference = ProximityAdminSharedPreference.getInstance(getApplicationContext());

        final String company = preference.getCompany();
        final String userName = preference.getUserName();
        final String userPassword = preference.getUserPassword();

        if (company == null || userName == null || userPassword == null) {
            final Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        handler = new Handler();

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.navigationview);

        final View headerView = navigationView.getHeaderView(0);
        final TextView companyTextView = headerView.findViewById(R.id.companynameheader);
        final TextView usernameTextView = headerView.findViewById(R.id.usernameheader);
        final ImageView companyImageView = headerView.findViewById(R.id.companyimageheader);

        companyTextView.setText(company);
        usernameTextView.setText(userName);
        companyImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_iero_icon));

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_training:
                    navItemIndex = 0;
                    fragmentTag = getString(R.string.nav_training);
                    if (siteTrainingFragment == null) {
                        siteTrainingFragment = new SiteTrainingFragment();
                    }
                    fragment = siteTrainingFragment;
                    break;
                case R.id.nav_detection:
                    if (!RuntimePermissionUtil.checkLocationPermission(this)) {
                        Toast.makeText(this, "Grant permission to use this option!", Toast.LENGTH_SHORT).show();
                        requestAllPermissions();
                        return false;
                    }
                    fragmentTag = getString(R.string.nav_detection);
                    navItemIndex = 1;
                    fragment = new DetectionFragment();
                    break;
                case R.id.nav_dwelltime:
                    if (checkConnection()) {
                        fragmentTag = getString(R.string.nav_dwelltime);
                        navItemIndex = 3;
                        fragment = new DwelltimeFragment();
                        break;
                    } else {
                        return true;
                    }
                case R.id.nav_heatmap:
                    if (checkConnection()) {
                        fragmentTag = getString(R.string.nav_heatmap);
                        navItemIndex = 4;
                        fragment = new HeatmapFragment();
                        break;
                    } else {
                        return true;
                    }
                case R.id.nav_entryexit:
                    if (checkConnection()) {
                        fragmentTag = getString(R.string.nav_entryexit);
                        navItemIndex = 4;
                        fragment = new EntryexitFragment();
                        break;
                    } else {
                        return true;
                    }
                case R.id.nav_settings:
                    fragmentTag = getString(R.string.nav_settings);
                    navItemIndex = 7;
                    fragment = new SettingsFragment();
                    break;
                case R.id.nav_logout:
                    confirmBeforeLogOut(settingsPreferences);
                    return true;
                case R.id.aboutUS:
                    showAboutUsDialog(MainActivity.this);
                    return true;
                default:
                    fragmentTag = getString(R.string.nav_training);
                    navItemIndex = 0;
                    if (siteTrainingFragment == null) {
                        siteTrainingFragment = new SiteTrainingFragment();
                    }
                    fragment = siteTrainingFragment;
                    break;
            }

            loadSelectedNavigationFragment(fragment, fragmentTag);

            return true;
        });

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        if (savedInstanceState == null) {
            if (siteTrainingFragment == null) {
                fragmentTag = getString(R.string.nav_training);
                siteTrainingFragment = new SiteTrainingFragment();
            }
            loadSelectedNavigationFragment(siteTrainingFragment, getString(R.string.nav_training));
        }


        checkConnection();
        checkGPSConnection();
        checkBluetoothConnection();

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Log.e("", "uncaughtException: ", e);
            System.exit(0);
        });

    }

    private void showAboutUsDialog(final Activity callingActivity) {
        //Use a Spannable to allow for links highlighting
        final SpannableString aboutText = new SpannableString("Version " + versionName(callingActivity));
        //Generate views to pass to AlertDialog.Builder and to set the text
        View about;
        try {
            //Inflate the custom view
            LayoutInflater inflater = callingActivity.getLayoutInflater();
            about = inflater.inflate(R.layout.aboutbox, (ViewGroup) callingActivity.findViewById(R.id.aboutView));
            final TextView tvAbout = (TextView) about.findViewById(R.id.aboutText);
            final TextView testOfText = (TextView) about.findViewById(R.id.show_test_app_only);
            final TextView privacyPolicy = about.findViewById(R.id.privacyText);
            final WebView licenseview = (WebView) about.findViewById(R.id.view_to_show_license);
            testOfText.setText(aboutText);
            String styledText = "<u><font color='blue'>Link for license info</font></u>.";
            String styledText1 = "<u><font color='blue'>Privacy policy</font></u>.";
            privacyPolicy.setText(Html.fromHtml(styledText1), TextView.BufferType.SPANNABLE);
            tvAbout.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
            privacyPolicy.setOnClickListener(v -> {
                privacyPolicy.setVisibility(View.GONE);
                tvAbout.setVisibility(View.GONE);
                licenseview.getSettings().setSupportZoom(false);
                testOfText.setVisibility(View.GONE);
                licenseview.loadUrl("file:///android_asset/IERO_Privacy_Document.html");
            });
            tvAbout.setOnClickListener(v -> {
                privacyPolicy.setVisibility(View.GONE);
                tvAbout.setVisibility(View.GONE);
                licenseview.getSettings().setSupportZoom(false);
                testOfText.setVisibility(View.GONE);
                licenseview.loadUrl("file:///android_asset/IPS_OSS_Android-Device.html");
            });
        } catch (InflateException e) {
            //Inflater can throw exception, unlikely but default to TextView if it occurs
            about = new TextView(callingActivity);
        }
        //Build and show the dialog
        new android.app.AlertDialog.Builder(callingActivity)
                .setTitle("About " + callingActivity.getString(R.string.app_name))
                .setCancelable(true)
                // .setIcon(R.drawable.appicon)
                .setPositiveButton("OK", null)
                .setView(about)
                .show();    //Builder method returns allow for method chaining
    }

    private static String versionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        final boolean isDataSyncNeeded = getIntent().getBooleanExtra(MainActivity.isDataSyncNeeded, false);
        if (isDataSyncNeeded) {
            CrashUploader.uploadCrashReports(MainActivity.this);
            DialogUtil.getConfirmation(MainActivity.this, "Server Sync!", "Sync with server to get latest data!\nLocal data will be cleared if available", new DialogUtil.IPermissionGranted() {
                @Override
                public void permissionGranted() {
                    downloadSiteAndLocationFromServer();
                }
            });
        } else {
            final Dialog dialog = DialogUtil.showProgreeBarPopup(this);
            authenticate(new UtilMethods.IAuthenticationCallback() {
                @Override
                public void onSuccess() {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    CrashUploader.uploadCrashReports(MainActivity.this);
                    handler.post(() -> {
                        final Toast toast = Toast.makeText(MainActivity.this, "LOGIN SUCCESS!", Toast.LENGTH_SHORT);
                        toast.show();
                    });
                }

                @Override
                public void onFailure(String errMessage) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    handler.post(() -> {
                        final Toast toast = Toast.makeText(MainActivity.this, "ERROR LOGGING IN.TRY AGAIN!\n" + errMessage, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                    });
                }
            });
        }
    }

    private void confirmBeforeLogOut(SettingsPreferences settingsPreferences) {
        DialogUtil.getConfirmation(MainActivity.this, "LogOut!", "App will be logged out.", () -> {
            final ProximityAdminSharedPreference preference =
                    ProximityAdminSharedPreference.getInstance(getApplicationContext());
            final AuthenticationManager authenticationManager =
                    new AuthenticationManager(preference.getCompany(), preference.getUserName(), null, preference.getProximityUrl());
            authenticationManager.clearUserSession();
            settingsPreferences.clearSharedPreferences();
            final Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void checkBluetoothConnection() {
        BluetoothStatusReceiver.isBluetoothConnected();
    }


    private void authenticate(UtilMethods.IAuthenticationCallback callback) {
        final SettingsPreferences settingsPreferences1 = new SettingsPreferences(this);

        String bearingServiceURL = settingsPreferences1.getBearingServiceURL();
        String proximityServiceURL = settingsPreferences1.getProximityServiceURL();

        if (bearingServiceURL.equals(proximityServiceURL)) {
            UtilMethods.authenticateProximity(this, callback);
        } else {
            UtilMethods.authenticateProximity(this, new UtilMethods.IAuthenticationCallback() {
                @Override
                public void onSuccess() {
                    UtilMethods.authenticateBearing(MainActivity.this, new UtilMethods.IAuthenticationCallback() {
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

    private void downloadSiteAndLocationFromServer() {
        if (fragmentTag.equals(getString(R.string.nav_training)) && siteTrainingFragment != null) {
            siteTrainingFragment.downloadSiteAndLocationFromServer();
        }
    }

    private void checkGPSConnection() {
        final boolean isConnected = GPSConnectionReceiver.isGPSConnected();
        onGPSonnectionChanged(isConnected);
    }

    private boolean checkConnection() {
        final boolean isConnected = InternetConnectionReceiver.isInternetConnected();
        if (!isConnected) {
            showSnack();
        }
        return isConnected;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!RuntimePermissionUtil.checkLocationPermission(this))
            requestAllPermissions();

        if (fragment != null) {
            try {
                if (fragment instanceof SiteTrainingFragment)
                    ((SiteTrainingFragment) fragment).loadSiteNamesIntoList();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error : " + e, e);
            }
        }
    }

    private void loadSelectedNavigationFragment(final Fragment fragment, final String trainingFragmentTag) {

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(trainingFragmentTag);
        }

        if (getSupportFragmentManager().findFragmentByTag(trainingFragmentTag) != null) {
            drawerLayout.closeDrawers();

            return;
        }

        final Runnable mPendingRunnable = () -> {
            final FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.nav_frame_layout, fragment, trainingFragmentTag);
            fragmentTransaction.commitAllowingStateLoss();
        };

        handler.post(mPendingRunnable);

        drawerLayout.closeDrawers();

        invalidateOptionsMenu();

    }

    public void requestAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Show Information about why you need the permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setTitle(getString(R.string.need_location_permission));
                    builder.setMessage(getString(R.string.this_app_needs_location_permission));
                    builder.setPositiveButton(getString(R.string.ok), (DialogInterface dialog, int which) -> {
                        dialog.cancel();
                        requestPermissions();
                    });
                    builder.show();

                } else if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
                    takeUserToSettingsPage();
                } else {
                    requestPermissions();
                }

                final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putBoolean(Manifest.permission.ACCESS_COARSE_LOCATION, true);
                editor.apply();


            } else {
                Log.d(LOG_TAG, "Permission Already Granted");
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(getString(R.string.device_not_supported));
            builder.setMessage(getString(R.string.app_doesnot_work));
            builder.setPositiveButton(getString(R.string.ok), (DialogInterface dialog, int which) -> dialog.cancel());
            builder.show();
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
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
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

    private void takeUserToSettingsPage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.need_location_permission));
        builder.setMessage(getString(R.string.this_app_needs_location_permission));
        builder.setPositiveButton(getString(R.string.ok), (DialogInterface dialog, int which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
            Toast.makeText(MainActivity.this, "Go to Permissions to grant location permission", Toast.LENGTH_LONG).show();
        });
        builder.show();
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_PERMISSION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PERMISSION_SETTING:
                if (RuntimePermissionUtil.checkLocationPermission(this) && siteTrainingFragment != null) {
                    siteTrainingFragment.onStorageSuccess();
                }
                break;
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "location not enabled the app might not work properly as expected. Please enable GPS.", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_ENABLE_BT:
                BluetoothStatusReceiver.isBluetoothConnected();
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_site, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!RuntimePermissionUtil.checkLocationPermission(this)) {
            Toast.makeText(this, "Grant permission to use this option!", Toast.LENGTH_SHORT).show();
            requestAllPermissions();
            return true;
        }
        if (fragmentTag != null) {
            switch (item.getItemId()) {
                case R.id.addsite:
                    if (fragmentTag.equals(getString(R.string.nav_training)) && siteTrainingFragment != null) {
                        siteTrainingFragment.createSite();
                    }
                    break;
                case R.id.download_siteand_locations:
                    if (!checkConnection()) {
                        DialogUtil.showAlertDialogOnError(this, getString(R.string.no_internet_conncetion));
                        break;
                    }
                    if (fragmentTag.equals(getString(R.string.nav_training)) && siteTrainingFragment != null) {
                        siteTrainingFragment.downloadSiteAndLocations();
                    }
                    break;
                default:
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (LOCATION_REQUEST_PERMISSION == requestCode && grantResults.length > 0) {

            boolean allgranted = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }
            if (allgranted) {
                Log.d(LOG_TAG, "onRequestPermissionsResult: all permissions granted");
            } else {
                Toast.makeText(this, "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        if (navItemIndex != 0) {
            navItemIndex = 0;
            fragmentTag = getString(R.string.nav_training);
            if (siteTrainingFragment == null) {
                siteTrainingFragment = new SiteTrainingFragment();
            }
            loadSelectedNavigationFragment(siteTrainingFragment, getString(R.string.nav_training));

            return;
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);

    }

    @Override
    public void onLocationPermissionRequest() {
        requestAllPermissions();
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected) {
            // Refresh token happens inside comms. Not needed explicit authenticate here
            /*authenticate(new UtilMethods.IAuthenticationCallback() {
                @Override
                public void onSuccess() {
                    handler.post(() -> {
                        final Toast toast = Toast.makeText(MainActivity.this, "AUTHENTICATED!", Toast.LENGTH_SHORT);
                        toast.show();
                    });
                }

                @Override
                public void onFailure(String errMessage) {
                    handler.post(() -> {
                        final Toast toast = Toast.makeText(MainActivity.this, "ERROR RE_AUTHENTICATING!\n" + errMessage, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                    });
                }
            });*/
        }

        if (!isConnected) {
            showSnack();
        }
    }

    public void showSnack() {
        final String message = "Sorry! Not connected to internet";
        final int color = Color.RED;

        final Snackbar snackbar = Snackbar
                .make(drawerLayout, message, Snackbar.LENGTH_LONG);

        final View sbView = snackbar.getView();
        final TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    public void onGPSonnectionChanged(boolean isConnected) {
        if (!isConnected) {
            displayEnableGPSLocationSettingsRequest();
        }
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
}
