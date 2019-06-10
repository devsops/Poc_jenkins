package com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.imu;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.SensorUtil;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.queryhandler.SensorObservationHandler;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.RawSnapshotConvertor;
import com.bosch.pai.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The type Imu observer.
 */
public class IMUObserver implements SensorEventListener {

    private final ResourceDataManager resourceDataManager;
    private final Context context;
    private SensorManager sManager;
    private Sensor stepSensor;
    private SensorObservationHandler sensorObservationHandler;
    private Timer imuSendOutTimer;

    private boolean isStepTaken = false;
    private boolean scanStarted = false;

    /**
     * Instantiates a new Imu observer.
     *
     * @param context             the context
     * @param resourceDataManager the resource data manager
     */
    public IMUObserver(Context context, ResourceDataManager resourceDataManager) {
        this.resourceDataManager = resourceDataManager;
        this.context = context;
    }

    /**
     * Sets up imu.
     *
     * @param sensorObservationHandler the sensor observation handler
     * @return the up imu
     */
    public boolean setUpIMU(SensorObservationHandler sensorObservationHandler) {

        this.sensorObservationHandler = sensorObservationHandler;

        sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sManager == null) {
            return false;
        }
        stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepSensor == null) {
            return false;
        }

        return true;
    }


    /**
     * Start scan.
     */
    public void startScan() {
        if(SensorUtil.isShutDown()) {
            stopScan();
            return;
        }
        if (!scanStarted) {
            final int IMUScanTimeOut = Double.valueOf(ConfigurationSettings.getConfiguration().getSensorPreferences().getProperty(Property.Sensor.IMU_SCAN_TIMEOUT).toString()).intValue();

            imuSendOutTimer = new Timer();
            imuSendOutTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    int imuCount = 0;
                    if (isStepTaken) {
                        imuCount = 1;
                    }
                    final List<SnapshotObservation> snapshotObservationIMU = RawSnapshotConvertor.createSnapshotObservationIMU(imuCount);
                    resourceDataManager.onResponseReceived(snapshotObservationIMU);
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, "IMUObserver", "STEP DETECTED : "+ isStepTaken);
                    isStepTaken = false;


                }
            }, 0, IMUScanTimeOut);

            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, "MagnetoObserver", "start: ");
            isStepTaken = false;
            sManager.registerListener(IMUObserver.this, stepSensor, SensorManager.SENSOR_DELAY_UI);
            scanStarted = true;

        }


    }


    /**
     * Stop scan.
     */
    public void stopScan() {
        imuSendOutTimer.cancel();
        sManager.unregisterListener(IMUObserver.this, stepSensor);
        scanStarted = false;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {

            float values = sensorEvent.values[0];
            if (values == 1) {
                isStepTaken = true;
            }
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, "IMUObserver", "onSensorChanged: " + isStepTaken);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
