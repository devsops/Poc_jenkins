package com.bosch.pai.ipsadmin.bearing.benchmark.crashreporter;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.config.ACRAConfiguration;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.acra.sender.ReportSenderFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * The type Acra report sender.
 */
public class ACRAReportSender implements ReportSender {
    private final String TAG = ACRAReportSender.class.getSimpleName();

    /**
     * Instantiates a new Acra report sender.
     */
    public ACRAReportSender() {
    }

    @Override
    public void send(@NonNull Context context, @NonNull CrashReportData errorContent) throws ReportSenderException {
        /*FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            String timeLog = new SimpleDateFormat("yyyyMMdd_HHmm").format(Calendar.getInstance().getTime());
            String dataDirectoryPath = CRASH_REPORTS_FOLDER + context.getPackageName() + File.separator;
            String logFilePath = dataDirectoryPath + "CrashReport_" + timeLog + ".txt";

            File logFile = new File(logFilePath);
            File dataDir = new File(dataDirectoryPath);
            if (!dataDir.exists()) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "Data Directory didn't exist, created: " + dataDir.mkdirs());
            }
            if (!logFile.exists()) {
                System.out.println(logFile.getCanonicalPath() + logFile.createNewFile());
            }
            // This will output the full path where the file will be written to...

            fileWriter = new FileWriter(logFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("ANDROID_VERSION : \t" + errorContent.getProperty(ReportField.ANDROID_VERSION) + "\n");
            bufferedWriter.append("PHONE_MODEL : \t").append(errorContent.getProperty(ReportField.PHONE_MODEL)).append("\n\n\n");
            bufferedWriter.append(errorContent.getProperty(ReportField.STACK_TRACE));
        } catch (IOException e) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "ERROR :", e);
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "ERROR :", e);
            }
        }*/

    }

    /**
     * The type Acra report sender factory.
     */
    public static class ACRAReportSenderFactory implements ReportSenderFactory {

        @NonNull
        public ReportSender create(@NonNull Context context, @NonNull ACRAConfiguration config) {
            return new ACRAReportSender();
        }
    }
}
