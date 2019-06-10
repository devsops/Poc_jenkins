package com.bosch.pai.ipswrapper;

public class Config {

    private Config() {
        //Needed
    }

    public enum Key {
        COMPANY_ID,
        UNIQUE_CLIENT_ID,
        SENSOR_TYPE,
        FLAVOR
    }

    public enum SensorType {
        WIFI,       //FingerPrinting
        BLE,        //Threshold
        WIFI_BLE    //Both Fingerprinting and Threshold
    }
}
