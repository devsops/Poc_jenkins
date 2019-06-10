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
import com.bosch.pai.retail.analytics.model.entryexit.DayDetails;
import com.bosch.pai.retail.analytics.model.entryexit.EntryExitDetails;
import com.bosch.pai.retail.analytics.model.entryexit.HourDetails;
import com.bosch.pai.retail.analytics.model.entryexit.IntervalDetails;
import com.bosch.pai.retail.analytics.model.entryexit.MonthsDetails;
import com.bosch.pai.retail.analytics.model.entryexit.YearsDetails;
import com.bosch.pai.retail.analytics.responses.EntryExitResponse;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.ipsadmin.retail.pmadminlib.Util;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.Analytics;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.callback.IAnalyticsCallbacks;
import com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.impl.AnalyticsImpl;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class EntryexitFragment extends Fragment {

    private static final String LOG_TAG = EntryexitFragment.class.getSimpleName();
    private static final int ENTRY_EXIT = 5;
    private static final int ALERT_POPUP = 51;

    private Handler handler;

    private BarChart entryexitScatterChart;
    private DatePickerHelper datePickerUtil;
    private long startTime;
    private long endTime;

    private String storeId;
    private String siteName;

    private Handler.Callback callback = (Message message) -> {
        if (message.what == ALERT_POPUP) {
            final String errorMessage = (String) message.obj;
            DialogUtil.showAlertDialogOnError(getActivity(), "No Data for this store");
            Toast.makeText(getActivity(), "Response is empty", Toast.LENGTH_SHORT).show();

        } else if (message.what == ENTRY_EXIT) {
            try {
                EntryExitResponse obj = (EntryExitResponse) message.obj;
                final EntryExitResponse entryExitResponse = new EntryExitResponse();
                entryExitResponse.setStatusMessage(obj.getStatusMessage());
                entryExitResponse.setIntervalDetails(obj.getIntervalDetails());
                entryExitResponse.setEntryExitDetails(obj.getEntryExitDetails());
                if (entryExitResponse != null && entryExitResponse.getEntryExitDetails() != null) {
                    final IntervalDetails intervalDetails = entryExitResponse.getIntervalDetails();
                    if (intervalDetails != null) {
                        switch (intervalDetails) {
                            case YEARLY:
                                handleYearlyResponse(entryExitResponse.getEntryExitDetails());
                                break;
                            case MONTHLY:
                                handleMonthlyResponse(entryExitResponse.getEntryExitDetails());
                                break;
                            case DAILY:
                                handlerDailyResponse(entryExitResponse.getEntryExitDetails());
                                break;
                            case HOURLY:
                                handleHourlyResponse(entryExitResponse.getEntryExitDetails());
                                break;
                            default:
                                Toast.makeText(getActivity(), "Invalid response type", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Response is empty", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error : " + e.getMessage(), e);
            }
        }
        return false;
    };

    private void handleHourlyResponse(EntryExitDetails entryExitDetails) {

        final YearsDetails yearsDetails = entryExitDetails.getYears().get(0);
        final MonthsDetails monthsDetails = yearsDetails.getMonths().get(0);
        final DayDetails dayDetails = monthsDetails.getDays().get(0);

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (HourDetails hourDetails : dayDetails.getHours()) {
            final Long entryCount = hourDetails.getEntryCount();
            final Long exitCount = hourDetails.getExitCount();
            barEntries.add(new BarEntry((float) hourDetails.getHour(), new float[]{entryCount, exitCount}));
        }

        invalidateChartWithDataset(barEntries, yearsDetails.getYear(), monthsDetails.getMonth(), dayDetails.getDay());
    }

    private void handlerDailyResponse(EntryExitDetails entryExitDetails) {

        final YearsDetails yearsDetails = entryExitDetails.getYears().get(0);
        final MonthsDetails monthsDetails = yearsDetails.getMonths().get(0);

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (DayDetails dayDetails : monthsDetails.getDays()) {
            final Long entryCount = dayDetails.getEntryCount();
            final Long exitCount = dayDetails.getExitCount();
            barEntries.add(new BarEntry((float) dayDetails.getDay(), new float[]{entryCount, exitCount}));
        }

        invalidateChartWithDataset(barEntries, yearsDetails.getYear(), monthsDetails.getMonth(), null);
    }

    private void handleMonthlyResponse(EntryExitDetails entryExitDetails) {
        final List<YearsDetails> yearsDetailsList = entryExitDetails.getYears();

        final YearsDetails yearsDetails = yearsDetailsList.get(0);

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (MonthsDetails monthsDetails : yearsDetails.getMonths()) {
            final Long entryCount = monthsDetails.getEntryCount();
            final Long exitCount = monthsDetails.getExitCount();
            barEntries.add(new BarEntry((float) monthsDetails.getMonth(), new float[]{entryCount, exitCount}));
        }

        invalidateChartWithDataset(barEntries, yearsDetails.getYear(), null, null);
    }

    private void handleYearlyResponse(EntryExitDetails entryExitDetails) {

        final List<YearsDetails> yearsDetailsList = entryExitDetails.getYears();

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (YearsDetails yearsDetails : yearsDetailsList) {
            final Long entryCount = yearsDetails.getEntryCount();
            final Long exitCount = yearsDetails.getExitCount();

            barEntries.add(new BarEntry((float) yearsDetails.getYear(), new float[]{entryCount, exitCount}));
        }

        invalidateChartWithDataset(barEntries, null, null, null);
    }

    private void invalidateChartWithDataset(ArrayList<BarEntry> barEntries, Integer year, Integer month, Integer day) {
        String displayText = "Entry Exit AnalyticsImpl";
        if (year != null) {
            displayText += " of " + year + "year";
            if (month != null) {
                displayText += " " + month + " month";
                if (day != null) {
                    displayText += " " + day + " day";
                }
            }
        }
        displayText += ".";
        BarDataSet set1 = new BarDataSet(barEntries, displayText);
        set1.setDrawIcons(false);
        set1.setColors(getColors());
        set1.setStackLabels(new String[]{"Entry", "Exit"});

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextColor(Color.WHITE);

        entryexitScatterChart.setData(data);

        entryexitScatterChart.setFitBars(true);
        entryexitScatterChart.invalidate();
    }

    public EntryexitFragment() {
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
            submitEntryExittime();
        }
        return false;
    }

    private void submitEntryExittime() {
        if (datePickerUtil.isEndDateGreaterThanCurrentDate(endTime)) {
            Toast.makeText(getActivity(), "EndTime can not be greater than current date!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!datePickerUtil.checkIfStartAndEndDatesSatifiesConditons(startTime, endTime)) {
            Toast.makeText(getActivity(), "Start time should be less than EndTime", Toast.LENGTH_SHORT).show();
            return;
        }
        if (null == this.storeId || this.storeId.isEmpty() || null == this.siteName || this.siteName.isEmpty()) {
            Toast.makeText(getActivity(), "Select Store", Toast.LENGTH_SHORT).show();
            return;
        }
        getEntryExit(storeId, startTime, endTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_entryexit, container, false);

        entryexitScatterChart = view.findViewById(R.id.entryexit_barchart);
        loadEntryExitChartProperties(entryexitScatterChart);

        final TrainedStoreListHelper storeListUtil = new TrainedStoreListHelper();
        if (Util.UserType.ROLE_PREMIUM == Util.getUserType()) {
            storeListUtil.getTrainedStorelistfromserver(view, getActivity(), new TrainedStoreListHelper.IGetStoreNamesFromServer() {
                @Override
                public void onStoreSelected(String storeId, String siteName) {
                    EntryexitFragment.this.storeId = storeId;
                    EntryexitFragment.this.siteName = siteName;
                }
            });
        }

        datePickerUtil = new DatePickerHelper();

        datePickerUtil.loadDatepickerViews(view,
                getActivity(),
                new DatePickerHelper.DatePickerListener() {
                    @Override
                    public void onStartDateSelect(long startTime) {
                        EntryexitFragment.this.startTime = startTime;
                    }

                    @Override
                    public void onEndDateSelect(long endTime) {
                        EntryexitFragment.this.endTime = endTime;
                    }
                });

        handler = new Handler(callback);
        checkUserTypeAndLoadDummyDataIfNeeded(view);
        return view;
    }

    private void checkUserTypeAndLoadDummyDataIfNeeded(View view) {
        //TODO
        final Util.UserType userType = Util.getUserType();
        switch (userType) {
            case ROLE_FREE:
                view.findViewById(R.id.entryexit_storelayout).setVisibility(View.GONE);
                view.findViewById(R.id.entryexit_date_layout).setVisibility(View.GONE);
                view.findViewById(R.id.dummyData).setVisibility(View.VISIBLE);
                getDummyEntryExitDetails();
                break;
            case ROLE_PAID:
            case ROLE_PREMIUM:
                view.findViewById(R.id.entryexit_storelayout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.entryexit_date_layout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.dummyData).setVisibility(View.GONE);
            default:
                Log.e(LOG_TAG, "Not a valid user type!", null);

        }
    }

    private void getDummyEntryExitDetails() {
        final Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH) + 1;

        final EntryExitResponse entryExitResponse = new EntryExitResponse();
        entryExitResponse.setIntervalDetails(IntervalDetails.DAILY);
        final StatusMessage statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS, "Dummy data");
        entryExitResponse.setStatusMessage(statusMessage);

        final EntryExitDetails entryExitDetails = new EntryExitDetails();
        final List<YearsDetails> yearsDetailList = new ArrayList<>();
        final List<MonthsDetails> monthsDetailsList = new ArrayList<>();
        final List<DayDetails> dayDetailsList = new ArrayList<>();

        final YearsDetails yearsDetails = new YearsDetails();
        final MonthsDetails monthsDetails = new MonthsDetails();
        for (int i = 1; i < 3; i++) {
            final DayDetails dayDetails = new DayDetails();
            dayDetails.setDay(i);
            final long temp = new SecureRandom().nextInt(50);
            dayDetails.setExitCount(temp);
            dayDetails.setEntryCount(temp);
            dayDetailsList.add(dayDetails);
        }
        monthsDetails.setMonth(month);
        monthsDetails.setDays(dayDetailsList);
        monthsDetailsList.add(monthsDetails);

        yearsDetails.setYear(year);
        yearsDetails.setMonths(monthsDetailsList);
        yearsDetailList.add(yearsDetails);

        entryExitDetails.setYears(yearsDetailList);
        entryExitResponse.setEntryExitDetails(entryExitDetails);
        handler.sendMessage(handler.obtainMessage(ENTRY_EXIT, entryExitResponse));
    }

    private void loadEntryExitChartProperties(BarChart entryexitScatterChart) {
        entryexitScatterChart.setDrawBarShadow(false);
        entryexitScatterChart.setDrawValueAboveBar(false);

        entryexitScatterChart.setScaleEnabled(false);
        entryexitScatterChart.setHighlightFullBarEnabled(false);

        entryexitScatterChart.getDescription().setEnabled(false);

        //entryexitScatterChart.setMaxVisibleValueCount(60);
        entryexitScatterChart.setPinchZoom(false);
        entryexitScatterChart.setDrawGridBackground(false);
        entryexitScatterChart.setDrawBarShadow(false);

        entryexitScatterChart.setDrawValueAboveBar(false);
        entryexitScatterChart.setHighlightFullBarEnabled(false);

        XAxis xAxis = entryexitScatterChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);


        YAxis leftAxis = entryexitScatterChart.getAxisLeft();
        leftAxis.setEnabled(false);
       /* leftAxis.setLabelCount(4, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
*/
        YAxis rightAxis = entryexitScatterChart.getAxisRight();
        rightAxis.setEnabled(false);


        final Legend l = entryexitScatterChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);
    }

    private int[] getColors() {
        final int stacksize = 2;

        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];

        for (int i = 0; i < colors.length; i++) {
            colors[i] = ColorTemplate.JOYFUL_COLORS[i];
        }

        return colors;
    }

    public void getEntryExit(String store,
                             long startTime,
                             long endTime) {

        entryexitScatterChart.clear();
        final Dialog dialog = DialogUtil.showProgreeBarPopup(getActivity());
        final String proximityUrl = new SettingsPreferences(getActivity()).getProximityServiceURL();
        final Analytics analytics = AnalyticsImpl.getInstance(proximityUrl);
        final ProximityAdminSharedPreference preference =
                ProximityAdminSharedPreference.getInstance(getActivity());
        analytics.getEntryExitDetails(preference.getCompany(),
                store, startTime, endTime, IntervalDetails.DAILY,
                new IAnalyticsCallbacks.IEntryExitListener() {

                    @Override
                    public void onSuccess(EntryExitResponse entryExitResponse) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        handler.sendMessage(handler.obtainMessage(ENTRY_EXIT, entryExitResponse));
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        handler.sendMessage(handler.obtainMessage(ALERT_POPUP, errorMessage));
                    }
                });
    }


}
