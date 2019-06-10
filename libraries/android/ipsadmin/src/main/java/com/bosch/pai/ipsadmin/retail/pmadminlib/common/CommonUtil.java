package com.bosch.pai.ipsadmin.retail.pmadminlib.common;

import com.bosch.pai.ipsadmin.comms.model.ResponseObject;

public class CommonUtil {


    private static final String DEV_SERVER_URL = "IEROPRODVM01LB-7db38d6779d36f25.elb.ap-south-1.amazonaws.com";
    private static final String PROD_SERVER_URL = "prod1.bosch-iero.com";

    private static final String BEARING_URL = "/gatewayService/ipsbearing/";
    private static final String CONFIGURATION_URL = "/gatewayService/ipsconfiguration/";
    private static final String ANALYTICS_URL = "/gatewayService/ipsanalytics/";
    private static final String PHILIPSELIFY_URL = "/gatewayService/retailproximityphilipslifi/";

    private CommonUtil() {
        //default
    }

    public static String getServerURL() {
        return PROD_SERVER_URL;
    }

    public static String getBearingServerEndPoint() {
        return BEARING_URL;
    }

    public static String getProximityConfigurationEndPoint() {
        return CONFIGURATION_URL;
    }

    public static String getAnalyticsEndPoint() {
        return ANALYTICS_URL;
    }

    public static boolean isResponseValid(ResponseObject responseObject) {
        if (responseObject != null) {
            final int httpStatus = responseObject.getStatusCode();
            if ((httpStatus / 100 == 2) && responseObject.getResponseBody() != null) {
                return true;
            }
        }
        return false;
    }

    public static String getErrorMessageFromResponse(ResponseObject responseObject) {
        StringBuilder errorMessage = new StringBuilder("Error from server. ");
        if (responseObject != null && responseObject.getResponseBody() != null) {
            errorMessage.append(responseObject.getResponseBody().toString());
        }
        return errorMessage.toString();
    }


    public static String getPhilipseEndPoint() {
        return PHILIPSELIFY_URL;
    }
}
