package com.bosch.pai.ipsadminapp.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bosch.pai.ipsadminapp.R;
import com.bosch.pai.ipsadminapp.adapters.DetectionAdapter;
import com.bosch.pai.ipsadminapp.utilities.DialogUtil;
import com.bosch.pai.ipsadminapp.utilities.SettingsPreferences;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.DetectionFromLocal;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.DetectionMode;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.callback.IBearingDetectionCallback;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.impl.BearingDetectionFromLocal;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.models.LocationDetectionResponse;
import com.bosch.pai.ipsadmin.retail.pmadminlib.detection.models.SiteDetectionResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import butterknife.ButterKnife;

public class DetectionFragment extends Fragment {

    private static final String LOG_TAG = DetectionFragment.class.getSimpleName();

    private DetectionFromLocal bearingDetection;
    private LinkedList<String> detectionItemList;
    private DetectionAdapter detectionAdapter;

    private DetectionMode detectionMode = DetectionMode.WIFI;
    private boolean isStarted = false;
    private static final int ALERT_POPUP = 1;
    private Handler handler;
    private Handler.Callback callback = (Message message) -> {
        if (message.what == ALERT_POPUP) {
            final String errorMessage = (String) message.obj;
            DialogUtil.showAlertDialogOnError(Objects.requireNonNull(getActivity()), errorMessage);
        }
        return false;
    };

    private Button siteDetection;
    private Button locationDetection;


    public DetectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem item = menu.findItem(R.id.addsite);
        item.setVisible(false);
        final MenuItem item1 = menu.findItem(R.id.download_siteand_locations);
        item1.setVisible(false);
        final MenuItem item2 = menu.findItem(R.id.detectionmode);
        item2.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detectionmode:
                selectTheDetectionMode();
                break;
            default:
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectTheDetectionMode() {
        final Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.dialog_detection_mode);

        final RadioGroup radioGroup = dialog.findViewById(R.id.trainingmode_radiogrp);

        final RadioButton wifiRadioBtn = dialog.findViewById(R.id.wifi);
        final RadioButton bleRadioBtn = dialog.findViewById(R.id.ble);
        final RadioButton bothRadioBtn = dialog.findViewById(R.id.both);

        switch (detectionMode) {
            case WIFI:
                wifiRadioBtn.setChecked(true);
                break;
            case BLE:
                bleRadioBtn.setChecked(true);
                break;
            case WIFI_BLE:
                bothRadioBtn.setChecked(true);
                break;
            default:
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                return;
        }

        final Button close = dialog.findViewById(R.id.trainingmode_cancel);
        close.setOnClickListener(v -> {
            final int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getActivity(), "Select one detection mode", Toast.LENGTH_SHORT).show();
                return;
            }
            final RadioButton radioButton = dialog.findViewById(selectedId);

            final CharSequence mode = radioButton.getText();
            if (mode != null && !String.valueOf(mode).isEmpty()) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            } else {
                Toast.makeText(getActivity(), "Select one detection mode", Toast.LENGTH_SHORT).show();
            }
        });

        final Button trainingmodeSure = dialog.findViewById(R.id.trainingmode_sure);
        trainingmodeSure.setOnClickListener(v -> {

            final int selectedId = radioGroup.getCheckedRadioButtonId();
            final RadioButton radioButton = dialog.findViewById(selectedId);

            final String mode = radioButton.getText().toString();

            switch (mode) {
                case "WIFI":
                    detectionMode = DetectionMode.WIFI;
                    break;
                case "BLE":
                    detectionMode = DetectionMode.BLE;
                    break;
                case "BOTH":
                    detectionMode = DetectionMode.WIFI_BLE;
                    break;
                default:
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    return;
            }

            bearingDetection.setDetectionMode(detectionMode);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_detection, container, false);
        ButterKnife.bind(Objects.requireNonNull(getActivity()), view);

        detectionItemList = new LinkedList<>();
        handler = new Handler(callback);

        loadBearingDetection();

        final RecyclerView recyclerView = view.findViewById(R.id.detectionrv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        detectionAdapter = new DetectionAdapter(detectionItemList);
        recyclerView.setAdapter(detectionAdapter);


        siteDetection = view.findViewById(R.id.sitedetection);
        locationDetection = view.findViewById(R.id.locationdetection);

        siteDetection.setOnClickListener((View v) -> {

            if (!locationDetection.getText().toString().equals(getString(R.string.start_location_detection))) {
                Toast.makeText(getActivity(), getString(R.string.stop_location_detection), Toast.LENGTH_SHORT).show();
                return;
            }

            if (siteDetection.getText().toString().equals(getString(R.string.start_site_detection))) {
                siteDetection.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.red_circle_indicator));
                detectionItemList.clear();
                detectionAdapter.notifyDataSetChanged();

                siteDetection.setText(getString(R.string.stop_site_detection));
                locationDetection.setEnabled(false);

                startSiteDetection();
            } else {
                siteDetection.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.green_circle_indicator));
                locationDetection.setEnabled(true);
                siteDetection.setText(getString(R.string.start_site_detection));

                stopSiteDetection();
            }
        });

        locationDetection.setOnClickListener((View v) -> {

            if (!siteDetection.getText().toString().equals(getString(R.string.start_site_detection))) {
                Toast.makeText(getActivity(), getString(R.string.stop_site_detection), Toast.LENGTH_SHORT).show();
                return;
            }

            if (locationDetection.getText().toString().equals(getString(R.string.start_location_detection))) {
                locationDetection.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.red_circle_indicator));
                detectionItemList.clear();
                detectionAdapter.notifyDataSetChanged();

                locationDetection.setText(getString(R.string.stop_location_detection));
                startLocationDetection();
                siteDetection.setEnabled(false);
            } else {
                locationDetection.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.green_circle_indicator));
                locationDetection.setText(getString(R.string.start_location_detection));
                stopLocationDetection();
                siteDetection.setEnabled(true);
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.d(LOG_TAG, " onDetach called.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(LOG_TAG, " Destroy called.");

        if (!siteDetection.getText().toString().equals(getString(R.string.start_site_detection))) {
            stopSiteDetection();
        }

        if (!locationDetection.getText().toString().equals(getString(R.string.start_location_detection))) {
            stopLocationDetection();
        }

    }

    private void stopLocationDetection() {

        this.isStarted = false;

        bearingDetection.stopDetection(new IBearingDetectionCallback.IBearingStopLocationDetectionListener() {
            @Override
            public void onStopLocationDetectionSuccess() {
                Log.d(LOG_TAG, "location detection stopped");
            }

            @Override
            public void onStopLocationDetectionFailure(String errorMessage) {
                handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
            }
        });


    }

    private void startLocationDetection() {
        this.isStarted = true;
        bearingDetection.startLocationDetection(new IBearingDetectionCallback.IBearingStartLocationDetectionListener() {
            @Override
            public void onStartLocationDetectionSuccess(final LocationDetectionResponse locationDetectionResponse) {

                if (locationDetectionResponse != null) {

                    final Map<String, Double> locationUpdateMap = locationDetectionResponse.getLocationUpdateMap();

                    String text = "Site Name : " + locationDetectionResponse.getSiteName() + "\nlocations : \n";
                    for (Map.Entry<String, Double> entry : locationUpdateMap.entrySet()) {
                        text += entry.getKey() + " : " + entry.getValue() + "\n";
                    }
                    String timestamp = locationDetectionResponse.getTimestamp();
                    if (timestamp == null) {
                        Calendar cal = Calendar.getInstance();
                        TimeZone tz = cal.getTimeZone();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
                        sdf.setTimeZone(tz);
                        String localTime = sdf.format(new Date());

                        text += "Timestamp : " + localTime;
                    } else {
                        text += "Timestamp : " + timestamp;
                    }

                    detectionItemList.addFirst(text);
                    detectionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onStartLocationDetectionFailure(String errorMessage) {
                handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
            }
        });

    }

    private void stopSiteDetection() {
        this.isStarted = false;
        bearingDetection.stopSiteDetection(new IBearingDetectionCallback.IBearingStopSiteDetectionListener() {
            @Override
            public void onStopSiteDetectionSuccess() {
                Log.d(LOG_TAG, "site detection stopped");
            }

            @Override
            public void onStopSiteDetectionFailure(String errorMessage) {
                handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
            }
        });
    }

    private void startSiteDetection() {
        this.isStarted = true;
        bearingDetection.startSiteDetection(new IBearingDetectionCallback.IBearingStartSiteDetectionListener() {
            @Override
            public void onStartSiteDetectionSuccess(final SiteDetectionResponse siteDetectionResponse) {
                if (siteDetectionResponse != null && isStarted) {

                    final String text = siteDetectionResponse.getSiteName() + " : " + siteDetectionResponse.getTimestamp();

                    Log.d(LOG_TAG, " site detected : " + text);

                    detectionItemList.addFirst(text);

                    detectionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onStartSiteDetectionFailure(String errorMessage) {
                handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
            }
        });
    }


    private void loadBearingDetection() {
        if (bearingDetection == null) {
            final SettingsPreferences settingsPreferences = new SettingsPreferences(getActivity());

            bearingDetection = BearingDetectionFromLocal.getInstance(getActivity(), detectionMode);
            bearingDetection.setServerEndPoint(settingsPreferences.getBearingServiceURL(), null, (boolean value) -> {
                if (!value) {
                    Toast.makeText(getActivity(), "Ip not set", Toast.LENGTH_SHORT).show();
                }
            });

            boolean trueForExternalFalseForInternal = settingsPreferences.getBearingStorageMode().equals(getString(R.string.external));
            bearingDetection.storeBearingData(trueForExternalFalseForInternal, getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.isStarted = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.isStarted) {
            bearingDetection.shutdownDetection();
            this.isStarted = false;
        }
    }


}
