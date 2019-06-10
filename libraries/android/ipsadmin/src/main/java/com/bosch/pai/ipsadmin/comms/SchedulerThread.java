//package com.bosch.pai.ipsadmin.comms;
//
//import android.util.Log;
//
//import com.bosch.pai.ipsadmin.comms.model.ResponseObject;
//import com.bosch.pai.ipsadmin.comms.util.CommsUtil;
//
///**
// * Created by chu7kor on 6/29/2017.
// */
//public class SchedulerThread implements Runnable {
//    private static final String TAG = SchedulerThread.class.getSimpleName();
//    private String baseUrl;
//    private String companyId;
//    private String userId;
//    private String password;
//    private final CommsManager commsManager;
//
//    public SchedulerThread(CommsManager commsManager) {
//        this.commsManager = commsManager;
//    }
//
//    public void setData(String baseUrl, String companyId, String userId, String password) {
//        this.baseUrl = baseUrl;
//        this.companyId = companyId;
//        this.userId = userId;
//        this.password = password;
//    }
//
//    @Override
//    public void run() {
//        commsManager.authenticateUser(baseUrl, companyId, userId, password,/*CommsManager.getCertificate(),*/ new CommsListener() {
//            @Override
//            public void onResponse(ResponseObject responseObject) {
//             /*   if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
//                    JsonParser jsonParser = new JsonParser();
//                    JsonObject jsonObject = (JsonObject) jsonParser.parse(responseObject.getResponseBody().toString());
//                    final String contextId = String.valueOf(jsonObject.get(CommsManager.CONTEXT_ID_KEY));
//                    final Long contextIdTime = Long.valueOf(jsonObject.get(CommsManager.TIME_EXPIRE_KEY).toString());
//                    CommsManager.setContextIDExpiryTime(contextIdTime);
//                    CommsManager.setContextID(contextId);
//                    SchedulerThread schedulerThread = new SchedulerThread(commsManager);
//                    schedulerThread.setData(baseUrl, companyId, userId, password);
//                    commsManager.startService(schedulerThread);
//
//                }*/
//            }
//
//            @Override
//            public void onFailure(int statusCode, String errMessage) {
//                CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "onFailure: " + statusCode + " message" + errMessage);
//            }
//        });
//    }
//}