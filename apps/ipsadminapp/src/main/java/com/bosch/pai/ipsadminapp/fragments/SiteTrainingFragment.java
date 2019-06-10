package com.bosch.pai.ipsadminapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.Training;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.callback.IBearingTrainingCallback;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.impl.BearingTrainingImpl;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.BearingSitedetails;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.ScannedBleDetails;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.SnapshotItemWithSensorType;
import com.bosch.pai.ipsadminapp.R;
import com.bosch.pai.ipsadminapp.activities.MainActivity;
import com.bosch.pai.ipsadminapp.activities.SiteLocationActivity;
import com.bosch.pai.ipsadminapp.adapters.BleSignalAdapter;
import com.bosch.pai.ipsadminapp.adapters.SignalAdapter;
import com.bosch.pai.ipsadminapp.adapters.StoreAdapter;
import com.bosch.pai.ipsadminapp.constants.Configuration;
import com.bosch.pai.ipsadminapp.constants.KEYS;
import com.bosch.pai.ipsadminapp.models.BleAdapterModel;
import com.bosch.pai.ipsadminapp.models.SignalAdapterModel;
import com.bosch.pai.ipsadminapp.models.SiteAdapterModel;
import com.bosch.pai.ipsadminapp.receivers.InternetConnectionReceiver;
import com.bosch.pai.ipsadminapp.utilities.DialogUtil;
import com.bosch.pai.ipsadminapp.utilities.ProximityAdminSharedPreference;
import com.bosch.pai.ipsadminapp.utilities.RuntimePermissionUtil;
import com.bosch.pai.ipsadminapp.utilities.SettingsPreferences;
import com.bosch.pai.ipsadminapp.utilities.UtilMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class SiteTrainingFragment extends Fragment
        implements StoreAdapter.OnSiteItemClickListener {

    private static final String LOG_TAG = SiteTrainingFragment.class.getSimpleName();
    private Training bearingTraining;
    private TextView fragmentTrainingHint;
    private RecyclerView siteRv;
    private List<SiteAdapterModel> siteAdapterModels;
    private StoreAdapter storeAdapter;
    private SettingsPreferences settingsPreferences;
    private String companyName;

    private static final int ALERT_POPUP = 3;
    private Handler handler;
    private Handler.Callback callback = (Message message) -> {
        if (message.what == ALERT_POPUP) {
            final String errorMessage = (String) message.obj;
            DialogUtil.showAlertDialogOnError(Objects.requireNonNull(getActivity()), errorMessage);
        }
        return false;

    };

    private OnRequestPermissionListener listener;

    public SiteTrainingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final FragmentActivity activity = getActivity();
        try {
            listener = (OnRequestPermissionListener) activity;
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "Exception in casting activity to onclicklistener. ", e);
            if (activity != null)
                throw new ClassCastException(activity.toString() + " must implement MyInterface. ");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_training,
                container, false);

        handler = new Handler(callback);
        final ProximityAdminSharedPreference preference = ProximityAdminSharedPreference.getInstance(getActivity());
        companyName = preference.getCompany();
        settingsPreferences = new SettingsPreferences(getActivity());
        siteAdapterModels = new ArrayList<>();

        fragmentTrainingHint = view.findViewById(R.id.fragment_training_hint);
        fragmentTrainingHint.setVisibility(View.VISIBLE);

        siteRv = view.findViewById(R.id.siterv);
        siteRv.setVisibility(View.GONE);
        siteRv.setHasFixedSize(true);
        siteRv.setNestedScrollingEnabled(false);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        siteRv.setLayoutManager(layoutManager);

        storeAdapter = new StoreAdapter(siteAdapterModels, this);
        siteRv.setAdapter(storeAdapter);

        loadSiteNamesIntoList();

        return view;
    }


    public void loadSiteNamesIntoList() {
        initializeBearingTrainingAndSetServerEndpoint();

        Set<String> siteNamesSet;
        siteAdapterModels.clear();


        siteNamesSet = bearingTraining.getAllSiteNamesFromLocal();

        for (String siteName : siteNamesSet) {
            final BearingSitedetails bearingSitedetails = bearingTraining.getAllLocationNamesForSiteFromLocal(siteName);
            Set<String> bleLocationNames = bearingSitedetails.getBleLocationNames();
            Set<String> wifiLocationNames = bearingSitedetails.getWifiLocationNames();

            int locationCount = 0;

            if (bleLocationNames != null && !bleLocationNames.isEmpty()) {
                locationCount += bleLocationNames.size();
            }

            if (wifiLocationNames != null && !wifiLocationNames.isEmpty()) {
                locationCount += wifiLocationNames.size();
            }

            siteAdapterModels.add(new SiteAdapterModel(siteName, locationCount));
        }

        sortSiteAdapterModelList();
    }

    @Override
    public void onResume() {
        super.onResume();

        loadSiteNamesIntoList();
    }

    private void initializeBearingTrainingAndSetServerEndpoint() {
        if (bearingTraining == null) {
            settingsPreferences = new SettingsPreferences(getActivity());
            final String bearingCompleteUrl = settingsPreferences.getBearingServiceURL();

            bearingTraining = BearingTrainingImpl.getInstance(getActivity(), bearingCompleteUrl);
            bearingTraining.setBearingServerEndPoint(bearingCompleteUrl, null, new IBearingTrainingCallback.IBearingSetServerEndpointListener() {
                @Override
                public void onSuccess() {
                    Log.d(LOG_TAG, "Success");
                }

                @Override
                public void onFailure(String errormessage) {
                    Log.d(LOG_TAG, "Failure");
                }
            });

            final boolean trueForExternalFalseForInternal = settingsPreferences.getBearingStorageMode().equals(getString(R.string.external));
            bearingTraining.storeBearingData(trueForExternalFalseForInternal, getActivity());
        }
    }

    private void sortSiteAdapterModelList() {
        Collections.sort(siteAdapterModels, (SiteAdapterModel o1, SiteAdapterModel o2) ->
                o1.getSiteName().compareTo(o2.getSiteName())
        );

        if (siteAdapterModels.isEmpty()) {
            fragmentTrainingHint.setVisibility(View.VISIBLE);
            siteRv.setVisibility(View.GONE);
        } else {
            fragmentTrainingHint.setVisibility(View.GONE);
            siteRv.setVisibility(View.VISIBLE);
        }

        storeAdapter.notifyDataSetChanged();
    }

    public void onStorageSuccess() {
        loadSiteNamesIntoList();
    }

    public void createSite() {
        if (RuntimePermissionUtil.checkLocationPermission(getActivity())) {
           /* if (siteAdapterModels != null && siteAdapterModels.size() >= 1) {
                DialogUtil.showAlertDialogOnError(getActivity(), "Only one site can be created");
                return;
            }*/
            createBearingSite();
        } else {
            listener.onLocationPermissionRequest();
        }
    }

    private void createBearingSite() {
        final Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.create_site_layout);

        final EditText storename = dialog.findViewById(R.id.storename);
        storename.setSingleLine();
        storename.setMaxLines(1);
        storename.setFilters(new InputFilter[]{UtilMethods.getEditTextInputFilter()});


        final EditText numberOfFloors = dialog.findViewById(R.id.number_of_floors);
        numberOfFloors.setSingleLine();
        numberOfFloors.setMaxLines(1);

        final RadioGroup radioGroup = dialog.findViewById(R.id.mode_of_training);

        final Button createsite = dialog.findViewById(R.id.createsite);
        createsite.setOnClickListener((View v) -> {
            final ProximityAdminSharedPreference preference =
                    ProximityAdminSharedPreference.getInstance(getActivity());

            String siteName = storename.getText().toString().trim();
            if (siteName.isEmpty()) {
                Toast.makeText(getActivity(), "Enter StoreName", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!UtilMethods.isValidinputString(siteName)) {
                Toast.makeText(getActivity(), "Sitename should not contain spaces and special characters", Toast.LENGTH_SHORT).show();
                return;
            }

            int numberOfFloor;
            try {
                final String numOfFloor = numberOfFloors.getText().toString();
                if (numOfFloor.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter Number of floor values", Toast.LENGTH_SHORT).show();
                    return;
                }
                numberOfFloor = Integer.parseInt(numOfFloor);
                if (numberOfFloor < 0) {
                    Toast.makeText(getActivity(), "Enter valid number of floor value", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error : " + e, e);
                numberOfFloor = Configuration.NUMBER_OF_FLOORS;
            }

            siteName = preference.getCompany() + "_" + siteName;

            final int selectedId = radioGroup.getCheckedRadioButtonId();
            final RadioButton radioButton = dialog.findViewById(selectedId);

            boolean isWifiSensor;
            final String mode = radioButton.getText().toString();
            isWifiSensor = mode.equals(getString(R.string.wifi));


            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            trainSite(siteName, numberOfFloor, isWifiSensor, -100);
        });
        dialog.show();
    }

    private void trainSite(String siteName, int numberOfFloor, boolean isWifiSensor, int rssivalue) {
        final Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.custom_progress_bar);

        final TextView progressTitle = dialog.findViewById(R.id.progress_title);
        progressTitle.setVisibility(View.GONE);

        dialog.show();

        bearingTraining.trainSite(siteName, numberOfFloor, rssivalue, isWifiSensor, new IBearingTrainingCallback.ITrainsite() {

            @Override
            public void onFailure(final String errorMessage) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(getActivity(), "trainSite failure", Toast.LENGTH_SHORT).show();
                handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
            }

            @Override
            public void onWifiSignalCapture(List<SnapshotObservation> observations, List<SnapshotItemWithSensorType> snapshotItemWithSensorTypeList) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                filterAccessPoints(siteName, observations, snapshotItemWithSensorTypeList, false);
            }

            @Override
            public void onBleSignalCapture(List<SnapshotObservation> observations, List<ScannedBleDetails> bleSourceIds) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                filterSourceIds(siteName, observations, bleSourceIds, false);
            }


        });
    }

    private void filterSourceIds(String siteName, List<SnapshotObservation> observations, List<ScannedBleDetails> bleSourceIds, boolean isSiteUpdateOnMerge) {
        final List<BleAdapterModel> allSignalAdapterModels = new ArrayList<>();
        for (ScannedBleDetails signal : bleSourceIds) {
            allSignalAdapterModels.add(new BleAdapterModel(signal.getBleId(), signal.getBleRssi(), true));
        }

        final Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.signal_merge_layout);

        final List<BleAdapterModel> signalAdapterModels = new ArrayList<>(allSignalAdapterModels);

        final CheckBox selectAll = dialog.findViewById(R.id.select_all);

        final ListView signals = dialog.findViewById(R.id.signals);
        final BleSignalAdapter adapter = new BleSignalAdapter(getActivity(), signalAdapterModels, selectAll::setChecked, false);
        signals.setAdapter(adapter);


        final CheckBox filterBeacon = dialog.findViewById(R.id.filter_beacon);
        filterBeacon.setOnCheckedChangeListener((compoundButton, b) -> {

            signalAdapterModels.clear();

            if (filterBeacon.isChecked()) {

                for (BleAdapterModel bleAdapterModel : allSignalAdapterModels) {
                    if (bleAdapterModel.getSourceId().contains(Configuration.ESTIMOTE_ID)) {
                        signalAdapterModels.add(bleAdapterModel);
                    }
                }

            } else {
                signalAdapterModels.addAll(allSignalAdapterModels);
            }

            adapter.notifyDataSetChanged();
        });

        selectAll.setOnClickListener(v -> {
            final boolean checked = selectAll.isChecked();

            for (BleAdapterModel signalAdapterModel : signalAdapterModels) {
                signalAdapterModel.setSelected(checked);
            }
            adapter.notifyDataSetChanged();
        });

        final Button signalMerge = dialog.findViewById(R.id.signal_merge);
        signalMerge.setOnClickListener((view) -> {
            final List<String> signalAdapterModelList = new ArrayList<>();
            for (BleAdapterModel signalAdapterModel : signalAdapterModels) {
                if (signalAdapterModel.isSelected()) {
                    signalAdapterModelList.add(signalAdapterModel.getSourceId());
                }
            }

            if (isSiteUpdateOnMerge) {
                if (bearingTraining.siteBleUpdateOnMerge(siteName, observations, new HashSet<>(signalAdapterModelList))) {
                    Toast.makeText(getActivity(), "Merge Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Merge Failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (bearingTraining.bleUpdateSnapshot(siteName, observations, new HashSet<>(signalAdapterModelList))) {
                    Toast.makeText(getActivity(), "Merge Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Merge Failed", Toast.LENGTH_SHORT).show();
                }
            }
            dialog.dismiss();

            loadSiteNamesIntoList();
        });

        dialog.show();
    }

    private void filterAccessPoints(final String siteName,
                                    final List<SnapshotObservation> snapshotObservations,
                                    List<SnapshotItemWithSensorType> snapshotItemWithSensorTypeList,
                                    final boolean isSightUpdateOnMerge) {
        final List<SignalAdapterModel> signalAdapterModels = new ArrayList<>();
        for (SnapshotItemWithSensorType snapshotItemWithSensorType : snapshotItemWithSensorTypeList) {
            signalAdapterModels.add(new SignalAdapterModel(snapshotItemWithSensorType, true));
        }

        final Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.signal_merge_layout);

        final LinearLayout linearLayout = dialog.findViewById(R.id.filter_beacon_layout);
        linearLayout.setVisibility(View.GONE);

        final CheckBox filterBeacon = dialog.findViewById(R.id.filter_beacon);
        filterBeacon.setVisibility(View.GONE);

        final CheckBox selectAll = dialog.findViewById(R.id.select_all);

        final ListView signals = dialog.findViewById(R.id.signals);
        final SignalAdapter adapter = new SignalAdapter(getActivity(), signalAdapterModels, selectAll::setChecked);
        signals.setAdapter(adapter);

        selectAll.setOnClickListener(v -> {
            final boolean checked = selectAll.isChecked();

            for (SignalAdapterModel signalAdapterModel : signalAdapterModels) {
                signalAdapterModel.setSelected(checked);
            }
            adapter.notifyDataSetChanged();
        });

        final Button signalMerge = dialog.findViewById(R.id.signal_merge);
        signalMerge.setOnClickListener((view) -> {
            final List<SnapshotItemWithSensorType> signalAdapterModelList = new ArrayList<>();
            for (SignalAdapterModel signalAdapterModel : signalAdapterModels) {
                if (signalAdapterModel.isSelected()) {
                    signalAdapterModelList.add(signalAdapterModel.getSnapshotItemWithSensorType());
                }
            }

            if (isSightUpdateOnMerge) {
                if (bearingTraining.siteUpdateOnMerge(siteName, snapshotObservations, signalAdapterModelList)) {
                    Toast.makeText(getActivity(), "Merge Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Merge Failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (bearingTraining.updateSnapshot(siteName, snapshotObservations, signalAdapterModelList)) {
                    Toast.makeText(getActivity(), "Merge Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Merge Failed", Toast.LENGTH_SHORT).show();
                }
            }
            dialog.dismiss();

            loadSiteNamesIntoList();
        });

        dialog.show();

    }

    private boolean checkConnection() {
        final boolean isConnected = InternetConnectionReceiver.isInternetConnected();
        if (!isConnected) {
            ((MainActivity) Objects.requireNonNull(getActivity())).showSnack();
        }
        return isConnected;
    }

    @Override
    public void onSiteUploadsiteClick(int position) {
        // NOT USED
    }

    @Override
    public void onSiteConfigurationClick(int position) {
        // NOT USED
    }

    @Override
    public void onSiteSignalMergeClick(int position) {
        final SiteAdapterModel siteAdapterModel = siteAdapterModels.get(position);
        String siteName = siteAdapterModel.getSiteName();

        DialogUtil.getModeOfTrainingDialog(getActivity(), "Mode of Training", status -> {
            Toast.makeText(getActivity(), "Processing please wait..", Toast.LENGTH_SHORT).show();

            final Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            dialog.setContentView(R.layout.custom_progress_bar);

            final TextView progressTitle = dialog.findViewById(R.id.progress_title);
            progressTitle.setText("");

            dialog.show();

            if (status) {
                bearingTraining.mergeSite(siteName, new IBearingTrainingCallback.IBearingSiteSignalMergeListener() {
                    @Override
                    public void onSuccess(final String siteName1,
                                          final List<SnapshotObservation> snapshotObservations,
                                          final List<SnapshotItemWithSensorType> snapshotItemWithSensorTypeList) {

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        if (snapshotItemWithSensorTypeList != null && !snapshotItemWithSensorTypeList.isEmpty()) {
                            filterAccessPoints(siteName1, snapshotObservations, snapshotItemWithSensorTypeList, true);
                        } else {
                            Toast.makeText(getActivity(), "No Accesspoints", Toast.LENGTH_SHORT).show();
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
            } else {
                bearingTraining.mergeBLESite(siteName, new IBearingTrainingCallback.IBearingBleSiteSignalMergeListener() {
                    @Override
                    public void onSuccess(List<SnapshotObservation> observations, List<ScannedBleDetails> bleSourceId) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (bleSourceId != null && !bleSourceId.isEmpty()) {
                            filterSourceIds(siteName, observations, bleSourceId, true);
                        } else {
                            Toast.makeText(getActivity(), "No Source Ids", Toast.LENGTH_SHORT).show();
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
            }
        });


    }

    @Override
    public void onSiteDeleteClick(int position) {
        // NOT USED
    }

    @Override
    public void onSiteClick(int position) {
        final SiteAdapterModel siteAdapterModel = siteAdapterModels.get(position);
        Intent intent = new Intent(getActivity(), SiteLocationActivity.class);
        intent.putExtra(KEYS.SITE_ADAPTER_MODEL, siteAdapterModel);
        startActivity(intent);
    }

    @Override
    public void onStoreConfigurationClick(int position) {
        //NOT USED
    }

    @Override
    public void onEditRssiClick(int position) {
        // NOT USED
    }


    public void downloadSiteAndLocations() {

        DialogUtil.getConfirmation(getActivity(), getString(R.string.are_you_sure), getString(R.string.previous_data_will_be_nolonger_available_from_local), this::downloadSiteAndLocationFromServer);

    }


    public void downloadSiteAndLocationFromServer() {
        initializeBearingTrainingAndSetServerEndpoint();
        final Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.custom_progress_bar);

        final TextView progressTitle = dialog.findViewById(R.id.progress_title);
        progressTitle.setVisibility(View.GONE);

        dialog.show();

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }, 15000);


        bearingTraining.deleteBearingData(null, null, new IBearingTrainingCallback.IBearingDataDelete() {
            @Override
            public void onSuccess() {
                bearingTraining.downloadAllSitesAndLocationsFromServer(new IBearingTrainingCallback.IBearingSuncWithServerListener() {
                    @Override
                    public void onSuccess() {

                        loadSiteNamesIntoList();

                        if (dialog.isShowing()) {

                            dialog.dismiss();

                        }
                    }

                    @Override
                    public void onFailure(final String errorMessage) {
                        handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
                        loadSiteNamesIntoList();
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onFailure() {
                handler.sendMessage(handler.obtainMessage(ALERT_POPUP, "LOCAL DELETION FAILED"));
                loadSiteNamesIntoList();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }

    public interface OnRequestPermissionListener {

        void onLocationPermissionRequest();

    }
}