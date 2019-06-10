package com.bosch.pai.detection.config;

import com.bosch.pai.comms.model.ResponseObject;

public class CommonUtil {

    private static final String BEARING_URL = "/gatewayService/ipsbearing/";

    private CommonUtil() {
        //default
    }

    public static String getBearingServerEndPoint() {
        return BEARING_URL;
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

}
