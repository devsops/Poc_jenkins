package com.bosch.pai.ipsadminapp.crashhandler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.config.ACRAConfiguration;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class IPSAdminCrashHandler implements ReportSender {
    private static final Logger logger = LoggerFactory.getLogger(IPSAdminCrashHandler.class);
    private static final String LOGTAG = IPSAdminCrashHandler.class.getSimpleName();

    public IPSAdminCrashHandler() {
        //sonar
    }

    @Override
    public void send(@NonNull Context context, @NonNull CrashReportData errorContent) {
        BufferedWriter writer = null;
        FileWriter fileWriter = null;
        try {
            //create a temporary file
            String timeLog = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            File logFile = new File(context.getFilesDir().getAbsolutePath() + "/IPSADMIN/crashreports/ACRALogFile_" + timeLog + ".txt");
            File dataDir = new File(context.getFilesDir().getAbsolutePath() + "/IPSADMIN/crashreports");
            if (!dataDir.exists()) {
                logger.debug("Data Directory didn't exist, created: {}", dataDir.mkdirs());
            }
            if (!logFile.exists()) {
                logger.debug(logFile.getCanonicalPath() + logFile.createNewFile());
            }
            // This will output the full path where the file will be written to...

            fileWriter = new FileWriter(logFile);
            writer = new BufferedWriter(fileWriter);
            String androidVersion = "androidVersion : \t" + errorContent.getProperty(ReportField.ANDROID_VERSION) + "\n";
            String phoneModel = "phoneModel : \t" + errorContent.getProperty(ReportField.PHONE_MODEL) + "\n\n\n";
            String stackTrace = errorContent.getProperty(ReportField.STACK_TRACE);
            String log = androidVersion + phoneModel + stackTrace;
            writer.write(log);
        } catch (Exception e) {
            logger.error("Error in generating and sending crash report.", e);
            Log.e(LOGTAG, "Error in generating and sending crash report" + e.getMessage(), e);
        } finally {
            try {
                // Close the writer regardless of what happens...
                if (writer != null) {
                    writer.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException ex) {

                logger.error("Exception in closing file. ", ex);
                Log.e(LOGTAG, "Exception in closing file." + ex.getMessage(), ex);
            }
        }

    }

    public static class ACRAReportSenderFactory implements ReportSenderFactory {

        @NonNull
        @Override
        public ReportSender create(@NonNull Context context, @NonNull ACRAConfiguration config) {
            return new IPSAdminCrashHandler();
        }
    }
}
