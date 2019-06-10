package com.bosch.pai.ipsadminapp.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bosch.pai.ipsadminapp.R;

/**
 * Created by sjn8kor on 1/19/2018.
 */

public class DialogUtil {

    private static final String error = "Error";

    private DialogUtil() {
        //default constructor
    }

    public static void showAlertDialogOnError(final Activity activity, final String errorMessage) {
        activity.runOnUiThread(() -> {
            try {
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(activity);
                builder.setTitle(error);
                builder.setMessage(errorMessage);
                builder.setPositiveButton("OK",
                        (DialogInterface dialog, int which) -> dialog.dismiss());
                builder.show();

            } catch (Exception e) {
                Log.e(error, e.getMessage(), e);
            }
        });
    }

    public static void showConfirmationPopup(final Context context, String title, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.confirm_layout);

        final TextView titleText = dialog.findViewById(R.id.confirm_title);
        final TextView messageText = dialog.findViewById(R.id.confirm_message);

        titleText.setText(title);
        messageText.setText(message);

        final Button sure = dialog.findViewById(R.id.confirm_sure);
        final Button cancel = dialog.findViewById(R.id.confirm_cancel);

        sure.setOnClickListener(view -> {
            /* write your code here*/
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(view -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static Dialog showProgreeBarPopup(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.custom_progress_bar);

        final TextView progressTitle = dialog.findViewById(R.id.progress_title);
        progressTitle.setText("");

        dialog.show();
        return dialog;
    }

    public interface IPermissionGranted {

        void permissionGranted();

    }

    public interface IGetModeofTraining {

        void isWifiMode(boolean status);

    }

    public interface ITwoTaskPermission {

        void firstTask();

        void secondTask();

    }

    public static void getConfirmation(Context context,
                                       String title,
                                       String message,
                                       final IPermissionGranted listener) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.confirm_layout);

        final TextView titleText = dialog.findViewById(R.id.confirm_title);
        final TextView messageText = dialog.findViewById(R.id.confirm_message);

        titleText.setText(title);
        messageText.setText(message);

        final Button sure = dialog.findViewById(R.id.confirm_sure);
        final Button cancel = dialog.findViewById(R.id.confirm_cancel);

        sure.setOnClickListener((view) -> {
            listener.permissionGranted();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener((view) -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void getModeOfTrainingDialog(Context context,
                                               String title,
                                               final IGetModeofTraining listener) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.training_mode_settings_layout);

        final TextView titleText = dialog.findViewById(R.id.trainingmode_title_textview);
        final RadioGroup radioGroup = dialog.findViewById(R.id.trainingmode_radiogrp);

        titleText.setText(title);

        final Button sure = dialog.findViewById(R.id.trainingmode_sure);
        final Button cancel = dialog.findViewById(R.id.trainingmode_cancel);

        sure.setOnClickListener((View view) -> {

            final int selectedId = radioGroup.getCheckedRadioButtonId();
            final RadioButton radioButton = dialog.findViewById(selectedId);

            final String mode = radioButton.getText().toString();

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (mode.equals(context.getString(R.string.wifi))){
                listener.isWifiMode(true);
            }else {
                listener.isWifiMode(false);
            }

        });

        cancel.setOnClickListener((View view) -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void handleTwoTasks(Context context,
                                      String task1,
                                      String task2,
                                      final ITwoTaskPermission listener) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.twotasklayout);

        final Button task1Button = dialog.findViewById(R.id.task1);
        task1Button.setText(task1);
        final Button task2Button = dialog.findViewById(R.id.task2);
        task2Button.setText(task2);

        task1Button.setOnClickListener((view) -> {
            listener.firstTask();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        task2Button.setOnClickListener((view) -> {
            listener.secondTask();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
