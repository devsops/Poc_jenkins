package com.bosch.pai.util;

import android.content.Context;
import android.util.Log;

import com.bosch.pai.comms.CommsListener;
import com.bosch.pai.comms.CommsManager;
import com.bosch.pai.comms.model.RequestObject;
import com.bosch.pai.comms.model.ResponseObject;
import com.bosch.pai.detection.Util;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Crash uploader.
 */
public final class CrashUploader {

    private static final String LOG_TAG = CrashUploader.class.getName();
    private static final List<File> FILES = new ArrayList<>();

    private static final String UPLOAD_CRASH_END_POINT = "/uploadCrashReports/";
    private static final String CRASH_REPORTS_DIR = "/IPSSDK/crashreports";

    private static final CommsListener LISTENER = new CommsListener() {
        @Override
        public void onResponse(ResponseObject responseObject) {
            if (responseObject.getStatusCode() / 100 == 2) {
                final boolean isDeleted = FILES.get(0).delete();
                Log.d(LOG_TAG, "File deleted after upload: " + isDeleted);
                if (isDeleted)
                    FILES.remove(0);
                uploadCrashReport();
            } else {
                Log.e(LOG_TAG, "Failed uploading error file");
            }
        }

        @Override
        public void onFailure(int statusCode, String errMessage) {
            Log.e(LOG_TAG, "Failed uploading error file");
        }
    };

    /**
     * Upload crash reports.
     *
     * @param context the context
     */
    public static void uploadCrashReports(Context context) {
        FILES.clear();
        FILES.addAll(getFileList(context));
        if (!FILES.isEmpty()) {
            uploadCrashReport();
        }
    }

    private static List<File> getFileList(Context context) {
        try {

            final String directoryPath = context.getFilesDir().getAbsolutePath() + CRASH_REPORTS_DIR;
            final File crashReportDirectory = new File(directoryPath);
            if (crashReportDirectory.exists()) {
                final File[] files = crashReportDirectory.listFiles();
                return Arrays.asList(files);
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    // Called recursively from comms listener success response callback, for uploading file(s) one after the other
    private static void uploadCrashReport() {
        if (!FILES.isEmpty()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RequestObject requestObject = new RequestObject(RequestObject.RequestType.MULTIPART_POST,
                            Constant.getServerUrl() + "/gatewayService/ipsfilehandler", UPLOAD_CRASH_END_POINT);

                    final Map<String, String> params = new HashMap<>();

                    final Calendar c = Calendar.getInstance();
                    final int year = c.get(Calendar.YEAR);
                    final int month = c.get(Calendar.MONTH) + 1;
                    final int date = c.get(Calendar.DAY_OF_MONTH);

                    params.put("year", String.valueOf(year));
                    params.put("month", String.valueOf(month));
                    params.put("date", String.valueOf(date));
                    params.put("file", "file:" + FILES.get(0).getAbsolutePath());

                    requestObject.setQueryParams(params);
                    requestObject.setMultipartFile(FILES.get(0));
                    final InputStream certificate = Util.getCertificate();
                    if (certificate != null) {
                        requestObject.setCertFileStream(certificate);
                        requestObject.setNonBezirkRequest(true);
                    }
                    Log.i(LOG_TAG, "requestObject: " + requestObject.toString());
                    CommsManager.getInstance().processRequest(requestObject, LISTENER);
                }
            }).start();
        }
    }
}
