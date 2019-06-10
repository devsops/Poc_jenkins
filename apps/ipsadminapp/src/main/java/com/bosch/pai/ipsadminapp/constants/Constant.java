package com.bosch.pai.ipsadminapp.constants;

import com.bosch.pai.ipsadmin.retail.pmadminlib.common.CommonUtil;

public class Constant {

    private static final String HTTP = "http://";

    private static final int RECEIVER_DELAY = 5000;

    private static final int MINIMUM_LOCATIONS_COUNT = 3;

    private static final String SERVER_URL = "https://" + CommonUtil.getServerURL();// Dev server

    private Constant() {
        // default constructor
    }

    public static String getServerUrl() {
        return SERVER_URL;
    }

    public static int getMinimumLocationsCount() {
        return MINIMUM_LOCATIONS_COUNT;
    }

    public static String getHttp() {
        return HTTP;
    }

    public static int getReceiverDelay() {
        return RECEIVER_DELAY;
    }


}
