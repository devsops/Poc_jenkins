package com.bosch.pai.ipsadmin.bearing.benchmark;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;


/**
 * The type Log and toast util.
 */
public class LogAndToastUtil {

    private static final String LOG_TAG = LogAndToastUtil.class.getSimpleName();

    private LogAndToastUtil() {
        //To hide default constructor as this is a helper class
    }

    /**
     * Show toasts with message.
     *
     * @param activity the activity
     * @param message  the message
     * @return the boolean
     */
    public static boolean showToastsWithMessage(final Activity activity, final String message) {
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, " error while displaying Toast : " + e.getMessage(), e);
            return false;
        }
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
        try {
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
        }
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
        try {
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
        }
    }
}
