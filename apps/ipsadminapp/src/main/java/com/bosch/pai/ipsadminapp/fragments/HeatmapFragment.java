package com.bosch.pai.ipsadminapp.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bosch.pai.ipsadminapp.R;
import com.bosch.pai.ipsadminapp.helpers.DatePickerHelper;
import com.bosch.pai.ipsadminapp.helpers.TrainedStoreListHelper;
import com.bosch.pai.ipsadminapp.utilities.DialogUtil;
import com.bosch.pai.ipsadminapp.utilities.ProximityAdminSharedPreference;
import com.bosch.pai.ipsadminapp.utilities.SettingsPreferences;
import com.bosch.pai.retail.analytics.model.heatmap.HeatMapDetail;
import com.bosch.pai.ipsadmin.retail.pmadminlib.Util;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.Analytics;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.callback.IAnalyticsCallbacks;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.impl.AnalyticsImpl;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HeatmapFragment extends Fragment {

    private static final String LOG_TAG = HeatmapFragment.class.getSimpleName();
    private static final int HEAT_MAP = 6;
    private static final int ALERT_POPUP = 61;

    private Handler handler;
    private DatePickerHelper datePickerUtil;
    private long startTime;
    private long endTime;

    private String storeId;
    private String siteName;

    private BarChart heatmapBarchart;

    private Handler.Callback callback = (Message message) -> {
        if (message.what == ALERT_POPUP) {

            final String errorMessage = (String) message.obj;
            DialogUtil.showAlertDialogOnError(Objects.requireNonNull(getActivity()), errorMessage);

        }
        if (message.what == HEAT_MAP) {
            try {
                List<HeatMapDetail> mapDetailList = (List<HeatMapDetail>) message.obj;
                if (mapDetailList != null && !mapDetailList.isEmpty()) {
                    final List<HeatMapDetail> heatMapDetailList = new ArrayList<>(mapDetailList);

                    Collections.sort(heatMapDetailList,
                            (HeatMapDetail heatMapDetail, HeatMapDetail t1) ->
                                    heatMapDetail.getLocationName().compareTo(t1.getLocationName())
                    );

                    handleHeatMapDetails(heatMapDetailList);
                } else {
                    Toast.makeText(getActivity(), "Response is empty", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, "Error : " + e.getMessage(), e);
            }
        }
        return false;
    };

    private void handleHeatMapDetails(List<HeatMapDetail> heatMapDetailList) {

        final List<BarEntry> entries = new ArrayList<>();
        final List<String> labels = new ArrayList<>();
        int counter = 0;
        for (HeatMapDetail entry : heatMapDetailList) {
            entries.add(new BarEntry(counter, entry.getUserCount(), entry.getLocationName()));
            labels.add(entry.getLocationName());
            counter++;
        }

        BarDataSet barDataSet = new BarDataSet(entries, "People count at each location");

        barDataSet.setDrawIcons(false);

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);
        heatmapBarchart.setData(data);
        heatmapBarchart.setEnabled(true);
        heatmapBarchart.getXAxis().setValueFormatter(new LabelValueFormatter(labels));
        heatmapBarchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        heatmapBarchart.setVisibleXRangeMaximum(3); // item should be shown in x axis

        heatmapBarchart.animateY(300);
        heatmapBarchart.getXAxis().setDrawAxisLine(true);

    }


    public HeatmapFragment() {
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

        menu.clear();
        inflater.inflate(R.menu.analytics_menu, menu);
        if (Util.UserType.ROLE_PREMIUM != Util.getUserType()) {
            menu.findItem(R.id.submit).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.submit) {
            submitHeatmap();
        }
        return false;
    }

    private void submitHeatmap() {
        if (startTime == 0 || endTime == 0) {
            Toast.makeText(getActivity(), "Select both Start time and End time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (datePickerUtil.checkIfStartAndEndDatesSatifiesConditons(startTime, endTime)) {
            if (null == this.storeId || this.storeId.isEmpty() || null == this.siteName || this.siteName.isEmpty()) {
                Toast.makeText(getActivity(), "Select Store", Toast.LENGTH_SHORT).show();
            } else {
                getHeatMap(storeId, siteName, null, startTime, endTime);
            }
        } else {
            Toast.makeText(getActivity(), "Start time should be less than EndTime", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_heatmap, container, false);

        heatmapBarchart = view.findViewById(R.id.heatmap_barchart);

        setBarChart(heatmapBarchart);

        final TrainedStoreListHelper storeListUtil = new TrainedStoreListHelper();
        if (Util.UserType.ROLE_PREMIUM == Util.getUserType()) {
            storeListUtil.getTrainedStorelistfromserver(view, getActivity(), (String store, String site) -> {
                HeatmapFragment.this.storeId = store;
                HeatmapFragment.this.siteName = site;
            });
        }
        datePickerUtil = new DatePickerHelper();

        datePickerUtil.loadDatepickerViews(view,
                getActivity(),
                new DatePickerHelper.DatePickerListener() {
                    @Override
                    public void onStartDateSelect(long startTime) {
                        HeatmapFragment.this.startTime = startTime;
                    }

                    @Override
                    public void onEndDateSelect(long endTime) {
                        HeatmapFragment.this.endTime = endTime;
                    }
                });

        handler = new Handler(callback);
        checkUserTypeAndLoadDummyDataIfNeeded(view);
        return view;
    }

    private void checkUserTypeAndLoadDummyDataIfNeeded(View view) {
        final Util.UserType userType = Util.getUserType();
        switch (userType) {
            case ROLE_FREE:
                view.findViewById(R.id.heatmap_date_layout).setVisibility(View.GONE);
                view.findViewById(R.id.heatmap_storelayout).setVisibility(View.GONE);
                view.findViewById(R.id.dummyData).setVisibility(View.VISIBLE);
                getDummyHeatMapData();
                break;
            case ROLE_PREMIUM:
            case ROLE_PAID:
                view.findViewById(R.id.heatmap_date_layout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.heatmap_storelayout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.dummyData).setVisibility(View.GONE);
            default:
                Log.e(LOG_TAG, "Not a valid user type!", null);
        }
    }

    private void getDummyHeatMapData() {
        final List<HeatMapDetail> heatMapDetails = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final HeatMapDetail heatMapDetail = new HeatMapDetail();
            heatMapDetail.setCompanyName("DUMMY");
            heatMapDetail.setStoreId("dummy");
            heatMapDetail.setSiteName("DummySite");
            heatMapDetail.setLocationName("Location " + (i + 1));
            heatMapDetail.setUserCount(new SecureRandom().nextInt(9) + 1);
            heatMapDetail.setStartTime(System.currentTimeMillis());
            heatMapDetail.setEndTime(System.currentTimeMillis());
            heatMapDetails.add(heatMapDetail);
        }
        handler.sendMessage(handler.obtainMessage(HEAT_MAP, heatMapDetails));
    }

    private void setBarChart(BarChart heatmapChart) {
        heatmapChart.setDrawBarShadow(false);
        heatmapChart.setDrawValueAboveBar(false);

        heatmapChart.setScaleEnabled(false);
        heatmapChart.setHighlightFullBarEnabled(false);


        heatmapChart.getDescription().setEnabled(false);

        // heatmapChart.setMaxVisibleValueCount(60);
        heatmapChart.setPinchZoom(false);
        heatmapChart.setDrawGridBackground(true);

        XAxis xAxis = heatmapChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);


        YAxis leftAxis = heatmapChart.getAxisLeft();
        leftAxis.setLabelCount(4, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = heatmapChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    public void getHeatMap(String store,
                           String siteName,
                           String locationName,
                           long startTime,
                           long endTime) {

        heatmapBarchart.clear();
        final Dialog dialog = DialogUtil.showProgreeBarPopup(getActivity());
        final String proximityUrl = new SettingsPreferences(getActivity()).getProximityServiceURL();

        final Analytics analytics = AnalyticsImpl.getInstance(proximityUrl);
        final ProximityAdminSharedPreference preference =
                ProximityAdminSharedPreference.getInstance(getActivity());
        dialog.show();
        analytics.getHeatMapDetails(preference.getCompany(),
                store, siteName, locationName, startTime, endTime,
                new IAnalyticsCallbacks.IHeatmapListener() {

                    @Override
                    public void onSuccess(List<HeatMapDetail> heatMapDetails) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        handler.sendMessage(handler.obtainMessage(HEAT_MAP, heatMapDetails));
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
                    }
                });
    }

    public class LabelValueFormatter implements IAxisValueFormatter {
        private final List<String> labels;

        LabelValueFormatter(List<String> labels) {
            this.labels = labels != null ? new ArrayList<>(labels) :
                    new ArrayList<>();
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            try {
                int index = (int) value;
                return labels.get(index);
            } catch (Exception e) {
                return "";
            }
        }
    }


}
