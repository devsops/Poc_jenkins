package com.bosch.pai.ipsadminapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.bosch.pai.ipsadminapp.IPSAdminApplication;
import com.bosch.pai.ipsadminapp.constants.Constant;

public class InternetConnectionReceiver extends BroadcastReceiver {

    private static InternetConnectionReceiverListner internetConnectionReceiverListner;

    public static void setInternetConnectionReceiverListner(InternetConnectionReceiverListner internetConnectionReceiverListner) {
        InternetConnectionReceiver.internetConnectionReceiverListner = internetConnectionReceiverListner;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        new Handler().postDelayed(()-> {
                final ConnectivityManager connectivityManager = (ConnectivityManager)
                        context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    final boolean isConnected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
                    if (internetConnectionReceiverListner != null) {
                        internetConnectionReceiverListner.onNetworkConnectionChanged(isConnected);
                    }
                }
        }, Constant.getReceiverDelay());
    }

    public static boolean isInternetConnected() {
        final ConnectivityManager
                cm = (ConnectivityManager) IPSAdminApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isAvailable()
                    && activeNetwork.isConnected();
        }
        return false;
    }


    public interface InternetConnectionReceiverListner {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
