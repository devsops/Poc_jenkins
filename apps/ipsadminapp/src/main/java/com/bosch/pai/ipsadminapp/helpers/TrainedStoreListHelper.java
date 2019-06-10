package com.bosch.pai.ipsadminapp.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bosch.pai.ipsadminapp.R;
import com.bosch.pai.ipsadminapp.utilities.DialogUtil;
import com.bosch.pai.ipsadminapp.utilities.SettingsPreferences;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.Training;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.callback.IBearingTrainingCallback;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.impl.BearingTrainingImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TrainedStoreListHelper {

    public TrainedStoreListHelper() {
        //default
    }

    @FunctionalInterface
    public interface IGetStoreNamesFromServer {

        void onStoreSelected(String storeId, String siteNames);

    }

    public void getTrainedStorelistfromserver(View view, final Activity context, final IGetStoreNamesFromServer listener) {

        final SettingsPreferences settingsPreferences = new SettingsPreferences(context);
        final String bearingCompleteUrl = settingsPreferences.getBearingServiceURL();
        final Training bearingTraining = BearingTrainingImpl.getInstance(context, bearingCompleteUrl);

        final List<String> siteList = new ArrayList<>();
        siteList.add(context.getString(R.string.select_store_id));

        final Spinner storeIdSpinner = view.findViewById(R.id.storeIdList);

        final ArrayAdapter<String> storeIdAdapter =
                new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, siteList);
        storeIdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storeIdSpinner.setAdapter(storeIdAdapter);
        storeIdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                final String site = (String) adapterView.getItemAtPosition(position);
                if (!site.equals(context.getString(R.string.select_store_id))) {

                    final String storeId = site.split("_")[0];
                    listener.onStoreSelected(storeId, site);
                } else {
                    listener.onStoreSelected("", "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(context, "nothing selected", Toast.LENGTH_SHORT).show();
            }
        });

        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.custom_progress_bar);

        final TextView progressTitle = dialog.findViewById(R.id.progress_title);
        progressTitle.setText("");
        progressTitle.setVisibility(View.GONE);

        dialog.show();

        bearingTraining.getAllSiteNamesFromServer(
                new IBearingTrainingCallback.IUtilityGetSiteListListenerFromServer() {
                    @Override
                    public void onSuccess(final Set<String> siteNames) {
                        siteList.clear();
                        siteList.add(context.getString(R.string.select_store_id));
                        siteList.addAll(siteNames);

                        storeIdAdapter.notifyDataSetChanged();
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(final String errorMessage) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        DialogUtil.showAlertDialogOnError(context, errorMessage);
                    }
                });

    }

}
