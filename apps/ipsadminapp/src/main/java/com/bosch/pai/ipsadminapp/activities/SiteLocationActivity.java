package com.bosch.pai.ipsadminapp.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.ipsadminapp.R;
import com.bosch.pai.ipsadminapp.adapters.BleSignalAdapter;
import com.bosch.pai.ipsadminapp.adapters.LocationAdapter;
import com.bosch.pai.ipsadminapp.constants.Configuration;
import com.bosch.pai.ipsadminapp.constants.Constant;
import com.bosch.pai.ipsadminapp.constants.KEYS;
import com.bosch.pai.ipsadminapp.constants.SITETYPE;
import com.bosch.pai.ipsadminapp.models.BleAdapterModel;
import com.bosch.pai.ipsadminapp.models.LocationAdapterModel;
import com.bosch.pai.ipsadminapp.models.SiteAdapterModel;
import com.bosch.pai.ipsadminapp.receivers.InternetConnectionReceiver;
import com.bosch.pai.ipsadminapp.utilities.DialogUtil;
import com.bosch.pai.ipsadminapp.utilities.ProximityAdminSharedPreference;
import com.bosch.pai.ipsadminapp.utilities.SettingsPreferences;
import com.bosch.pai.ipsadminapp.utilities.UtilMethods;
import com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.ConfigurationAPI;
import com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.callback.IConfigurationCallback;
import com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.impl.ConfigurationsAPIImpl;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.Training;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.callback.IBearingTrainingCallback;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.impl.BearingTrainingImpl;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.BearingSitedetails;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.ScannedBleDetails;
import com.github.lzyzsd.circleprogress.CircleProgress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SiteLocationActivity extends AppCompatActivity implements LocationAdapter.OnLocationItemClickListener {

    private static final String LOG_TAG = SiteLocationActivity.class.getSimpleName();

    private List<LocationAdapterModel> locationAdapterModelList;
    private LocationAdapter locationAdapter;
    private Training bearingTraining;
    private String siteName; // one time assignment
    private String companyName; //one time assignment
    private String storeId;// one time assignment
    private static final int ALERT_POPUP = 2;
    private Handler handler;
    private Handler.Callback callback = (Message message) -> {
        if (message.what == ALERT_POPUP) {
            final String errorMessage = (String) message.obj;
            DialogUtil.showAlertDialogOnError(SiteLocationActivity.this, errorMessage);
        }
        return false;
    };

    private SITETYPE sitetype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_location);

        final SettingsPreferences settingsPreferences = new SettingsPreferences(this);
        handler = new Handler(callback);
        final ProximityAdminSharedPreference preference = ProximityAdminSharedPreference.getInstance(this);

        companyName = preference.getCompany();
        locationAdapterModelList = new ArrayList<>();

        bearingTraining = BearingTrainingImpl.getInstance(this, settingsPreferences.getBearingServiceURL());

        final Toolbar toolbar = findViewById(R.id.ast_tb);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        toolbar.setNavigationOnClickListener((View view) -> onBackPressed());

        final TextView siteLocationHint = findViewById(R.id.site_location_hint);
        siteLocationHint.setVisibility(View.VISIBLE);
        final RecyclerView locationsrv = findViewById(R.id.asl_locations_rv);
        locationsrv.setVisibility(View.GONE);
        locationsrv.setHasFixedSize(true);
        locationsrv.setNestedScrollingEnabled(false);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        locationsrv.setLayoutManager(layoutManager);

        locationAdapter = new LocationAdapter(locationAdapterModelList, this);
        locationsrv.setAdapter(locationAdapter);


        final SiteAdapterModel siteAdapterModel =
                (SiteAdapterModel) getIntent().getSerializableExtra(KEYS.SITE_ADAPTER_MODEL);

        siteName = siteAdapterModel.getSiteName();
        loadLocationsFortheSite(siteName);

        sitetype = getSiteType();

        storeId = UtilMethods.getStoreIdFromSiteName(siteName);

    }

    private SITETYPE getSiteType() {
        int counter = 0;
        final List<SITETYPE> sitetypes = new ArrayList<>();
        List<BearingConfiguration.SensorType> sensorTypes = bearingTraining.getSensorTypes(siteName);
        if (sensorTypes != null && !sensorTypes.isEmpty()) {
            for (BearingConfiguration.SensorType sensorType : sensorTypes) {
                if (sensorType.equals(BearingConfiguration.SensorType.ST_WIFI)) {
                    sitetypes.add(SITETYPE.WIFI);
                    counter++;
                } else if (sensorType.equals(BearingConfiguration.SensorType.ST_BLE)) {
                    sitetypes.add(SITETYPE.BLE);
                    counter++;
                }
            }
        }
        switch (counter) {
            case 1:
                switch (sitetypes.get(0)) {
                    case WIFI:
                        return SITETYPE.WIFI;
                    case BLE:
                        return SITETYPE.BLE;
                    default:
                        return null;
                }
            case 2:
                return SITETYPE.BOTH;
            default:
                return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocationsFortheSite(siteName);
    }

    private void loadLocationsFortheSite(String siteName) {
        final BearingSitedetails bearingSitedetails =
                bearingTraining.getAllLocationNamesForSiteFromLocal(siteName);

        runOnUiThread(() -> {

            locationAdapterModelList.clear();

            Set<String> bleLocationNames = bearingSitedetails.getBleLocationNames();
            Set<String> wifiLocationNames = bearingSitedetails.getWifiLocationNames();

            if (bleLocationNames != null && !bleLocationNames.isEmpty()) {
                for (String locationName : bleLocationNames) {
                    locationAdapterModelList.add(new LocationAdapterModel(LocationAdapterModel.SENSOR.BLE, locationName));
                }
            }

            if (wifiLocationNames != null && !wifiLocationNames.isEmpty()) {
                for (String locationName : wifiLocationNames) {
                    locationAdapterModelList.add(new LocationAdapterModel(LocationAdapterModel.SENSOR.WIFI, locationName));
                }
            }

            sortLocationAdapterModelList();
        });
    }

    private void sortLocationAdapterModelList() {
        Collections.sort(locationAdapterModelList, (LocationAdapterModel o1, LocationAdapterModel o2) ->
                o1.getLocationName().compareTo(o2.getLocationName())
        );

        final TextView siteLocationHint = findViewById(R.id.site_location_hint);
        final RecyclerView locationsrv = findViewById(R.id.asl_locations_rv);

        if (locationAdapterModelList.isEmpty()) {
            siteLocationHint.setVisibility(View.VISIBLE);
            locationsrv.setVisibility(View.GONE);
        } else {
            siteLocationHint.setVisibility(View.GONE);
            locationsrv.setVisibility(View.VISIBLE);
        }

        locationAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_location, menu);
        menu.findItem(R.id.generate_classifier).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addlocation:
                DialogUtil.getModeOfTrainingDialog(this, "Mode of Training", isWifi -> {

                    if (sitetype != null) {
                        switch (sitetype) {
                            case BLE:
                                if (isWifi) {
                                    DialogUtil.showAlertDialogOnError(SiteLocationActivity.this, "Merge Site for Wifi mode");
                                    return;
                                }
                                break;
                            case WIFI:
                                if (!isWifi) {
                                    DialogUtil.showAlertDialogOnError(SiteLocationActivity.this, "Merge Site for BLE mode");
                                    return;
                                }
                                break;
                            case BOTH:
                                break;
                        }
                    }

                    if (isWifi) {
                        trainLocation();
                    } else {
                        trainBleLocation(null);
                    }
                });
                break;
            case R.id.generate_classifier:
                if (!checkConnection()) {
                    DialogUtil.showAlertDialogOnError(this, getString(R.string.no_internet_conncetion));
                    break;
                }
                validateForGenerateClassifier();
                break;
            case R.id.upload:
                if (!checkConnection()) {
                    DialogUtil.showAlertDialogOnError(this, getString(R.string.no_internet_conncetion));
                    break;
                }
                validateSiteAndLocationUpload();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void retrainLocationUsingBLE(String oldLocationName, String bleId, double threshold, ISuccess iSuccess) {
        bearingTraining.retrainBleLocation(siteName, oldLocationName, threshold, bleId, new IBearingTrainingCallback.ITrainBleLocation() {
            @Override
            public void onSuccess() {
                iSuccess.onSuccess();
            }

            @Override
            public void onFailure(String errorMessage) {
                iSuccess.onFailure(errorMessage);
            }
        });
    }

    private void trainBleLocation(String locationname, String bleId, double threshold, ISuccess iSuccess) {
        bearingTraining.trainBleLocation(siteName, locationname, threshold, bleId, new IBearingTrainingCallback.ITrainBleLocation() {
            @Override
            public void onSuccess() {
                iSuccess.onSuccess();
            }

            @Override
            public void onFailure(String errorMessage) {
                iSuccess.onFailure(errorMessage);
            }
        });
    }

    private void validateSiteAndLocationUpload() {

        if (locationAdapterModelList != null && !locationAdapterModelList.isEmpty()) {

            final List<String> wifiLocations = new ArrayList<>();
            for (LocationAdapterModel locationAdapterModel : locationAdapterModelList) {
                if (locationAdapterModel.getSensorType().equals(LocationAdapterModel.SENSOR.WIFI)) {
                    wifiLocations.add(locationAdapterModel.getLocationName());
                }
            }

            if (!wifiLocations.isEmpty() && wifiLocations.size() < Constant.getMinimumLocationsCount()) {
                handler.sendMessage(handler.obtainMessage(ALERT_POPUP, getString(R.string.minimum_three_locations_shouldbe_trained_with_wifi_sensor)));
                return;
            }

            if (checkConnection()) {
                DialogUtil.getConfirmation(this, getString(R.string.are_you_sure), getString(R.string.upload_site_and_locations), this::uploadTrainedSiteAndLocations);
            } else {
                Toast.makeText(this, "Please enable Internet", Toast.LENGTH_SHORT).show();
                showSnack();
            }
        } else {
            Toast.makeText(this, "Please train some locations", Toast.LENGTH_SHORT).show();
        }

    }

    private void validateForGenerateClassifier() {
        if (locationAdapterModelList != null && locationAdapterModelList.size() < Constant.getMinimumLocationsCount()) {
            handler.sendMessage(handler.obtainMessage(ALERT_POPUP, getString(R.string.minimum_three_locations_shouldbe_trained)));
            return;
        }
        if (checkConnection()) {
            DialogUtil.getConfirmation(this, getString(R.string.are_you_sure), getString(R.string.upload_site_and_locations_before_generating_classifer), () -> generateClassifier(null));
        } else {
            Toast.makeText(this, "Please enable Internet", Toast.LENGTH_SHORT).show();
            showSnack();
        }
    }

    private void generateClassifier(Dialog dialog) {
        if (dialog == null) {
            dialog = new Dialog(SiteLocationActivity.this);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            dialog.setContentView(R.layout.custom_progress_bar);

            final TextView progressTitle = dialog.findViewById(R.id.progress_title);
            progressTitle.setText("");

            dialog.show();
        }

        Dialog finalDialog = dialog;
        bearingTraining.generateClaasifier(siteName, new IBearingTrainingCallback.IBearingOnUpload() {
            @Override
            public void onSuccess() {
                if (finalDialog.isShowing()) {
                    finalDialog.dismiss();
                }
                showMessageInDialog("Uploaded Successfully. \nPlease wait for a moment before starting detection.");
            }

            @Override
            public void onFailure(String errorMessage) {
                handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
                if (finalDialog.isShowing()) {
                    finalDialog.dismiss();
                }
            }
        });
    }

    private void uploadTrainedSiteAndLocations() {
        this.uploadLocationMetaDataListForAnalytics();
        final Dialog dialog = new Dialog(SiteLocationActivity.this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.custom_progress_bar);

        final TextView progressTitle = dialog.findViewById(R.id.progress_title);
        progressTitle.setText("");

        bearingTraining.uploadSiteLocations(siteName, new IBearingTrainingCallback.IBearingOnUpload() {
            @Override
            public void onSuccess() {
                final List<String> wifiLocations = new ArrayList<>();
                for (LocationAdapterModel locationAdapterModel : locationAdapterModelList) {
                    if (locationAdapterModel.getSensorType().equals(LocationAdapterModel.SENSOR.WIFI)) {
                        wifiLocations.add(locationAdapterModel.getLocationName());
                    }
                }

                if (wifiLocations.size() >= Constant.getMinimumLocationsCount()) {
                    new Handler().postDelayed(() -> generateClassifier(dialog), 2000);
                } else {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    showMessageInDialog(null);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
            }
        });

        dialog.show();
    }

    private void uploadLocationMetaDataListForAnalytics() {
        final ConfigurationAPI api = ConfigurationsAPIImpl.getInstance();
        final SettingsPreferences settingsPreferences = new SettingsPreferences(this);
        final Set<String> locationNames = new HashSet<>();
        for (LocationAdapterModel locationAdapterModel : locationAdapterModelList) {
            locationNames.add(locationAdapterModel.getLocationName());
        }
        api.saveStoreLocations(companyName, storeId, siteName,
                locationNames, settingsPreferences.getProximityServiceURL(),
                new IConfigurationCallback.ISaveStoreLocationsCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(LOG_TAG, "Analytics meta data has been uploaded successfully!");
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
                    }
                });
    }

    private void showMessageInDialog(String message) {

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        if (message == null) {
            builder.setMessage(getString(R.string.upload_site_success));
        } else {
            builder.setMessage(message);
        }
        builder.setPositiveButton("OK",
                (DialogInterface dialog, int which) -> dialog.cancel());
        builder.show();

    }

    public boolean checkConnection() {
        final boolean isConnected = InternetConnectionReceiver.isInternetConnected();
        if (!isConnected) {
            showSnack();
        }
        return isConnected;
    }

    private void showSnack() {
        String message = "Sorry! Not connected to internet";
        int color = Color.RED;

        final RelativeLayout siteLocationsLayout = findViewById(R.id.asl_rl);

        final Snackbar snackbar = Snackbar
                .make(siteLocationsLayout, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }


    private void trainLocation() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.create_location_layout);

        final EditText locationName = dialog.findViewById(R.id.locationname);
        locationName.setSingleLine();
        locationName.setMaxLines(1);
        locationName.setFilters(new InputFilter[]{UtilMethods.getEditTextInputFilter()});

        final Button trainLocation = dialog.findViewById(R.id.trainlocation);
        trainLocation.setOnClickListener((View v) -> {
            final String locationname = locationName.getText().toString();
            if (isLocationNameAlreadyPresent(locationname)) {
                Toast.makeText(this, "Location already exists! Try retraining...", Toast.LENGTH_SHORT).show();
                return;
            }
            if (locationname.isEmpty()) {
                Toast.makeText(SiteLocationActivity.this, "Enter Location Name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            startLocationTrainingPopup(locationname);
        });

        dialog.show();
    }

    private void startLocationTrainingPopup(final String locationName) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.start_location_training_layout);

        final Button startLocaTraining = dialog.findViewById(R.id.start_loca_training);
        startLocaTraining.setOnClickListener((View view) -> {
            startLocationTraining(locationName);
            dialog.dismiss();
        });


        dialog.show();
    }

    private void startLocationTraining(final String locationName) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.location_training_progress);

        final TextView trainingstatus = dialog.findViewById(R.id.trainingstatus);

        final CircleProgress circleProgress = dialog.findViewById(R.id.circle_progress);
        circleProgress.setMax(100);

        bearingTraining.trainLocation(siteName, locationName, new IBearingTrainingCallback.IBearingOnLocationTrainAndRetrain() {
            @Override
            public void onSuccess(final Integer progress) {
                if (progress != null) {
                    circleProgress.setProgress(progress);
                    if (progress == 100) {
                        trainingstatus.setText(getString(R.string.training_completed));
                        new Handler().postDelayed(() -> {
                            if (dialog.isShowing()) {
                                loadLocationsFortheSite(siteName);
                                dialog.dismiss();

                            }
                        }, 3000);
                    }
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
            }
        });

        dialog.show();
    }

    private void trainBleLocation(String locationNameForRetrain) {

        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.custom_progress_bar);

        final TextView progressTitle = dialog.findViewById(R.id.progress_title);
        progressTitle.setVisibility(View.GONE);

        dialog.show();

        bearingTraining.mergeBLESite(siteName, new IBearingTrainingCallback.IBearingBleSiteSignalMergeListener() {
            @Override
            public void onSuccess(List<SnapshotObservation> observations, List<ScannedBleDetails> bleSourceId) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                trainBleLocation(locationNameForRetrain, observations, bleSourceId);
            }

            @Override
            public void onFailure(String errorMessage) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(SiteLocationActivity.this, "No bles nearby", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void trainBleLocation(String locationNameForRetrain, List<SnapshotObservation> observations, List<ScannedBleDetails> bleSourceIds) {
        final Set<ScannedBleDetails> bleidslist = new HashSet<>();
        if (bleSourceIds != null && !bleSourceIds.isEmpty()) {
            bleidslist.addAll(bleSourceIds);
        }
        if (bleidslist.isEmpty()) {
            Toast.makeText(this, "No Bles Found", Toast.LENGTH_SHORT).show();
            return;
        }


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.create_ble_location_layout);


        final EditText locationName = dialog.findViewById(R.id.locationname_ble);
        locationName.setSingleLine();
        locationName.setMaxLines(1);
        locationName.setFilters(new InputFilter[]{UtilMethods.getEditTextInputFilter()});


        final EditText thresholdBle = dialog.findViewById(R.id.threshold_ble);
        thresholdBle.setVisibility(View.GONE);
        thresholdBle.setSingleLine();
        locationName.setMaxLines(1);
        thresholdBle.setText("-50");

        thresholdBle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    thresholdBle.setText("-50");
                    thresholdBle.setSelection(thresholdBle.getText().toString().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final List<BleAdapterModel> bleAdapterModelList = new ArrayList<>();

        for (ScannedBleDetails bleId : bleidslist) {
            bleAdapterModelList.add(new BleAdapterModel(bleId.getBleId(), bleId.getBleRssi(), false));
        }

        final List<BleAdapterModel> bleAdapterModels = new ArrayList<>(bleAdapterModelList);

        if (locationNameForRetrain != null) {
            locationName.setText(locationNameForRetrain);
            locationName.setEnabled(false);


//            String mappedBle = null;
//            Double mappedThreshold = -1.0;

            String[] mappedBleWithThresh = getMappedBleWithThresh(locationNameForRetrain);
            if (mappedBleWithThresh != null) {

                final TextView alreadymappedble = dialog.findViewById(R.id.alreadymappedble);
                alreadymappedble.setVisibility(View.VISIBLE);
                alreadymappedble.setText("Trained BLE " + mappedBleWithThresh[0] + " with Threshold " + mappedBleWithThresh[1]);
            }

            /*Map<String, Map<Double, List<String>>> bleAndthreshMappingForLocation = bearingTraining.getBleAndthreshMappingForLocation(siteName);
            if (!bleAndthreshMappingForLocation.isEmpty()) {

                for (Map.Entry<String, Map<Double, List<String>>> stringMapEntry : bleAndthreshMappingForLocation.entrySet()) {

                    final String keyBle = stringMapEntry.getKey();
                    final Map<Double, List<String>> thresholdLocationList = stringMapEntry.getValue();

                    for (Map.Entry<Double, List<String>> listEntry : thresholdLocationList.entrySet()) {

                        final Double keyThreshold = listEntry.getKey();
                        List<String> locationList = listEntry.getValue();


                        final String locationNameForBle = locationList.get(0);

                        if (locationNameForRetrain.equals(locationNameForBle)) {

                            mappedBle = keyBle;
                            mappedThreshold = keyThreshold;
                            break;
                        }

                    }

                }
            }*/

            /*if (mappedBle != null && Math.abs(mappedThreshold) > 1.0D) {
                final TextView alreadymappedble = dialog.findViewById(R.id.alreadymappedble);
                alreadymappedble.setVisibility(View.VISIBLE);
                alreadymappedble.setText("Trained BLE " + mappedBle + " with Threshold " + mappedThreshold);
            }
*/

//            final String bleThreshValueFromSharedPrefernce = UtilMethods.getBLEThreshValueFromSharedPrefernce(this, locationNameForRetrain);
            final String bleThreshValueFromSharedPrefernce = getBleMappedForLocation(locationNameForRetrain);

            if (bleThreshValueFromSharedPrefernce != null && !bleThreshValueFromSharedPrefernce.isEmpty()) {
//                String[] split = bleThreshValueFromSharedPrefernce.split("####");
//                final String ble = split[0];
//                final String thr = split[1];

//                if (!ble.isEmpty()) {
                for (BleAdapterModel bleAdapterModel : bleAdapterModels) {
                    if (bleAdapterModel.getSourceId().equals(bleThreshValueFromSharedPrefernce)) {
                        bleAdapterModel.setSelected(true);
                        break;
                    }
                }
//                }

//                thresholdBle.setText(thr);
            }
        }


        final ListView bleListView = dialog.findViewById(R.id.bleidslist);
        final BleSignalAdapter adapter = new BleSignalAdapter(this, bleAdapterModels, null, true);
        bleListView.setAdapter(adapter);

        final CheckBox filterBeacon = dialog.findViewById(R.id.filter_beacon);
        filterBeacon.setOnCheckedChangeListener((compoundButton, b) -> {

            bleAdapterModels.clear();
            adapter.notifyDataSetChanged();

            if (filterBeacon.isChecked()) {

                for (BleAdapterModel bleAdapterModel : bleAdapterModelList) {
                    if (bleAdapterModel.getSourceId().contains(Configuration.ESTIMOTE_ID)) {
                        bleAdapterModels.add(bleAdapterModel);
                    }
                }

            } else {
                bleAdapterModels.addAll(bleAdapterModelList);
            }

            adapter.notifyDataSetChanged();
        });

        final Button trainLocation = dialog.findViewById(R.id.trainlocationble);
        trainLocation.setOnClickListener((View v) -> {
            final String locationname = locationName.getText().toString().trim();
            if (locationNameForRetrain == null && isLocationNameAlreadyPresent(locationname)) {
                Toast.makeText(this, "Location already exists! Try retraining...", Toast.LENGTH_SHORT).show();
                return;
            }
            if (locationname.isEmpty()) {
                Toast.makeText(SiteLocationActivity.this, "Enter Location Name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!UtilMethods.isValidinputString(locationname)) {
                Toast.makeText(this, "locationname should not contain spaces and special characters", Toast.LENGTH_SHORT).show();
                return;
            }


            final List<BleAdapterModel> signalAdapterModelList = new ArrayList<>();
            for (BleAdapterModel signalAdapterModel : bleAdapterModels) {
                if (signalAdapterModel.isSelected()) {
                    signalAdapterModelList.add(signalAdapterModel);
                }
            }

            if (signalAdapterModelList.isEmpty()) {
                Toast.makeText(this, "Select one BleId", Toast.LENGTH_SHORT).show();
                return;
            }


            if (signalAdapterModelList.size() > 1) {
                Toast.makeText(this, "Select only one BleId per location.", Toast.LENGTH_SHORT).show();
                return;
            }


            /*double threshold = -50;
            try {
                threshold = Double.parseDouble(thresholdBle.getText().toString());
                if (threshold < -100 || threshold > 0) {
                    Toast.makeText(this, "Threshold value should be in between -100 to 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter Valid threshold Value", Toast.LENGTH_SHORT).show();
                return;
            }*/

            BleAdapterModel bleAdapterModel = signalAdapterModelList.get(0);
            final String bleId = bleAdapterModel.getSourceId();
            double threshold = bleAdapterModel.getRssi();
            try {
                if (threshold < -100 || threshold > 0) {
                    Toast.makeText(this, "Threshold value should be in between -100 to 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Threshold value should be in between -100 to 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (locationNameForRetrain == null) {
                trainBle(dialog, locationname, threshold, bleId, observations);
            } else {
                retrainBle(dialog, locationname, threshold, bleId, observations);
            }


        });

        dialog.show();
    }

    private boolean isLocationNameAlreadyPresent(String locationName) {
        if(locationAdapterModelList == null || locationAdapterModelList.isEmpty())
            return false;
        for(LocationAdapterModel lAM : locationAdapterModelList) {
            if(locationName.equals(lAM.getLocationName())) {
                return true;
            }
        }
        return false;
    }


    private String[] getMappedBleWithThresh(String locationNameForRetrain) {
        Map<String, Map<Double, List<String>>> bleAndthreshMappingForLocation = bearingTraining.getBleAndthreshMappingForLocation(siteName);
        if (!bleAndthreshMappingForLocation.isEmpty()) {

            for (Map.Entry<String, Map<Double, List<String>>> stringMapEntry : bleAndthreshMappingForLocation.entrySet()) {

                final String keyBle = stringMapEntry.getKey();
                final Map<Double, List<String>> thresholdLocationList = stringMapEntry.getValue();

                for (Map.Entry<Double, List<String>> listEntry : thresholdLocationList.entrySet()) {

                    final Double keyThreshold = listEntry.getKey();
                    List<String> locationList = listEntry.getValue();

                    if (!locationList.isEmpty()) {
                        final String locationNameForBle = locationList.get(0);

                        if (locationNameForRetrain.equals(locationNameForBle)) {

                            if (keyBle != null && Math.abs(keyThreshold) > 1.0D) {
                                String[] list = new String[2];

                                list[0] = keyBle;
                                list[1] = String.valueOf(keyThreshold);

                                return list;
                            }
                        }
                    }

                }

            }
        }
        return null;
    }

    private void trainBle(Dialog dialog, String locationname, double threshold, String bleId, List<SnapshotObservation> observations) {
        final String oldLocationName = checkIfBeaconAlreadyMappedWhileCreating(bleId);

        if (oldLocationName != null && oldLocationName.equals(locationname)) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            retrainLocationUsingBLE(locationname, bleId, threshold, new ISuccess() {
                @Override
                public void onSuccess() {
                    loadLocationsFortheSite(siteName);

                   /* final ProximityAdminSharedPreference proximityAdminSharedPreference =
                            ProximityAdminSharedPreference.getInstance(SiteLocationActivity.this);
                    proximityAdminSharedPreference.setBlereTrained(true);*/
                }

                @Override
                public void onFailure(String errorMessage) {
                    handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
                }
            });
            return;
        }

        if (oldLocationName == null) {

            HashSet<String> sourceIds = new HashSet<>();
            sourceIds.add(bleId);

            if (bearingTraining.siteBleUpdateOnMerge(siteName, observations, sourceIds)) {

                trainBleLocation(locationname, bleId, threshold, new ISuccess() {
                    @Override
                    public void onSuccess() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        loadLocationsFortheSite(siteName);

                   /*     final ProximityAdminSharedPreference proximityAdminSharedPreference =
                                ProximityAdminSharedPreference.getInstance(SiteLocationActivity.this);
                        proximityAdminSharedPreference.setBlereTrained(true);*/
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
                    }
                });
            } else {
                Toast.makeText(SiteLocationActivity.this, "Location train Failed", Toast.LENGTH_SHORT).show();
            }

        } else {
            // show dialog
            DialogUtil.getConfirmation(this,
                    "Alert",
                    "Beacon already mapped to location " + oldLocationName + ". \n Want to disable this and create new locations ?",
                    () -> {

                        HashSet<String> sourceIds = new HashSet<>();
                        sourceIds.add(bleId);

                        if (bearingTraining.siteBleUpdateOnMerge(siteName, observations, sourceIds)) {

                            trainRetrainUsingBLE(oldLocationName, bleId, locationname, threshold, dialog);
                        } else {

                            Toast.makeText(SiteLocationActivity.this, "Location train Failed", Toast.LENGTH_SHORT).show();

                        }

                    });
        }
    }

    private void trainRetrainUsingBLE(String oldLocationName, String bleId, String locationname, double finalThreshold, Dialog dialog) {
        retrainLocationUsingBLE(oldLocationName, bleId, -1.0, new ISuccess() {
            @Override
            public void onSuccess() {

                trainBleLocation(locationname, bleId, finalThreshold, new ISuccess() {
                    @Override
                    public void onSuccess() {

                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        loadLocationsFortheSite(siteName);

                       /* final ProximityAdminSharedPreference proximityAdminSharedPreference =
                                ProximityAdminSharedPreference.getInstance(SiteLocationActivity.this);
                        proximityAdminSharedPreference.setBlereTrained(true);*/
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
            }
        });
    }

    private void retrainBle(Dialog dialog, String locationname, double threshold, String bleId, List<SnapshotObservation> observations) {
        final String oldLocationName = checkIfBeaconAlreadyMappedWhileCreating(bleId);

        if (oldLocationName != null && oldLocationName.equals(locationname)) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            retrainLocationUsingBLE(locationname, bleId, threshold, new ISuccess() {
                @Override
                public void onSuccess() {
                    loadLocationsFortheSite(siteName);

                  /*  final ProximityAdminSharedPreference proximityAdminSharedPreference =
                            ProximityAdminSharedPreference.getInstance(SiteLocationActivity.this);
                    proximityAdminSharedPreference.setBlereTrained(true);*/
                }

                @Override
                public void onFailure(String errorMessage) {
                    handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
                }
            });
            return;
        }

        if (oldLocationName == null) {

            Set<String> objects = new HashSet<>();
            objects.add(bleId);

            if (bearingTraining.siteBleUpdateOnMerge(siteName, observations, objects)) {
                retrainLocationUsingBLE(locationname, bleId, threshold, new ISuccess() {
                    @Override
                    public void onSuccess() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        loadLocationsFortheSite(siteName);

                        /*final ProximityAdminSharedPreference proximityAdminSharedPreference =
                                ProximityAdminSharedPreference.getInstance(SiteLocationActivity.this);
                        proximityAdminSharedPreference.setBlereTrained(true);*/
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
                    }
                });
            } else {
                Toast.makeText(SiteLocationActivity.this, "Location retrain Failed", Toast.LENGTH_SHORT).show();
            }

        } else {
            DialogUtil.getConfirmation(this,
                    "Alert",
                    "Beacon already mapped to location " + oldLocationName + ". \n Want to disable this and create new location ?.",
                    () -> {

                        Set<String> objects = new HashSet<>();
                        objects.add(bleId);

                        if (bearingTraining.siteBleUpdateOnMerge(siteName, observations, objects)) {
                            retrainRetrainUsingBLE(oldLocationName, bleId, locationname, threshold, dialog);
                        } else {
                            Toast.makeText(SiteLocationActivity.this, "Location retrain Failed", Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    private void retrainRetrainUsingBLE(String oldLocationName, String bleId, String locationname, double finalThreshold, Dialog dialog) {
        retrainLocationUsingBLE(oldLocationName, bleId, -1.0, new ISuccess() {
            @Override
            public void onSuccess() {

                retrainLocationUsingBLE(locationname, bleId, finalThreshold, new ISuccess() {
                    @Override
                    public void onSuccess() {

                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        loadLocationsFortheSite(siteName);

                       /* final ProximityAdminSharedPreference proximityAdminSharedPreference =
                                ProximityAdminSharedPreference.getInstance(SiteLocationActivity.this);
                        proximityAdminSharedPreference.setBlereTrained(true);*/
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
            }
        });
    }

    private String getBleMappedForLocation(String locationName) {

        final Map<String, String> bleMappingForLocation = bearingTraining.getBleMappingForLocation(siteName);

        for (Map.Entry<String, String> entry : bleMappingForLocation.entrySet()) {
            final String ble = entry.getKey();
            final String location = entry.getValue();

            if (location.equals(locationName)) {
                return ble;
            }

        }

        return null;
    }


    private String checkIfBeaconAlreadyMappedWhileCreating(String bleId) {

        final Map<String, String> bleMappingForLocation = bearingTraining.getBleMappingForLocation(siteName);

        String alreadyFoundBleLocation = null;

        if (bleMappingForLocation.containsKey(bleId)) {
            for (Map.Entry<String, String> entry : bleMappingForLocation.entrySet()) {
                final String ble = entry.getKey();

                if (ble.equals(bleId)) {
                    alreadyFoundBleLocation = entry.getValue();
                    break;
                }

            }
        }

        return alreadyFoundBleLocation;

       /* final String actualLocation = UtilMethods.getLocationValueFromSharedPrefernce(this, bleId);
        if (actualLocation.isEmpty()) {
            return null;
        }

        return actualLocation;*/
    }

    @Override
    public void onLocationRenameItemClick(int position) {
        // NOT USED
        //final LocationAdapterModel locationAdapterModel = locationAdapterModelList.get(position);
        //DialogUtil.getConfirmation(this, getString(R.string.are_you_sure), getString(R.string.this_item_name_is_permanently_going_to_change), () -> renameLocation(locationAdapterModel));
    }

    @Override
    public void onLocationUploadItemClicked(int position) {
        // NOT USED
        //final LocationAdapterModel locationAdapterModel = locationAdapterModelList.get(position);
        //DialogUtil.getConfirmation(this, getString(R.string.are_you_sure), getString(R.string.do_you_want_to_upload_this_location), () -> uploadLocation(locationAdapterModel.getLocationName()));
    }

    @Override
    public void onLocationRetrainItemClick(final int position) {
        final LocationAdapterModel locationAdapterModel = locationAdapterModelList.get(position);
        final String locationName = locationAdapterModel.getLocationName();

        DialogUtil.getModeOfTrainingDialog(this, "Mode of Training", new DialogUtil.IGetModeofTraining() {
            @Override
            public void isWifiMode(boolean isWifi) {

                if (sitetype != null) {
                    switch (sitetype) {
                        case BLE:
                            if (isWifi) {
                                Toast.makeText(SiteLocationActivity.this, "Merege Site for Wifi mode", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            break;
                        case WIFI:
                            if (!isWifi) {
                                Toast.makeText(SiteLocationActivity.this, "Merege Site for BLE mode", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            break;
                        case BOTH:
                            break;
                    }
                }

                if (isWifi) {
                    startLocationRetrain(locationName);
                } else {
                    trainBleLocation(locationName);
                }
            }
        });

    }

    public void startLocationRetrain(String locationName) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.start_location_training_layout);

        final Button startLocaTraining = dialog.findViewById(R.id.start_loca_training);
        startLocaTraining.setOnClickListener((View view) -> {
            locationRetrainStatusMaintainence(locationName);
            dialog.dismiss();
        });

        dialog.show();

    }

    private void locationRetrainStatusMaintainence(String locationName) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.location_training_progress);

        final TextView trainingstatus = dialog.findViewById(R.id.trainingstatus);

        final CircleProgress circleProgress = dialog.findViewById(R.id.circle_progress);
        circleProgress.setMax(100);

        bearingTraining.retrainLocation(siteName, locationName,
                new IBearingTrainingCallback.IBearingOnLocationTrainAndRetrain() {
                    @Override
                    public void onSuccess(final Integer progress) {
                        if (progress != null) {
                            circleProgress.setProgress(progress);
                            if (progress == 100) {
                                trainingstatus.setText(getString(R.string.training_completed));
                                new Handler().postDelayed(() -> {
                                    if (dialog.isShowing()) {
                                        loadLocationsFortheSite(siteName);
                                        dialog.dismiss();
                                    }
                                }, 3000);
                            }
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
                    }
                });

        dialog.show();
    }

    @Override
    public void onLocationDeleteItemClick(int position) {
        // NOT USED
        //final LocationAdapterModel locationAdapterModel = locationAdapterModelList.get(position);
        //DialogUtil.getConfirmation(this, getString(R.string.are_you_sure), getString(R.string.this_item_is_nolonger_available), () -> deleteLocation(locationAdapterModel));
    }

    @Override
    public void onLocationItemClick(int position) {
        // NOT USED
    }

    public interface ISuccess {
        void onSuccess();

        void onFailure(String errorMessage);
    }
}