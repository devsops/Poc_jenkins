package com.bosch.pai.indoordetection;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bosch.pai.IeroIPSPlatform;
import com.bosch.pai.IeroIPSPlatformListener;
import com.bosch.pai.ipswrapper.Config;
import com.bosch.pai.ipswrapper.IPSPlatformFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements IeroIPSPlatformListener {

    private DetectionAdapter detectionAdapter;
    private LinkedList<String> detectionItemList;
    private IeroIPSPlatform ieroIPSPlatform;
    private TextView siteTV;
    private RadioGroup radioGroup;
    private Map<Config.Key, Object> keyObjectMap = new HashMap<>();
    private String companyId = "TESTIPS";
    private String userId = "iot_client_user";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detectionItemList = new LinkedList<>();
        siteTV = findViewById(R.id.siteTV);
        radioGroup = findViewById(R.id.radioGroup);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        detectionAdapter = new DetectionAdapter(detectionItemList);
        recyclerView.setAdapter(detectionAdapter);
    }

    public void start(View view) {
        keyObjectMap.clear();
        final int rd = radioGroup.getCheckedRadioButtonId();
        final Config.SensorType sensorType;
        switch (rd) {
            case R.id.wifiRD:
                sensorType = Config.SensorType.WIFI;
                break;
            case R.id.bleRD:
                sensorType = Config.SensorType.BLE;
                break;
            case R.id.bothRD:
                sensorType = Config.SensorType.WIFI_BLE;
                break;
            default:
                sensorType = Config.SensorType.WIFI_BLE;
        }
        final String companyID = sharedPreferences.getString(getString(R.string.teamid), companyId);
        final String username = sharedPreferences.getString(getString(R.string.username), userId);
        try {
            if (!companyID.isEmpty() || !username.isEmpty()) {
                ieroIPSPlatform = IPSPlatformFactory.getInstance(this, IPSPlatformFactory.PlatformType.IERO_IPS_PLATFORM, MainActivity.this);
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Exception : " + e, Toast.LENGTH_SHORT).show();
        }
        if (ieroIPSPlatform != null) {
            keyObjectMap.put(Config.Key.SENSOR_TYPE, sensorType);
            keyObjectMap.put(Config.Key.COMPANY_ID, companyID);
            keyObjectMap.put(Config.Key.UNIQUE_CLIENT_ID, username);
            ieroIPSPlatform.register(keyObjectMap);
        } else {
            Toast.makeText(this, "enter companyID and userId", Toast.LENGTH_SHORT).show();
        }
    }

    public void stop(View view) {
        if (ieroIPSPlatform != null) {
            ieroIPSPlatform.unregister(keyObjectMap);
            detectionItemList.clear();
            detectionAdapter.notifyDataSetChanged();
            siteTV.setText("");
            ieroIPSPlatform = null;
        } else {
            Toast.makeText(this, "enter teamid and userId", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.user_name) {
            enterUserName();
        }
        return super.onOptionsItemSelected(item);
    }

    private void enterUserName() {
        final Dialog urlDialog = new Dialog(MainActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        urlDialog.setCanceledOnTouchOutside(false);
        urlDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        urlDialog.setContentView(R.layout.credentials_layout);
        final EditText teamId = urlDialog.findViewById(R.id.teamid);
        final EditText username = urlDialog.findViewById(R.id.userId);
        final Button ok = urlDialog.findViewById(R.id.okay);
        final Button cancel = urlDialog.findViewById(R.id.cancel);
        final String t = sharedPreferences.getString(getString(R.string.teamid), companyId);
        ;
        final String u = sharedPreferences.getString(getString(R.string.username), userId);
        ;
        teamId.setText(t + "");
        username.setText(u + "");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String teamidtext = teamId.getText().toString().trim();
                final String usernametext = username.getText().toString().trim();
                if (usernametext.isEmpty() && teamidtext.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter all the fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.username), usernametext);
                editor.putString(getString(R.string.teamid), teamidtext);
                editor.apply();
                if (urlDialog != null && urlDialog.isShowing()) {
                    urlDialog.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (urlDialog != null && urlDialog.isShowing()) {
                    urlDialog.dismiss();
                }
            }
        });
        urlDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stop(null);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop(null);
    }

    @Override
    public void onSuccess(String message) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Success :: " + message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onFailure(String failureMessage) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failure :: " + failureMessage, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onSiteDetected(String siteName) {
        runOnUiThread(() -> siteTV.setText(siteName));
    }

    @Override
    public void onLocationDetected(Map<String, Double> locationUpdateMap) {
        runOnUiThread(() -> {
            if (locationUpdateMap != null && !locationUpdateMap.isEmpty()) {
                StringBuilder text = new StringBuilder();
                for (Map.Entry<String, Double> entry : locationUpdateMap.entrySet()) {
                    text.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
                }
                Calendar cal = Calendar.getInstance();
                TimeZone tz = cal.getTimeZone();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
                sdf.setTimeZone(tz);
                String localTime = sdf.format(new Date());
                text.append(localTime).append("\n");
                detectionItemList.addFirst(text.toString());
                detectionAdapter.notifyDataSetChanged();
            }
        });
    }

}
