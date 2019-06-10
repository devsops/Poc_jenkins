package com.bosch.pai.ipsadminapp.receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothStatusReceiver extends BroadcastReceiver {

    private static IBluetoothStatusListener iBluetoothStatusListener;

    public static void setiBluetoothStatusListener(IBluetoothStatusListener iBluetoothStatusListener) {
        BluetoothStatusReceiver.iBluetoothStatusListener = iBluetoothStatusListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        isBluetoothConnected();
    }

    public static void isBluetoothConnected() {
        if (iBluetoothStatusListener!=null) {
            final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null) {
                final boolean isEnabled = bluetoothAdapter.isEnabled();
                iBluetoothStatusListener.onBluetoothConnectionChanged(isEnabled);
            } else {
                iBluetoothStatusListener.deviceNotSupported();
            }
        }
    }

    public interface IBluetoothStatusListener {
        void onBluetoothConnectionChanged(boolean isConnected);

        void deviceNotSupported();
    }
}
