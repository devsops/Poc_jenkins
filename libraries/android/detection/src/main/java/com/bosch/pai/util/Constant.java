package com.bosch.pai.util;

public final class Constant {

    private static final String PROD_SERVER_URL = "https://prod1.bosch-iero.com";// Prod server
    private static final String DEV_SERVER_URL = "https://IEROPRODVM01LB-7db38d6779d36f25.elb.ap-south-1.amazonaws.com";// Dev server

    public static String getServerUrl() {
        return PROD_SERVER_URL; // Change this as per testing URL need
    }
}
