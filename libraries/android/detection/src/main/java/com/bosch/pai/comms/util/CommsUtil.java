package com.bosch.pai.comms.util;


import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * The type Comms util.
 */
public class CommsUtil {

    private CommsUtil() {
        /// Needed empty private constructor
    }

    /**
     * The enum Log status.
     */
    public enum LOG_STATUS {
        /**
         * Debug log status.
         */
        DEBUG,
        /**
         * Info log status.
         */
        INFO,
        /**
         * Error log status.
         */
        ERROR,
        /**
         * Verbose log status.
         */
        VERBOSE,
        /**
         * Warning log status.
         */
        WARNING;
    }

    /**
     * Add logs.
     *
     * @param logStatus the log status
     * @param tag       the tag
     * @param message   the message
     */
    public static void addLogs(LOG_STATUS logStatus, String tag, String message) {
        /*try {
            switch (logStatus) {
                case DEBUG:
                    Log.d(tag, message);
                    break;
                case INFO:
                    Log.i(tag, message);
                    break;
                case ERROR:
                    Log.e(tag, message);
                    break;
                case WARNING:
                    Log.w(tag, message);
                    break;
                case VERBOSE:
                    Log.v(tag, message);
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            // Error not logged here
        }*/
    }

    /**
     * Add logs.
     *
     * @param logStatus the log status
     * @param tag       the tag
     * @param message   the message
     * @param e         the e
     */
    public static void addLogs(LOG_STATUS logStatus, String tag, String message, Exception e) {
        /*try {
            switch (logStatus) {
                case DEBUG:
                    Log.d(tag, message);
                    break;
                case INFO:
                    Log.i(tag, message);
                    break;
                case ERROR:
                    Log.e(tag, message, e);
                    break;
                case WARNING:
                    Log.w(tag, message);
                    break;
                case VERBOSE:
                    Log.v(tag, message);
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            // Error not logged here
        }*/
    }

    /**
     * Read baseurl from user url string.
     *
     * @param baseURL the base url
     * @return the string
     */
    public static String readBASEURLFromUserURL(String baseURL) {
        if (baseURL.contains("gatewayService/"))
            return baseURL.substring(0, baseURL.indexOf("/gatewayService/"));
        else if(baseURL.contains("registration/"))
            return baseURL.substring(0, baseURL.indexOf("/registration/"));
        return baseURL;
    }

    /**
     * Convert crt stream to string string.
     *
     * @param certificateStream the certificate stream
     * @return the string
     */
    public static String convertCrtStreamToString(InputStream certificateStream) {
        if(certificateStream == null)
            return null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = certificateStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, "", "Failed to convert IOStream to String", e);
        }
        return byteArrayOutputStream.toString();
    }

    /**
     * Gets stream crt string.
     *
     * @param certificateString the certificate string
     * @return the stream crt string
     */
    public static InputStream getStreamCrtString(String certificateString) {
        if(certificateString != null) {
            return new ByteArrayInputStream(certificateString.getBytes());
        }
        return null;
    }
}
