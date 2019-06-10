package com.bosch.pai.ipsadmin.retail.pmadminlib.training.config;

import android.support.annotation.NonNull;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjn8kor on 1/8/2018.
 */

public class TrainingConfig {

    public TrainingConfig() {
        //default constuctor
    }

    @NonNull
    public List<BearingConfiguration.SensorType> getWifiSensorTypes() {
        final List<BearingConfiguration.SensorType> sensorTypes = new ArrayList<>();

        sensorTypes.add(BearingConfiguration.SensorType.ST_WIFI);
        //sensorTypes.add(BearingConfiguration.SensorType.ST_IMU);
        //sensorTypes.add(BearingConfiguration.SensorType.ST_MAGNETO);


        return sensorTypes;
    }

    @NonNull
    public List<BearingConfiguration.SensorType> getBleSensorTypes() {
        final List<BearingConfiguration.SensorType> sensorTypes = new ArrayList<>();

        sensorTypes.add(BearingConfiguration.SensorType.ST_BLE);

        return sensorTypes;
    }


}
