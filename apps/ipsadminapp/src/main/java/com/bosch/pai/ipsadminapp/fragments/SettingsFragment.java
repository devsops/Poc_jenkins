package com.bosch.pai.ipsadminapp.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bosch.pai.ipsadminapp.R;
import com.bosch.pai.ipsadminapp.activities.LoginActivity;
import com.bosch.pai.ipsadminapp.utilities.DialogUtil;
import com.bosch.pai.ipsadminapp.utilities.SettingsPreferences;
import com.bosch.pai.ipsadminapp.utilities.UtilMethods;
import com.bosch.pai.ipsadmin.retail.pmadminlib.Util;
import com.bosch.pai.ipsadmin.retail.pmadminlib.authentication.AuthenticationCallback;

import java.util.Objects;

import butterknife.ButterKnife;


public class SettingsFragment extends Fragment implements View.OnClickListener {

    private SettingsPreferences settingsPreferences;

    public SettingsFragment() {
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
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(Objects.requireNonNull(getActivity()), view);

        this.settingsPreferences = new SettingsPreferences(getActivity());

        final CardView changePasswordLayout = view.findViewById(R.id.changePasswordLayout);
        changePasswordLayout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.changePasswordLayout:
                handleChangePassword();
                break;
            default:
                break;
        }
    }

    private void handleChangePassword() {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.dialog_changepassword, null);
        final EditText oldPasswordTxt = view.findViewById(R.id.oldPasswordTxt);
        final EditText newPasswordTxt = view.findViewById(R.id.newPasswordTxt);
        final EditText confirmNewPasswordTxt = view.findViewById(R.id.confirmNewPasswordTxt);
        final TextView errorTxt = view.findViewById(R.id.errorTxt);
        errorTxt.setText("");
        final Button okBtn = view.findViewById(R.id.okBtn);
        final Button cancelBtn = view.findViewById(R.id.cancelBtn);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        okBtn.setOnClickListener(v -> {
            if (!newPasswordTxt.getText().toString().equals(confirmNewPasswordTxt.getText().toString())) {
                errorTxt.setText("Mis match in new password. Please re-type!");
                return;
            }
            if (!UtilMethods.isAValidPassword(newPasswordTxt.getText().toString())) {
                errorTxt.setText("Weak password. Please try giving new password as per hint given below.");
                return;
            }
            final Dialog dialog = DialogUtil.showProgreeBarPopup(getActivity());
            final String oldPwd = Util.getSHA256Conversion(oldPasswordTxt.getText().toString());
            final String newPwd = Util.getSHA256Conversion(newPasswordTxt.getText().toString());
            UtilMethods.changePassword(getContext(), oldPwd, newPwd, new AuthenticationCallback() {
                @Override
                public void onAuthenticationSuccess() {
                    dialog.dismiss();
                    alertDialog.dismiss();
                    settingsPreferences.clearSharedPreferences();
                    final Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    Objects.requireNonNull(getActivity()).finish();
                }

                @Override
                public void onAuthenticationFail(String message) {
                    dialog.dismiss();
                    errorTxt.setText(message);
                }
            });
        });
        cancelBtn.setOnClickListener(v -> alertDialog.dismiss());
    }
}
