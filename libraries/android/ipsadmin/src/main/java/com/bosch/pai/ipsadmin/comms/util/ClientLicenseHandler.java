//package com.bosch.pai.ipsadmin.comms.util;
//
//import android.util.Log;
//
//import com.bosch.pai.ipsadmin.api.licenseX.common.LicenseResponse;
//import com.bosch.pai.ipsadmin.api.licenseX.common.LicenseStatus;
//import com.bosch.pai.ipsadmin.api.licenseX.exceptions.LicenseException;
//import com.bosch.pai.ipsadmin.api.licenseX.handler.LicenseAPIHandler;
//import com.bosch.pai.ipsadmin.comms.CommsListener;
//import com.bosch.pai.ipsadmin.comms.CommsManager;
//import com.bosch.pai.ipsadmin.comms.model.RequestObject;
//import com.bosch.pai.ipsadmin.comms.model.ResponseObject;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicReference;
//
//public class ClientLicenseHandler extends LicenseAPIHandler {
//
//    private static final String BASE_URL = CommsManager.getLicenseServiceBaseUrl();
//    private static final String BOSCH_LICENSE_ACTIVATION_URI_TEMPLATE = "/licenseservice/products/%s/activate";
//    private static final String BOSCH_LICENSE_DEACTIVATION_URI_TEMPLATE = "/licenseservice/products/%s/deactivate";
//    private static final String APPLICATION_JSON_TYPE = "application/json";
//    private static final String LICENSE_ACTIVATION_FAIL_MSG = "Could Not Activate the License !";
//    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
//    private static final String ACCEPT_TYPE_HEADER_NAME = "Accept";
//    private static final String LICENSE_STRING = "{\"licenseString\" : \"";
//    private static final String PRODUCT_ID_STRING = ",\"productId\":\"";
//    private static final String PUBLIC_KEY_STRING = ",\"publicKey\":\"";
//
//    @Override
//    public LicenseStatus activateLicense(String publicKey, String productName, Map<String, String> productLicenseMap) {
//        String activation_url_template = String.format(BOSCH_LICENSE_ACTIVATION_URI_TEMPLATE,productName);
//        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.POST,
//                BASE_URL,
//                activation_url_template);
//        String msgBody = LICENSE_STRING + productLicenseMap.get(productName).trim()+"\""+
//                PRODUCT_ID_STRING + productName + "\"" +PUBLIC_KEY_STRING +publicKey+"\"}";
//
//        HashMap<String,String> headerParams = new HashMap<>();
//        headerParams.put(CONTENT_TYPE_HEADER_NAME,APPLICATION_JSON_TYPE);
//        headerParams.put(ACCEPT_TYPE_HEADER_NAME, APPLICATION_JSON_TYPE);
//
//        requestObject.setMessageBody(msgBody);
//        requestObject.setHeaders(headerParams);
//
//        LicenseResponse resObj = synchLicenseStatusCall(requestObject);
//
//        if(resObj.getLicenseStatus() == LicenseStatus.ACTIVATED){
//            productLicenseMap.put(productName, resObj.getLicenseString());
//            return LicenseStatus.ACTIVATED;
//        }else if(resObj.getLicenseStatus() != null ){
//            return resObj.getLicenseStatus();
//        }
//
//        throw new LicenseException(LICENSE_ACTIVATION_FAIL_MSG);
//    }
//
//    @Override
//    public LicenseStatus activateLicense(String publicKey, String productName, Map<String, String> productLicenseMap, String customHWID) {
//        String activation_url_template = String.format(BOSCH_LICENSE_ACTIVATION_URI_TEMPLATE,productName);
//        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.POST,
//                BASE_URL,
//                activation_url_template);
//        String msgBody = LICENSE_STRING + productLicenseMap.get(productName).trim()+"\""+
//                ",\"hardwareId\":\"" + customHWID + "\""
//                +PRODUCT_ID_STRING+productName+"\"" + PUBLIC_KEY_STRING+publicKey+"\"}";
//
//        HashMap<String,String> headerParams = new HashMap<>();
//        headerParams.put(CONTENT_TYPE_HEADER_NAME,APPLICATION_JSON_TYPE);
//        headerParams.put(ACCEPT_TYPE_HEADER_NAME, APPLICATION_JSON_TYPE);
//
//        requestObject.setMessageBody(msgBody);
//        requestObject.setHeaders(headerParams);
//
//        LicenseResponse resObj = synchLicenseStatusCall(requestObject);
//
//        if(resObj.getLicenseStatus() == LicenseStatus.ACTIVATED){
//            productLicenseMap.put(productName, resObj.getLicenseString());
//            return LicenseStatus.ACTIVATED;
//        }else if(resObj.getLicenseStatus() != null ){
//            return resObj.getLicenseStatus();
//        }
//
//        throw new LicenseException(LICENSE_ACTIVATION_FAIL_MSG);
//    }
//
//    private LicenseResponse synchLicenseStatusCall(RequestObject requestObject){
//        final AtomicReference<LicenseResponse> atomicRef = new AtomicReference<>();
//
//        CommsManager commsManager = CommsManager.getInstance();
//        commsManager.processRequest(requestObject, new CommsListener() {
//            @Override
//            public void onResponse(ResponseObject responseObject) {
//                JsonParser jsonParser = new JsonParser();
//                JsonObject jsonObject = (JsonObject) jsonParser.parse(responseObject.getResponseBody().toString());
//
//                final String activationstatus = String.valueOf(jsonObject.get("licenseStatus")).replace("\"","");
//                 String licenseString = String.valueOf(jsonObject.get("licenseString"));
//                StringBuilder builder = new StringBuilder(licenseString);
//
//                builder.deleteCharAt(licenseString.length()-1);
//                builder.deleteCharAt(0);
//                licenseString = builder.toString();
//                final String productId = String.valueOf(jsonObject.get("productId"));
//                final String validTill = String.valueOf(jsonObject.get("validTill"));
//
//                LicenseStatus status = LicenseStatus.valueOf(activationstatus);
//                LicenseResponse resObj = new LicenseResponse();
//
//                resObj.setLicenseStatus(status);
//                resObj.setLicenseString(licenseString);
//                resObj.setProductId(productId);
//                resObj.setValidTill(validTill);
//                synchronized (atomicRef){
//                    atomicRef.set(resObj);
//                    atomicRef.notifyAll();
//                }
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                LicenseResponse resObj = new LicenseResponse();
//                synchronized (atomicRef){
//                    atomicRef.set(resObj);
//                    atomicRef.notifyAll();
//                }
//            }
//        });
//
//
//        synchronized (atomicRef){
//            while(atomicRef.get() == null){
//                try {
//                    atomicRef.wait();
//                } catch (Exception e) {
//                    Log.e("ClientLicenseHandler", e.getMessage(), e);
//                }
//            }
//        }
//
//        return atomicRef.get();
//
//    }
//
//    @Override
//    public LicenseStatus deactivateLicense(String publicKey, String productName, Map<String, String> productLicenseMap) {
//        String deactivation_url_template = String.format(BOSCH_LICENSE_DEACTIVATION_URI_TEMPLATE,productName);
//        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.POST,
//                BASE_URL,
//                deactivation_url_template);
//        String msgBody = LICENSE_STRING + productLicenseMap.get(productName).trim()+"\""
//                +PRODUCT_ID_STRING+productName+"\"" + PUBLIC_KEY_STRING+publicKey+"\"}";
//
//        HashMap<String,String> headerParams = new HashMap<>();
//        headerParams.put(CONTENT_TYPE_HEADER_NAME,APPLICATION_JSON_TYPE);
//        headerParams.put(ACCEPT_TYPE_HEADER_NAME, APPLICATION_JSON_TYPE);
//
//        requestObject.setMessageBody(msgBody);
//        requestObject.setHeaders(headerParams);
//
//        LicenseResponse resObj = synchLicenseStatusCall(requestObject);
//
//        if(resObj.getLicenseStatus() == LicenseStatus.DEACTIVATED){
//            productLicenseMap.put(productName, resObj.getLicenseString());
//            return LicenseStatus.DEACTIVATED;
//        }else if(resObj.getLicenseStatus() != null ){
//            return resObj.getLicenseStatus();
//        }
//
//        throw new LicenseException(LICENSE_ACTIVATION_FAIL_MSG);
//    }
//
//    @Override
//    public LicenseStatus deactivateLicense(String publicKey, String productName, Map<String, String> productLicenseMap, String customHWID) {
//        String deactivation_url_template = String.format(BOSCH_LICENSE_DEACTIVATION_URI_TEMPLATE,productName);
//        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.POST,
//                BASE_URL,
//                deactivation_url_template);
//        String msgBody = LICENSE_STRING + productLicenseMap.get(productName).trim()+"\""+
//                ",\"hardwareId\":\"" + customHWID + "\""
//                +PRODUCT_ID_STRING+productName+"\"" + PUBLIC_KEY_STRING+publicKey+"\"}";
//
//        HashMap<String,String> headerParams = new HashMap<>();
//        headerParams.put(CONTENT_TYPE_HEADER_NAME,APPLICATION_JSON_TYPE);
//        headerParams.put(ACCEPT_TYPE_HEADER_NAME, APPLICATION_JSON_TYPE);
//
//        requestObject.setMessageBody(msgBody);
//        requestObject.setHeaders(headerParams);
//
//        LicenseResponse resObj = synchLicenseStatusCall(requestObject);
//
//        if(resObj.getLicenseStatus() == LicenseStatus.DEACTIVATED){
//            productLicenseMap.put(productName, resObj.getLicenseString());
//            return LicenseStatus.DEACTIVATED;
//        }else if(resObj.getLicenseStatus() != null ){
//            return resObj.getLicenseStatus();
//        }
//
//        throw new LicenseException(LICENSE_ACTIVATION_FAIL_MSG);
//    }
//}