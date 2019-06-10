package com.bosch.pai.session;


import android.content.Context;
import android.support.annotation.NonNull;
import android.webkit.URLUtil;

import com.bosch.pai.comms.CommsListener;
import com.bosch.pai.comms.CommsManager;
import com.bosch.pai.comms.model.RequestObject;
import com.bosch.pai.comms.model.ResponseObject;
import com.bosch.pai.detection.Util;
import com.bosch.pai.detection.models.StatusMessage;
import com.bosch.pai.util.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The type Session handler.
 */
public class SessionHandler {

    private static final String TAG = SessionHandler.class.getName();

    private static SessionHandler sessionHandler;
    private final String sessionFilePath;
    private final Gson gson;

    /**
     * Gets instance.
     *
     * @param context the context
     * @return the instance
     */
    public static SessionHandler getInstance(Context context) {
        if (sessionHandler == null) {
            sessionHandler = new SessionHandler(context);
        }
        return sessionHandler;
    }

    private SessionHandler(Context context) {
        String sessionFileName = "SessionInfo.json";
        sessionFilePath = context.getFilesDir().getPath() + File.separator + sessionFileName;
        gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .create();
    }

    /**
     * Start session string.
     *
     * @param userId  the user id
     * @param storeId the store id
     * @return the string
     */
    public String startSession(String userId, String storeId) {
        if(Util.getUserType() == Util.UserType.ROLE_FREE) {
            //Session feature not enabled for free version
            return String.valueOf(UUID.randomUUID());
        }
        Util.addLogs(Util.LOG_STATUS.DEBUG, TAG, "startSession: starting new session for userId :" + userId, null);
        final String uuid = String.valueOf(UUID.randomUUID());
        final Map<SessionInfo, List<SubSessionInfo>> sessionInfoListMap = new LinkedHashMap<>(convertJSONtoMap(readFromFile()));
        final SessionInfo sessionInfo = new SessionInfo(userId);
        sessionInfo.setStoreId(storeId);
        sessionInfo.setSessionId(uuid);
        sessionInfo.setStartTime(System.currentTimeMillis());
        sessionInfoListMap.put(sessionInfo, new LinkedList<SubSessionInfo>());
        writeToFile(convertMapToJSON(sessionInfoListMap));
        return uuid;
    }

    /**
     * End session.
     *
     * @param sessionId the session id
     */
    public void endSession(String sessionId) {
        if(Util.getUserType() == Util.UserType.ROLE_FREE) {
            //Session feature not enabled for free version
            return;
        }
        Util.addLogs(Util.LOG_STATUS.DEBUG, TAG, "endSession: End of user session", null);
        final Map<SessionInfo, List<SubSessionInfo>> sessionInfoListMap = convertJSONtoMap(readFromFile());
        for (Map.Entry<SessionInfo, List<SubSessionInfo>> entry : sessionInfoListMap.entrySet()) {
            if (sessionId.equals(entry.getKey().getSessionId())) {
                entry.getKey().setEndTime(System.currentTimeMillis());
                entry.getKey().setValid(true);
                (new LinkedList<>(entry.getValue())).getLast().setEndTime(System.currentTimeMillis());
                (new LinkedList<>(entry.getValue())).getLast().setValid(true);
                break;
            }
        }
        writeToFile(convertMapToJSON(sessionInfoListMap));
    }

    /**
     * Start sub session.
     *
     * @param sessionId      the session id
     * @param subSessionInfo the sub session info
     */
    public void startSubSession(String sessionId, SubSessionInfo subSessionInfo) {
        if(Util.getUserType() == Util.UserType.ROLE_FREE) {
            //Session feature not enabled for free version
            return;
        }
        final LinkedList<SubSessionInfo> subSessionInfoList = getSubSessionList(sessionId);
        if (!subSessionInfoList.isEmpty()) {
            SubSessionInfo temp = subSessionInfoList.getLast();
            temp.setEndTime(System.currentTimeMillis());
            temp.setValid(true);
        }
        subSessionInfo.setStartTime(System.currentTimeMillis());
        subSessionInfoList.add(subSessionInfo);
        writeToFile(convertMapToJSON(updateSubSessionInfoInMap(sessionId, subSessionInfoList)));
    }

    /**
     * Clear session info data boolean.
     *
     * @return the boolean
     */
    private boolean clearSessionInfoData() {
        return writeToFile("");
    }

    /**
     * Gets session info data as string.
     *
     * @return the session info data as string
     */
    private String getSessionInfoDataAsString() {
        final Map<SessionInfo, List<SubSessionInfo>> sessionInfoListMap = convertJSONtoMap(readFromFile());
        if(!sessionInfoListMap.isEmpty()) {
            updateEndTimeIfLastSessionIsInValid(sessionInfoListMap);
        }
        return convertMapToJSON(sessionInfoListMap);
    }

    private void updateEndTimeIfLastSessionIsInValid(Map<SessionInfo, List<SubSessionInfo>> sessionInfoListMap) {
        final Map.Entry<SessionInfo, List<SubSessionInfo>> entry = sessionInfoListMap.entrySet().iterator().next();
        try {
            if (!entry.getKey().isValid()) {
                entry.getKey().setEndTime(System.currentTimeMillis());
                entry.getValue().get(entry.getValue().size() - 1).setEndTime(System.currentTimeMillis());
            }
        } catch (Exception e) {
            Util.addLogs(Util.LOG_STATUS.ERROR, TAG, "Error updating end-time for invalid session", e);
        }
    }


    private boolean writeToFile(String writeData) {
        synchronized (this) {
            FileWriter fileWriter = null;
            BufferedWriter bufferedWriter = null;
            File file = new File(sessionFilePath);
            try {
                fileWriter = new FileWriter(file, false);
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(writeData);
            } catch (IOException e) {
                Util.addLogs(Util.LOG_STATUS.ERROR, TAG, "No such file exists! Error writing to file", e);
                return false;
            } finally {
                try {
                    if (bufferedWriter != null)
                        bufferedWriter.close();
                    if (fileWriter != null)
                        fileWriter.close();
                } catch (IOException e) {
                    Util.addLogs(Util.LOG_STATUS.ERROR, TAG, "Error writing session data to file", null);
                }
            }
            return true;
        }
    }

    @NonNull
    private String readFromFile() {
        synchronized (this) {
            final StringBuilder stringBuilder = new StringBuilder();
            FileReader fileReader = null;
            BufferedReader br = null;
            File file = new File(sessionFilePath);
            try {
                fileReader = new FileReader(file);
                br = new BufferedReader(fileReader);
                String line;
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }

            } catch (IOException e) {
                Util.addLogs(Util.LOG_STATUS.DEBUG, TAG, "No such file exists! Error reading from file", null);
            } finally {
                try {
                    if (br != null)
                        br.close();
                    if (fileReader != null)
                        fileReader.close();
                } catch (IOException e) {
                    Util.addLogs(Util.LOG_STATUS.DEBUG, TAG, "Error reading session data from file", null);
                }

            }
            return stringBuilder.toString();
        }
    }

    private String convertMapToJSON(Map<SessionInfo, List<SubSessionInfo>> sessionInfoListMap) {
        Type type = new TypeToken<Map<SessionInfo, List<SubSessionInfo>>>() {
        }.getType();
        return gson.toJson(sessionInfoListMap, type);
    }

    private Map<SessionInfo, List<SubSessionInfo>> convertJSONtoMap(String json) {
        Map<SessionInfo, List<SubSessionInfo>> sessionInfoListMap = new LinkedHashMap<>();
        try {
            Type type = new TypeToken<Map<SessionInfo, List<SubSessionInfo>>>() {
            }.getType();
            sessionInfoListMap = gson.fromJson(json, type);
        } catch (JsonParseException e) {
            Util.addLogs(Util.LOG_STATUS.DEBUG, TAG, "convertJSONtoMap : Error parsing JSON", null);
        }
        return sessionInfoListMap != null ? sessionInfoListMap : Collections.<SessionInfo, List<SubSessionInfo>>emptyMap();
    }

    @NonNull
    private LinkedList<SubSessionInfo> getSubSessionList(String sessionId) {
        final Map<SessionInfo, List<SubSessionInfo>> sessionInfoListMap = convertJSONtoMap(readFromFile());
        for (Map.Entry<SessionInfo, List<SubSessionInfo>> entry : sessionInfoListMap.entrySet()) {
            if (sessionId.equals(entry.getKey().getSessionId())) {
                return new LinkedList<>(entry.getValue());
            }
        }
        return new LinkedList<>();
    }

    private Map<SessionInfo, List<SubSessionInfo>> updateSubSessionInfoInMap(String sessionId, List<SubSessionInfo> subSessionInfoList) {
        final Map<SessionInfo, List<SubSessionInfo>> sessionInfoListMap = convertJSONtoMap(readFromFile());
        for (Map.Entry<SessionInfo, List<SubSessionInfo>> entry : sessionInfoListMap.entrySet()) {
            if (sessionId.equals(entry.getKey().getSessionId())) {
                sessionInfoListMap.get(entry.getKey()).clear();
                sessionInfoListMap.get(entry.getKey()).addAll(subSessionInfoList);
                return sessionInfoListMap;
            }
        }
        return sessionInfoListMap;
    }

    /**
     * Upload previous session data if available.
     *
     */
    public void uploadPreviousSessionDataIfAvailable(Context context, String companyID) {
        if(Util.getUserType() == Util.UserType.ROLE_FREE) {
            //Session feature not enabled for free version
            return;
        }
        try {
            if (!getSessionInfoDataAsString().isEmpty()) {

                final RequestObject requestObject = new RequestObject(RequestObject.RequestType.POST, Constant.getServerUrl(),
                        "/gatewayService/ipssession/companies/" + companyID + "/sessions/saveCompleteSession/");
                requestObject.setMessageBody(getSessionInfoDataAsString());
                setCertBasedOnHttpsRequest(context, requestObject);
                //requestObject.setFallbackHttpPort(Integer.parseInt(configuration.getGatewayServiceHttpPort()));
                requestObject.setRetry(true);
                //requestObject.setRetryCount(2);
                //requestObject.setRetryAfterMillis(10000);
                CommsManager.getInstance().processRequest(requestObject, new CommsListener() {
                    @Override
                    public void onResponse(ResponseObject responseObject) {
                        try {
                            if (isResponseValid(responseObject)) {
                                final Type type = new TypeToken<StatusMessage>() {
                                }.getType();
                                try {
                                    final StatusMessage statusMessage =
                                            gson.fromJson(responseObject.getResponseBody().toString(), type);
                                    if (statusMessage != null
                                            && statusMessage.getStatus() != null
                                            && statusMessage.getStatus().equals(StatusMessage.STATUS.SUCCESS)) {
                                        Util.addLogs(Util.LOG_STATUS.DEBUG, TAG, "onResponse: store session data uploaded successfully", null);
                                        final boolean isCleared = sessionHandler.clearSessionInfoData();
                                        Util.addLogs(Util.LOG_STATUS.DEBUG, TAG, "Cleared session data after uploading :: " + isCleared, null);
                                    } else {
                                        Util.addLogs(Util.LOG_STATUS.DEBUG, TAG, "Error uploading session data::" + responseObject.getStatusMessage(), null);
                                    }
                                } catch (JsonParseException e) {
                                    Util.addLogs(Util.LOG_STATUS.ERROR, TAG, "Error uploading session data::" + responseObject.getStatusMessage(), null);
                                }
                            } else {
                                Util.addLogs(Util.LOG_STATUS.ERROR, TAG, "Error uploading session data::" + responseObject, null);
                            }
                        } catch (Exception e) {
                            Util.addLogs(Util.LOG_STATUS.ERROR, TAG, "Error : " + e, e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String errMessage) {
                        Util.addLogs(Util.LOG_STATUS.DEBUG, TAG, "Error uploading session data" + errMessage, null);
                    }
                });
            }
        } catch (Exception e) {
            Util.addLogs(Util.LOG_STATUS.ERROR, TAG, "Error : " + e, e);
        }
    }

    private void setCertBasedOnHttpsRequest(Context context, RequestObject requestObject) {
        if (URLUtil.isHttpsUrl(requestObject.getBaseURL()) && Util.getCertificate(context) != null) {
            requestObject.setCertFileStream(Util.getCertificate(context));
            requestObject.setNonBezirkRequest(true);

        }
    }

    private boolean isResponseValid(ResponseObject responseObject) {
        try {
            if (responseObject != null) {
                final int httpStatus = responseObject.getStatusCode();
                return (httpStatus / 100 == 2) && responseObject.getResponseBody() != null;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private String getErrorMessageFromResponse(ResponseObject responseObject) {
        final StringBuilder errorMessage = new StringBuilder("Error from server. ");
        if (responseObject != null && responseObject.getResponseBody() != null) {
            errorMessage.append(responseObject.getResponseBody().toString());
        }
        return errorMessage.toString();
    }
}
