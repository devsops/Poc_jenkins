package com.bosch.pai.ipsadminapp;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.bosch.pai.ipsadminapp.crashhandler.IPSAdminCrashHandler;
import com.bosch.pai.ipsadminapp.receivers.BluetoothStatusReceiver;
import com.bosch.pai.ipsadminapp.receivers.GPSConnectionReceiver;
import com.bosch.pai.ipsadminapp.receivers.InternetConnectionReceiver;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;


@ReportsCrashes(mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text,
        reportSenderFactoryClasses = {IPSAdminCrashHandler.ACRAReportSenderFactory.class}
)
public class IPSAdminApplication extends MultiDexApplication {

    private static IPSAdminApplication ipsAdminApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        setIpsAdminApplication(this);
    }

    private static void setIpsAdminApplication(IPSAdminApplication ipsAdminApplication) {
        IPSAdminApplication.ipsAdminApplication = ipsAdminApplication;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        ACRA.init(this);
    }

    public static synchronized IPSAdminApplication getInstance() {
        return ipsAdminApplication;
    }

    public void setInternetConnectivityListener(
            InternetConnectionReceiver.InternetConnectionReceiverListner internetConnectivityListener) {
        InternetConnectionReceiver.setInternetConnectionReceiverListner(internetConnectivityListener);
    }


    public void setGPSReceiverReceiverListner(GPSConnectionReceiver.IGPSReceiverReceiverListner listner) {
        GPSConnectionReceiver.setIgpsReceiverReceiverListner(listner);
    }

    public void setBluetoothReceiverListner(BluetoothStatusReceiver.IBluetoothStatusListener listner) {
        BluetoothStatusReceiver.setiBluetoothStatusListener(listner);
    }
}
