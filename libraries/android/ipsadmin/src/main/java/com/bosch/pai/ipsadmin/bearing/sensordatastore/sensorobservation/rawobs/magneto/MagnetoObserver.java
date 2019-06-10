package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.magneto;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.queryhandler.SensorObservationHandler;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.RawSnapshotConvertor;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs.ResourceDataManager;

import java.text.DecimalFormat;
import java.util.List;

/**
 * The type Magneto observer.
 */
public class MagnetoObserver implements SensorEventListener {

    private final ResourceDataManager resourceDataManager;
    private final double alpha = 0.8;
    private double gravity[] = new double[3];
    private float magnetic[] = new float[3];
    private double[] magnetometerDouble;
    private final Context context;
    private SensorManager sensorManager;
    private Sensor mSensor, aSensor, stepSensor;
    private int sensorCounter = 0;
    private double magnetoX = 0;
    private double magnetoY = 0;
    private double magnetoZ = 0;
    private SensorObservationHandler sensorObservationHandler;


    /**
     * Instantiates a new Magneto observer.
     *
     * @param context             the context
     * @param resourceDataManager the resource data manager
     */
    public MagnetoObserver(Context context, ResourceDataManager resourceDataManager) {
        this.resourceDataManager = resourceDataManager;
        this.context = context;
    }


    /**
     * Sets up magneto.
     *
     * @param sensorObservationHandler the sensor observation handler
     * @return the up magneto
     */
    public boolean setUpMagneto(SensorObservationHandler sensorObservationHandler) {

        this.sensorObservationHandler = sensorObservationHandler;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) {
            return false;
        }

        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (mSensor == null || aSensor == null || stepSensor == null) {
            return false;
        }

        return true;
    }

    /**
     * Start scan.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void startScan() {

        final int magnetoScanTimeOut = Double.valueOf(ConfigurationSettings.getConfiguration().getSensorPreferences().getProperty(Property.Sensor.MAGNETO_SCAN_TIMEOUT).toString()).intValue();
        if (sensorObservationHandler != null) {
            sensorObservationHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                    LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, "MagnetoObserver", "stop: ");
                }
            }, magnetoScanTimeOut);
        }
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, "MagnetoObserver", "start: ");
        sensorManager.registerListener(MagnetoObserver.this, mSensor, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(MagnetoObserver.this, aSensor, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(MagnetoObserver.this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }


    /**
     * Stop scan.
     */
    public void stopScan() {

        sensorManager.unregisterListener(MagnetoObserver.this, mSensor);
        sensorManager.unregisterListener(MagnetoObserver.this, aSensor);
        sensorManager.unregisterListener(MagnetoObserver.this, stepSensor);

        //Compute the average of the responses based on the counter responses and send out the observation on stop scan.
        double resMagnetoX = magnetoX / sensorCounter;
        if (Double.isNaN(resMagnetoX)) {
            resMagnetoX = 0;
        }
        double resMagnetoY = magnetoY / sensorCounter;
        if (Double.isNaN(resMagnetoY)) {
            resMagnetoY = 0;
        }
        double resMagnetoZ = magnetoZ / sensorCounter;
        if (Double.isNaN(resMagnetoZ)) {
            resMagnetoZ = 0;
        }

        final List<SnapshotObservation> snapshotObservationMagneto = RawSnapshotConvertor.
                createSnapshotObservationMagneto(resMagnetoX, resMagnetoY, resMagnetoZ);
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, "MagnetoScan", "stopScan: " + "X:" + resMagnetoX + "Y:" + resMagnetoY + "Z:" + resMagnetoZ);
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, "MagnetoScan", "stopScan:" + "XTotal:" + magnetoX + "YTotal:" + magnetoY + "ZTotal :" + magnetoZ + "counter: " + sensorCounter);
        resourceDataManager.onResponseReceived(snapshotObservationMagneto);
        magnetoX = 0;
        magnetoY = 0;
        magnetoZ = 0;
        sensorCounter = 0;

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
        } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

            sensorCounter = sensorCounter + 1;

            magnetic[0] = event.values[0];
            magnetic[1] = event.values[1];
            magnetic[2] = event.values[2];

            float[] R = new float[9];
            float[] I = new float[9];
            SensorManager.getRotationMatrix(R, I, doubleToFloat(gravity), magnetic);
            double[] A_D = floatToDouble(event.values.clone());
            double[] A_W = new double[3];
            A_W[0] = R[0] * A_D[0] + R[1] * A_D[1] + R[2] * A_D[2];
            A_W[1] = R[3] * A_D[0] + R[4] * A_D[1] + R[5] * A_D[2];
            A_W[2] = R[6] * A_D[0] + R[7] * A_D[1] + R[8] * A_D[2];
            magnetometerDouble = new double[3];
            DecimalFormat df = new DecimalFormat("#");
            df.setMaximumFractionDigits(8);
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, "Only X", df.format(A_W[0]));
            magnetometerDouble[0] = Double.valueOf(df.format(A_W[0]));
            magnetometerDouble[1] = A_W[1];
            magnetometerDouble[2] = A_W[2];

            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, "MagnetoScan", "stopScan: actualVal: " + " X:" + magnetometerDouble[0] + " Y:" + magnetometerDouble[1] + " Z:" + magnetometerDouble[2]);

             /*Assign the response from magnetometer to averaging the responses*/
            magnetoX += (magnetometerDouble[0]);
            magnetoY += (magnetometerDouble[1]);
            magnetoZ += (magnetometerDouble[2]);

            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, "MagnetoScan", "stopScan: actualValTotal: " + " X:" + magnetoX + " Y:" + magnetoY + " Z:" + magnetoZ);
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, "MagnetoScan", "stopScan: actualValTotalCount: " + sensorCounter);


        }
    }

    private double[] floatToDouble(float[] floats) {
        final double[] doubles = new double[floats.length];
        for (int i = 0; i < floats.length; i++)
            doubles[i] = floats[i];
        return doubles;
    }

    private float[] doubleToFloat(double[] doubles) {
        float[] floats = new float[doubles.length];
        for (int i = 0; i < doubles.length; i++)
            floats[i] = (float) doubles[i];
        return floats;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
