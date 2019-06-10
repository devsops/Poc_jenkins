package com.bosch.pai.bearing.benchmark.bearinglogger.profiling;

import android.content.Context;
import android.os.BatteryManager;
import android.support.annotation.NonNull;

import com.bosch.pai.bearing.logger.Logger;
import com.bosch.pai.bearing.logger.LoggerUtil;

import org.apache.commons.lang3.time.StopWatch;

import java.io.File;

/**
 * The type Resource profiler.
 */
public class ResourceProfiler {

    private final LoggerUtil loggerUtil;
    private static final String DEVICE_PERFORMANCE_FILE_NAME = "DevicePerformance";
    private final File file;
    private static StopWatch stopWatch;
    private static boolean enableProfiling = false;


    /**
     * Instantiates a new Resource profiler.
     */
    public ResourceProfiler() {

        this.loggerUtil = new LoggerUtil();

        final String filePath = Logger.LOG_REPORT_FOLDER +
                DEVICE_PERFORMANCE_FILE_NAME +
                loggerUtil.getDateStamp() + Logger.Extension.CSV;
        this.file = new File(filePath);

    }

    /**
     * Enable disable profiling.
     *
     * @param enable the enable
     */
    public static void enableDisableProfiling(boolean enable) {
        enableProfiling = enable;
        stopWatch = new StopWatch();
        stopWatch.start();

    }

    /**
     * Write device info.
     *
     * @param context the context
     */
    public void writeDeviceInfo(Context context) {

        if (enableProfiling) {
            final String deviceInfo = loggerUtil.getLocalTime() + ','
                    + captureCallersMethodName() + ','
                    + captureTimeStamp() + ','
                    + getBatteryStatus(context) + "\n";
            loggerUtil.writeHeader(file, new String[]{"POINT_OF_CAPTURE", "TIME_ELAPSED", "BATTERY_PROPERTY_CAPACITY", "BATTERY_PROPERTY_CHARGE_COUNTER",
                    "BATTERY_PROPERTY_CURRENT_AVERAGE", "BATTERY_PROPERTY_CURRENT_NOW", "BATTERY_PROPERTY_ENERGY_COUNTER"});
            loggerUtil.writeToFile(file, deviceInfo, true);
        }
    }

    /**
     * Gets battery status.
     *
     * @param context the context
     * @return the battery status
     */
    private String getBatteryStatus(Context context) {
        if (context == null) {
            return null;
        }
        StringBuilder batteryStatus = new StringBuilder();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            if(batteryManager == null)
                return "Unable to get Battery info";
            final int batteryCapacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            final int batteryChargeCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            final int batteryAverageCurrent = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
            final int batteryCurrentNow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
            final int batteryEnergyCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);

            batteryStatus.append(batteryCapacity).append(",")
                    .append(batteryChargeCounter).append(",")
                    .append(batteryAverageCurrent).append(",")
                    .append(+batteryCurrentNow).append(",")
                    .append(batteryEnergyCounter);
        } else {
            batteryStatus.append("Unable to get Battery info for Anrdroid versions less than Lollipop (API21)");
        }
        return batteryStatus.toString();
    }


    private static String captureCallersMethodName() {

        int position = -1;
        final Thread thread = Thread.currentThread();
        final StringBuilder stringBuilder = new StringBuilder();
        final StackTraceElement[] stack = thread.getStackTrace();

        for (int i = 0; i < stack.length; i++) {
            if (stack[i].getMethodName().contains("writeDeviceInfo")) {
                position++;
            }
        }
        if (stack[position] != null && position != -1) {
            stringBuilder.append(" Class :" + stack[position].getFileName())
                    .append(" Method :" + stack[position].getMethodName())
                    .append(" Line :" + stack[position].getLineNumber());

        }

        return stringBuilder.toString();
    }


    private static String captureTimeStamp() {

        final float watchTime = stopWatch.getTime();
        final String watchTimeString = String.valueOf(watchTime);


        return watchTimeString;
    }


}
