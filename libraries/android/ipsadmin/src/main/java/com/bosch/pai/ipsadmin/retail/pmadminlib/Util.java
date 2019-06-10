package com.bosch.pai.ipsadmin.retail.pmadminlib;


import android.content.Context;
import android.util.Log;

import com.bosch.pai.R;
import com.bosch.pai.ipsadmin.comms.model.RequestObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Util {

    private static final String LOG_TAG = Util.class.getSimpleName();

    private static String authCertText;
    private static UserType USER_TYPE = UserType.ROLE_FREE;

    public enum UserType {
        ROLE_FREE,
        ROLE_PAID,
        ROLE_PREMIUM
    }

    private Util() {
        //To hide default constructor as this is a helper class
    }

    public static void setUserType(UserType userType) {
        Util.USER_TYPE = userType;
    }

    public static UserType getUserType() {
        return Util.USER_TYPE;
    }

    public static String getSHA256Conversion(String toBeConvertedString) {
        try {
            if (toBeConvertedString == null || toBeConvertedString.isEmpty()) {
                return toBeConvertedString;
            }
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            return bytesToHex(messageDigest.digest(toBeConvertedString.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            addLogs(LOG_STATUS.ERROR, LOG_TAG, "Error: NoSuchAlgorithmException", e);
        }
        return toBeConvertedString;
    }

    private static String bytesToHex(final byte[] hash) {
        final StringBuilder hexString = new StringBuilder();
        for (byte aHash : hash) {
            String hex = Integer.toHexString(0xff & aHash);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }

    public static void addCertification(RequestObject requestObject, InputStream certificate, String retailProductOfferServerURL) {
        if (retailProductOfferServerURL.contains("https")) {
            requestObject.setCertFileStream(certificate);
        }
    }


    public static enum LOG_STATUS {

        DEBUG,
        INFO,
        ERROR,
        WARNING;

    }

    public static void addLogs(LOG_STATUS logStatus, String tag, String message, Exception e) {

     /*   try {
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
                default:
                    break;
            }
        } catch (Exception ex) {
            Log.e("Util", "Error : " + ex, ex);
        }*/

    }

    private static void loadCertificate(Context context) {
        try {
            final InputStream certStream = context.getResources().openRawResource(R.raw.prodkeystore);
            setCertificate(certStream);
        } catch (Exception e) {
            Log.e(LOG_TAG, "exception while reading prodkeystore crt from raw  " + e.getMessage(), e);
        }
    }

    public static boolean isHttpsURL(String url) {
        return url != null && url.toUpperCase().contains("HTTPS");
    }

    synchronized static public void setCertificate(InputStream certificate) {
        if (certificate != null) {

            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            try {
                while ((len = certificate.read(buffer)) > -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, "Unable to store certificate. ", e);
            }
            authCertText = byteArrayOutputStream.toString();
        } else {
            authCertText = null;
        }

    }

    synchronized static public InputStream getCertificate(Context context) {

        if (authCertText == null) {
            loadCertificate(context);
        }

        if (authCertText != null) {
            return new ByteArrayInputStream(authCertText.getBytes());
        } else {
            return null;
        }
    }

    public static synchronized InputStream getCertificate() {
        if (authCertText != null) {
            return new ByteArrayInputStream(authCertText.getBytes());
        } else {
            return null;
        }
    }
}
