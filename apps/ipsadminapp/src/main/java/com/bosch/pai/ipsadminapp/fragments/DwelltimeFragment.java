package com.bosch.pai.ipsadminapp.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.bosch.pai.retail.analytics.model.dwelltime.LocationDwellTime;
import com.bosch.pai.ipsadmin.retail.pmadminlib.Util;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.Analytics;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.callback.IAnalyticsCallbacks;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.impl.AnalyticsImpl;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DwelltimeFragment extends Fragment {

    private static final String LOG_TAG = DwelltimeFragment.class.getSimpleName();
    private static final int DWELL_TIME = 4;
    private static final int ALERT_POPUP = 41;

    private Handler handler;

    private PieChart pieChart;
    private DatePickerHelper datePickerUtil;
    private long startTime;
    private long endTime;

    private String companyName;
    private String storeId;
    private String siteName;

    private Handler.Callback callback = (Message message) -> {
        if (message.what == ALERT_POPUP) {
            final String errorMessage = (String) message.obj;
            DialogUtil.showAlertDialogOnError(getActivity(), errorMessage);
        } else if (message.what == DWELL_TIME) {
            try {
                List<LocationDwellTime> locationDwellTimeList = (List<LocationDwellTime>) message.obj;
                if (locationDwellTimeList != null && !locationDwellTimeList.isEmpty()) {

                    final List<LocationDwellTime> locationDwellTimes = new ArrayList<>(locationDwellTimeList);
                    Collections.sort(locationDwellTimes,
                            (LocationDwellTime locationDwellTime, LocationDwellTime t1) ->
                                    locationDwellTime.getLocationName().compareTo(t1.getLocationName())
                    );
                    handleLocationDwelltime(locationDwellTimes);
                } else {
                    Toast.makeText(getActivity(), "Response is Empty", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, " Error : " + e.getMessage(), e);
            }
        }
        return false;
    };

    private void handleLocationDwelltime(List<LocationDwellTime> locationDwellTimes) {

        final List<PieEntry> chartXYDataValues = new ArrayList<>();
        for (LocationDwellTime locationDwellTime : locationDwellTimes) {
            final String sectionName = locationDwellTime.getLocationName();
            final PieEntry pieEntry = new PieEntry(locationDwellTime.getAverageDuration(), sectionName.toUpperCase() + " " +
                    locationDwellTime.getAverageDuration());
            chartXYDataValues.add(pieEntry);
        }
        final PieDataSet pieDataSet = new PieDataSet(chartXYDataValues, "");
        List<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        pieDataSet.setColors(colors);
        pieDataSet.setSliceSpace(4f);
        final PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setData(pieData);
        for (IDataSet<?> set : pieChart.getData().getDataSets())
            set.setDrawValues(!set.isDrawValuesEnabled());
        pieChart.setDrawEntryLabels(false);
        pieChart.animateX(300);
        pieChart.invalidate();

    }

    public DwelltimeFragment() {
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
            submitDwelltime();
        }
        return false;
    }

    private void submitDwelltime() {
        if (startTime == 0 || endTime == 0) {
            Toast.makeText(getActivity(), "Select both Start time and End time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (datePickerUtil.checkIfStartAndEndDatesSatifiesConditons(startTime, endTime)) {
            if (null == this.storeId || this.storeId.isEmpty() || null == this.siteName || this.siteName.isEmpty()) {
                Toast.makeText(getActivity(), "Select Store", Toast.LENGTH_SHORT).show();
            } else {
                getDwelltime(storeId, siteName, null, startTime, endTime);
            }
        } else {
            Toast.makeText(getActivity(), "Start time should be less than EndTime", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_dwelltime, container, false);

        final ProximityAdminSharedPreference preference = ProximityAdminSharedPreference.getInstance(getActivity());

        companyName = preference.getCompany();

        pieChart = view.findViewById(R.id.dwelltime_piechart);
        setUpPieChart(pieChart);

        final TrainedStoreListHelper storeListUtil = new TrainedStoreListHelper();
        if (Util.UserType.ROLE_PREMIUM == Util.getUserType()) {
            storeListUtil.getTrainedStorelistfromserver(view, getActivity(), (storeId, siteName) -> {
                DwelltimeFragment.this.storeId = storeId;
                DwelltimeFragment.this.siteName = siteName;
            });
        }

        handler = new Handler(callback);
        datePickerUtil = new DatePickerHelper();

        datePickerUtil.loadDatepickerViews(view,
                getActivity(),
                new DatePickerHelper.DatePickerListener() {
                    @Override
                    public void onStartDateSelect(long startTime) {
                        DwelltimeFragment.this.startTime = startTime;
                    }

                    @Override
                    public void onEndDateSelect(long endTime) {
                        DwelltimeFragment.this.endTime = endTime;
                    }
                });

        checkUserTypeAndLoadDummyDataIfNeeded(view);
        return view;
    }

    private void checkUserTypeAndLoadDummyDataIfNeeded(View view) {
        //TODO
        final Util.UserType userType = Util.getUserType();
        switch (userType) {
            case ROLE_FREE:
                view.findViewById(R.id.dwelltime_storelayout).setVisibility(View.GONE);
                view.findViewById(R.id.dwelltime_date_layout).setVisibility(View.GONE);
                view.findViewById(R.id.dummyData).setVisibility(View.VISIBLE);
                getDummyDwellTimeData();
                break;
            case ROLE_PAID:
            case ROLE_PREMIUM:
                view.findViewById(R.id.dwelltime_storelayout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.dwelltime_date_layout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.dummyData).setVisibility(View.GONE);
            default:
                Log.e(LOG_TAG, "Not a valid user type!", null);

        }
    }

    private void getDummyDwellTimeData() {
        final List<LocationDwellTime> locationDwellTimeList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final LocationDwellTime locationDwellTime = new LocationDwellTime();
            locationDwellTime.setCompanyId("DUMMY");
            locationDwellTime.setSiteName("DummySite");
            locationDwellTime.setStoreId("dummy");
            locationDwellTime.setLocationName("Location " + (i + 1));
            locationDwellTime.setAverageDuration((float) new SecureRandom().nextInt(50));
            locationDwellTime.setUserCount(new SecureRandom().nextInt(50));
            locationDwellTime.setStartTime(System.currentTimeMillis());
            locationDwellTime.setEndTime(System.currentTimeMillis());
            locationDwellTimeList.add(locationDwellTime);
        }
        handler.sendMessage(handler.obtainMessage(DWELL_TIME, locationDwellTimeList));
    }

    private void setUpPieChart(PieChart pieChart) {
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(35f);
        pieChart.setHoleRadius(30f);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setCenterText("");
        pieChart.setTransparentCircleColor(Color.GRAY);
        pieChart.setTouchEnabled(false);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.setCenterTextColor(Color.GRAY);
        pieChart.setDrawEntryLabels(true);

        final Description description = new Description();
        description.setText("DwellTimeAnalytics");
        description.setTextColor(Color.GRAY);
        pieChart.setDescription(description);
        pieChart.setUsePercentValues(true);
        Legend pieChartLegend = pieChart.getLegend();
        pieChartLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        pieChartLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        pieChartLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        pieChartLegend.setTextColor(Color.GRAY);
        pieChartLegend.setDrawInside(false);
        pieChartLegend.setXEntrySpace(7f);
        pieChartLegend.setYEntrySpace(0f);
        pieChartLegend.setYOffset(0f);
    }


    public void getDwelltime(String store,
                             String siteName,
                             String locationName,
                             long startTime,
                             long endTime) {

        pieChart.clear();
        final Dialog dialog = DialogUtil.showProgreeBarPopup(getActivity());
        final String proximityUrl = new SettingsPreferences(getActivity()).getProximityServiceURL();

        final Analytics analytics = AnalyticsImpl.getInstance(proximityUrl);
        dialog.show();
        analytics.getDwellTimeAnalytics(companyName,
                store, siteName, locationName, startTime, endTime,
                new IAnalyticsCallbacks.IDwelltimeListener() {
                    @Override
                    public void onSuccess(List<LocationDwellTime> locationDwellTimes) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        handler.sendMessage(handler.obtainMessage(DWELL_TIME, locationDwellTimes));
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

        public LabelValueFormatter(List<String> labels) {
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
