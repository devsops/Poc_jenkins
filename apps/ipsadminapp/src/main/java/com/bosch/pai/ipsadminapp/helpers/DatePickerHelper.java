package com.bosch.pai.ipsadminapp.helpers;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bosch.pai.ipsadminapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerHelper {

    private static final String LOG_TAG = DatePickerHelper.class.getSimpleName();

    public DatePickerHelper() {
        //default constructor
    }

    public interface DatePickerListener {

        void onStartDateSelect(long startTime);

        void onEndDateSelect(long endTime);

    }

    public boolean isEndDateGreaterThanCurrentDate(long endTime) {
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        final String currentDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
        final String currentDateFormat = currentDate + " 23:59:59";

        final long todayDateInMillis = getTimeStamp(currentDateFormat);
        return endTime > todayDateInMillis;
    }

    public void loadDatepickerViews(View view, final Context context, final DatePickerListener listener) {
        final TextView starttimetv = view.findViewById(R.id.starttimetv);
        final TextView endtimetv = view.findViewById(R.id.endtimetv);
        final ImageButton starttimebtn = view.findViewById(R.id.starttimebtn);
        final ImageButton endtimebtn = view.findViewById(R.id.endtimebtn);

        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        final String startTextviewValue = context.getString(R.string.startdate);
        final String endTextviewValue = context.getString(R.string.enddate);

        final String startDateTvData = mYear + "-" + (mMonth + 1) + "-" + mDay;
        final String startTimeText = startDateTvData + " 00:00:00";
        final String endDateTvData = mYear + "-" + (mMonth + 1) + "-" + mDay;
        final String endTimeText = endDateTvData + " 23:59:59";

        starttimetv.setText(startTextviewValue + " : " + startDateTvData);
        endtimetv.setText(endTextviewValue + " : " + endDateTvData);

        final long startTimeLong = getTimeStamp(startTimeText);
        listener.onStartDateSelect(startTimeLong);

        final long endTimeLong = getTimeStamp(endTimeText);
        listener.onEndDateSelect(endTimeLong);

        starttimebtn.setOnClickListener((View v) -> {

            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (DatePicker dpview, int year, int monthOfYear, int dayOfMonth) -> {

                        final String startTimeTvData = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        final String startTimeText2 = startTimeTvData + " 00:00:00";

                        final long startTime =
                                getTimeStamp(startTimeText2);
                        if (startTime != 0) {
                            starttimetv.setText(startTextviewValue + " : " + startTimeTvData);
                            listener.onStartDateSelect(startTime);
                        } else {
                            Toast.makeText(context, "Select Valid Date", Toast.LENGTH_SHORT).show();
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        endtimebtn.setOnClickListener((View v) -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (DatePicker dpview, int year,
                     int monthOfYear, int dayOfMonth) -> {

                        final String endDateTvValue = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        final String endTimeText2 = endDateTvValue + " 23:59:59";

                        final long endTime =
                                getTimeStamp(endTimeText2);
                        if (endTime != 0) {
                            endtimetv.setText(endTextviewValue + " : " + endDateTvValue);
                            listener.onEndDateSelect(endTime);
                        } else {
                            Toast.makeText(context, "Select Valid Date", Toast.LENGTH_SHORT).show();
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

        });
    }

    public boolean checkIfStartAndEndDatesSatifiesConditons(long startTime, long endTime) {
        return startTime < endTime;
    }

    private long getTimeStamp(String dateString) {
        try {
            final SimpleDateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            dateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
            final Date parsedDate = dateFormat.parse(dateString);
            return parsedDate.getTime();
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error in parsing date. ", e);
        }
        return 0;
    }

}
