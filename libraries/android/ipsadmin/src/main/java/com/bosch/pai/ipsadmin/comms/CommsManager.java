package com.bosch.pai.ipsadmin.comms;


import com.bosch.pai.ipsadmin.comms.model.RequestObject;
import com.bosch.pai.ipsadmin.comms.model.ResponseObject;
import com.bosch.pai.ipsadmin.comms.operation.DELETERunnable;
import com.bosch.pai.ipsadmin.comms.operation.GETRunnable;
import com.bosch.pai.ipsadmin.comms.operation.MULTIPARTPOSTRunnable;
import com.bosch.pai.ipsadmin.comms.operation.POSTRunnable;
import com.bosch.pai.ipsadmin.comms.operation.PUTRunnable;
import com.bosch.pai.ipsadmin.comms.util.CommsUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

/**
 * TODO This is the initial version of comms and needs refractoring as per comments stated under this link
 * https://github.com/Bezirk-Bosch/Deployment-Layer/pull/1
 * Comms Manager which handles all the HTTP Client requests made to the server.
 */
public class CommsManager {

    private static final String TAG = CommsManager.class.getSimpleName();

    private static CommsManager commsInstance;
    private static String contextID;
    private static Map<String, String> baseURLToCrtMap = new HashMap<>();


    /**
     * The constant AUTHENTICATE_USER.
     */
    protected static final String AUTHENTICATE_USER = "/authentication";
    protected static final String REFRESH_AUTH_TOKEN_ENDPOINT = "/refreshAuthToken";
    protected static final String INVALIDATE_TOKEN = "/invalidateAuthToken";
    protected static final String CONTEXT_ID_KEY = "contextId";
    protected static final String TIME_EXPIRE_KEY = "contextIdExpiryMillis";
    private static long contextIdExpiryTime = -1L;

    private final ExecutorService executorService;
    private boolean userFirstTime = true;
    private Timer timer;
    private static volatile String licenseServiceBaseUrl;

    /**
     * The constant connectionHandlerMap.
     */
    private static Map<String, RetryConnectionHandler> connectionHandlerMap = new HashMap<>();
    //Test
    private static Map<String, String> urlContextIdMAP = new HashMap<>();
    //Their should be one master and multiple slaves
    private boolean isMasterURLAvailable = false;
    private static String masterContextId = "";

    /**
     * Gets url context id map.
     *
     * @return the url context id map
     */
    protected Map<String, String> getUrlContextIdMAP() {
        return urlContextIdMAP;
    }

    /**
     * Gets license service base url.
     *
     * @return the license service base url
     */
    public static String getLicenseServiceBaseUrl() {
        return licenseServiceBaseUrl;
    }

    /**
     * Sets license service base url.
     *
     * @param aBaseUrl the a base url
     */
    private static void setLicenseServiceBaseUrl(String aBaseUrl) {
        licenseServiceBaseUrl = aBaseUrl;
    }

    private static void setMasterContextId(String contextID) {
        CommsManager.masterContextId = contextID;
    }

    private CommsManager() {
        final int numOfCore = Runtime.getRuntime().availableProcessors();
        executorService = Executors.newFixedThreadPool(numOfCore);
    }

    /**
     * Sets context id.
     *
     * @param baseURL   the base url
     * @param contextID the context id
     */
    protected static synchronized void setContextID(boolean isMasterURL, String baseURL, String contextID) {
        if (contextID != null && contextID.matches("\"(.+)\"")) {
            CommsManager.contextID = contextID.substring(1, contextID.length() - 1);
        } else if (contextID != null) {
            CommsManager.contextID = contextID;
        }
        if (contextID != null) {
            urlContextIdMAP.put(baseURL, CommsManager.contextID);
            if (isMasterURL) setMasterContextId(CommsManager.contextID);
        }
    }

    /**
     * Sets context id expiry time.
     *
     * @param contextIdExpiryTime the context id expiry time
     */
    protected static synchronized void setContextIDExpiryTime(long contextIdExpiryTime) {
        final long contextTimeRemaining = contextIdExpiryTime - 10000L;
        if (contextTimeRemaining <= 0) {
            CommsManager.contextIdExpiryTime = 0;
        } else {
            CommsManager.contextIdExpiryTime = contextTimeRemaining;
        }
    }

    /**
     * Gets context id.
     *
     * @return the context id
     */
    static synchronized String getContextID() {
        return contextID;
    }

    /**
     * Gets context id expiry time.
     *
     * @return the context id expiry time
     */
    private static synchronized long getContextIdExpiryTime() {
        return contextIdExpiryTime;
    }

    /**
     * Sets certificate.
     *
     * @param certificate the certificate
     */
    private synchronized void setCertificate(String baseURL, InputStream certificate) {
        if (certificate != null) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            try {
                while ((len = certificate.read(buffer)) > -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
            } catch (IOException e) {
                CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Unable to store certificate. ", e);
            }
            CommsManager.baseURLToCrtMap.put(baseURL, byteArrayOutputStream.toString());
        }
    }

    /**
     * Gets certificate.
     *
     * @return the certificate
     */
    private synchronized InputStream getCertificate(String baseURL) {

        if (baseURLToCrtMap.containsKey(baseURL)) {
            return new ByteArrayInputStream(baseURLToCrtMap.get(baseURL).getBytes());
        } else {
            return null;
        }
    }

    /**
     * Get the instance of CommsManager for serving the HTTP Requests
     *
     * @return instance of comms manager
     */
    public static synchronized CommsManager getInstance() {
        if (commsInstance == null)
            commsInstance = new CommsManager();
        return commsInstance;
    }

    /**
     * Register user.
     *
     * @param baseURL       the base url
     * @param companyId     the company id
     * @param userId        the user name
     * @param password      the password
     * @param commsListener the comms listener
     * @param certStream    the cert stream
     */
    public void registerUser(@NotNull String baseURL, @NotNull final String companyId, @NotNull final String userId, @NotNull final String password,
                             @NotNull final CommsListener commsListener, InputStream certStream) {
        Pattern pattern = Pattern.compile("(.*)://(.*):(.*)/(.*)");
        Matcher matcher = pattern.matcher(baseURL);

        if (matcher.find()) {
            String protocol = matcher.group(1);
            String host = matcher.group(2);
            String port = matcher.group(3);
            String licenseBaseUrl = protocol + "://" + host + ":" + port;
            setLicenseServiceBaseUrl(licenseBaseUrl);
        }

        final String baseUrl = (baseURL.endsWith("/")) ? baseURL.substring(0, baseURL.length() - 1) : baseURL;
        final String REGISTER_URL = "/registration/companies/" + companyId + "/users";

        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.POST,
                baseUrl, REGISTER_URL);
        final String jsonBody = "{\"userId\":\"" + userId + "\",\"password\":\"" + password + "\",\"companyId\":\"" + companyId + "\"}";
        requestObject.setMessageBody(jsonBody);
        this.setCertificate(baseUrl, certStream);
        if (this.getCertificate(baseUrl) != null) {
            requestObject.setCertFileStream(this.getCertificate(baseUrl));
            requestObject.setNonBezirkRequest(true);
        }
        setContextID(false, null, null);
        final HashMap<String, String> requestUserHeaderHashMap = new HashMap<>();
        requestUserHeaderHashMap.put(CONTEXT_ID_KEY, getContextID());
        requestObject.setHeaders(requestUserHeaderHashMap);

        processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpURLConnection.HTTP_CREATED) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject) jsonParser.parse(responseObject.getResponseBody().toString());
                    final String contextId = String.valueOf(jsonObject.get(CONTEXT_ID_KEY));
                    final Long contextIdTime = Long.valueOf(jsonObject.get(TIME_EXPIRE_KEY).toString());
                    setContextIDExpiryTime(contextIdTime);
                    setContextID(false, baseUrl, contextId);
                    if (userFirstTime) {
                        userFirstTime = false;
                        ScheduleTimerTask timerTask = new ScheduleTimerTask();
                        Timer timer = new Timer(true);
                        timer.scheduleAtFixedRate(timerTask, 0, getContextIdExpiryTime() - 25000);
                    }
                    commsListener.onResponse(responseObject);
                } else if (responseObject.getStatusCode() == HttpURLConnection.HTTP_CONFLICT) {
                    commsListener.onFailure(responseObject.getStatusCode(), "User already registered!! Try authenticating.");
                } else {
                    commsListener.onFailure(responseObject.getStatusCode(), responseObject.getResponseBody().toString());
                }
            }

            @Override
            public void onFailure(int statusCode, String errMessage) {
                commsListener.onFailure(statusCode, errMessage);
            }
        });
    }

    /**
     * Authenticate.
     * This authentication should be called when multiple gateway's are their with different host name. Then one gateway can be made
     * as master and remaining as slaves to use contextID obtained from authenticating master url.
     * IMPORTANT :: Both master and slave's should contain same Company and User registered prior at server, to use this method.
     *
     * @param baseURL       the base url
     * @param companyId     the company id
     * @param userId        the user id
     * @param password      the password
     * @param certStream    the cert stream
     * @param commsListener the comms listener
     */
    public void authenticateWithMasterGateway(@NotNull final String baseURL, @NotNull final String companyId, @NotNull final String userId, @NotNull final String password,
                                              final InputStream certStream, @NotNull final CommsListener commsListener) {
        this.isMasterURLAvailable = true;
        final String baseUrl = (baseURL.endsWith("/")) ? baseURL.substring(0, baseURL.length() - 1) : baseURL;
        this.setCertificate(baseUrl, certStream);
        authenticateUser(true, baseURL, companyId, userId, password,/*certStream,*/ new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
                    if (userFirstTime) {
                        userFirstTime = false;
                        ScheduleTimerTask timerTask = new ScheduleTimerTask();
                        timer = new Timer(true);
                        timer.scheduleAtFixedRate(timerTask, getContextIdExpiryTime() - 25000, getContextIdExpiryTime() - 25000);
                    }
                    commsListener.onResponse(responseObject);
                } else {
                    CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "authenticate onResponse");
                    commsListener.onFailure(responseObject.getStatusCode(), responseObject.getResponseBody().toString());
                }
            }

            @Override
            public void onFailure(int statusCode, String errMessage) {
                CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "authenticate comms on onFailure in authenticate.");
                commsListener.onFailure(statusCode, errMessage);
            }
        });
    }


    /**
     * Authenticate.
     *
     * @param baseURL       the base url
     * @param companyId     the company id
     * @param userId        the user id
     * @param password      the password
     * @param certStream    the cert stream
     * @param commsListener the comms listener
     */
    public void authenticate(@NotNull final String baseURL, @NotNull final String companyId, @NotNull final String userId, @NotNull final String password,
                             final InputStream certStream, @NotNull final CommsListener commsListener) {
        final String baseUrl = (baseURL.endsWith("/")) ? baseURL.substring(0, baseURL.length() - 1) : baseURL;
        this.setCertificate(baseUrl, certStream);
        authenticateUser(false, baseURL, companyId, userId, password,/*certStream,*/ new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
                    if (userFirstTime) {
                        userFirstTime = false;
                        ScheduleTimerTask timerTask = new ScheduleTimerTask();
                        timer = new Timer(true);
                        timer.scheduleAtFixedRate(timerTask, getContextIdExpiryTime() - 25000, getContextIdExpiryTime() - 25000);
                    }
                    commsListener.onResponse(responseObject);
                } else {
                    CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "authenticate onResponse");
                    commsListener.onFailure(responseObject.getStatusCode(), responseObject.getResponseBody().toString());
                }
            }

            @Override
            public void onFailure(int statusCode, String errMessage) {
                CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "authenticate comms on onFailure in authenticate.");
                commsListener.onFailure(statusCode, errMessage);
            }
        });

    }

    /**
     * Authenticate user.
     *
     * @param isMasterURL    boolean to enable master and slave mode
     * @param baseURL       the base url
     * @param companyId     the company id
     * @param userId        the user name
     * @param password      the password
     * @param commsListener the comms listener
     */
    protected void authenticateUser(boolean isMasterURL, @NotNull String baseURL, @NotNull final String companyId, @NotNull final String userId,
                                    @NotNull final String password,/*@NotNull InputStream certStream,*/ @NotNull final CommsListener commsListener) {
        final String baseUrl = (baseURL.endsWith("/")) ? baseURL.substring(0, baseURL.length() - 1) : baseURL;
        setLicenseServiceBaseUrl(baseUrl);
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.POST, baseUrl, AUTHENTICATE_USER);
        final String jsonBody = "{\"userId\":\"" + userId + "\",\"password\":\"" + password + "\",\"companyId\":\"" + companyId + "\"}";
        requestObject.setMessageBody(jsonBody);
        if (this.getCertificate(baseUrl) != null) {
            requestObject.setNonBezirkRequest(true);
            requestObject.setCertFileStream(this.getCertificate(baseUrl));
        }
        processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject) jsonParser.parse(responseObject.getResponseBody().toString());
                    final String contextId = String.valueOf(jsonObject.get(CONTEXT_ID_KEY));
                    final Long contextIdTime = Long.valueOf(jsonObject.get(TIME_EXPIRE_KEY).toString());
                    setContextIDExpiryTime(contextIdTime);
                    setContextID(isMasterURL, baseUrl, contextId);
                    commsListener.onResponse(responseObject);
                } else {
                    CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "authenticateUser onResponse");
                    commsListener.onFailure(responseObject.getStatusCode(), responseObject.getResponseBody().toString());
                }
            }

            @Override
            public void onFailure(int statusCode, String errMessage) {
                CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "authenticateUser comms on onFailure in authenticate.");
                commsListener.onFailure(statusCode, errMessage);
            }
        });
    }

    /**
     * Method to process the request incoming request objects {@link RequestObject}
     *
     * @param requestObject requestObject to be processed
     * @param commsListener comms listener callback to send back the response
     * @return the uuid
     */
    public UUID processRequest(@NotNull RequestObject requestObject, @NotNull CommsListener commsListener) {
        synchronized (this) {
            final UUID requestID = UUID.randomUUID();
            Runnable runnableTask = null;
            addContextIdHeader(requestObject);
            final RetryConnectionHandler retryConnectionHandler = addRetryConnectionHandler(requestObject);
            final RetryRequestHandler retryRequestHandler = addRetryRequestHandler(requestObject, commsListener);
            boolean isRetry = false;
            if (commsListener instanceof RetryRequestHandler) {
                isRetry = true;
            }
            switch (requestObject.getRequestType()) {
                case DELETE:
                    runnableTask = isRetry ? new DELETERunnable(requestID, requestObject, retryConnectionHandler, commsListener)
                            : new DELETERunnable(requestID, requestObject, retryConnectionHandler, retryRequestHandler);
                    break;
                case GET:
                    runnableTask = isRetry ? new GETRunnable(requestID, requestObject, retryConnectionHandler, commsListener)
                            : new GETRunnable(requestID, requestObject, retryConnectionHandler, retryRequestHandler);
                    break;
                case MULTIPART_POST:
                    runnableTask = isRetry ? new MULTIPARTPOSTRunnable(requestID, requestObject, retryConnectionHandler, commsListener)
                            : new MULTIPARTPOSTRunnable(requestID, requestObject, retryConnectionHandler, retryRequestHandler);
                    break;
                case POST:
                    runnableTask = isRetry ? new POSTRunnable(requestID, requestObject, retryConnectionHandler, commsListener)
                            : new POSTRunnable(requestID, requestObject, retryConnectionHandler, retryRequestHandler);
                    break;
                case PUT:
                    runnableTask = isRetry ? new PUTRunnable(requestID, requestObject, retryConnectionHandler, commsListener)
                            : new PUTRunnable(requestID, requestObject, retryConnectionHandler, retryRequestHandler);
                    break;
                default:
                    CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Unknown request type: " + requestObject.getRequestType().toString());
            }
            executorService.execute(runnableTask);
            return requestID;
        }
    }

    private RetryConnectionHandler addRetryConnectionHandler(RequestObject requestObject) {
        final String baseURLKey = CommsUtil.readBASEURLFromUserURL(requestObject.getBaseURL());
        if (CommsManager.connectionHandlerMap.get(baseURLKey) == null) {
            final RetryConnectionHandler retryConnectionHandler = new RetryConnectionHandler(baseURLKey);
            CommsManager.connectionHandlerMap.put(baseURLKey, retryConnectionHandler);
        }
        if (AUTHENTICATE_USER.equals(requestObject.getApiEndPoint())) {
            final RetryConnectionHandler retryConnectionHandler = CommsManager.connectionHandlerMap.get(baseURLKey);
            retryConnectionHandler.setAuthenticateRequestBody(requestObject.getMessageBody());
            final String crtStr = CommsUtil.convertCrtStreamToString(requestObject.getCertFileStream());
            retryConnectionHandler.setCertificateString(crtStr);
            retryConnectionHandler.setIsMasterURL(isMasterURLAvailable);
            requestObject.setCertFileStream(CommsUtil.getStreamCrtString(crtStr));
        }
        return CommsManager.connectionHandlerMap.get(baseURLKey);
    }

    private void addContextIdHeader(RequestObject requestObject) {
        if (getContextID() != null) {
            Map<String, String> headerMap = requestObject.getHeaders();
            String contextId = urlContextIdMAP.get(CommsUtil.readBASEURLFromUserURL(requestObject.getBaseURL()));
            if (contextId != null) {
                headerMap.put(CONTEXT_ID_KEY, contextId);
            } else {
                headerMap.put(CONTEXT_ID_KEY, masterContextId);
            }
            requestObject.setHeaders(headerMap);
        } else {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "No context ID exists! Please register or authenticate.");
        }
    }

    private RetryRequestHandler addRetryRequestHandler(RequestObject requestObject, CommsListener commsListener) {
        final RetryRequestHandler retryRequestHandler = new RetryRequestHandler(requestObject);
        retryRequestHandler.setCommsListener(commsListener);
        return retryRequestHandler;
    }

    private static String extractIPFromBaseURL(String baseURL) {
        CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "Base URL :" + baseURL);
        for (Map.Entry<String, String> entry : urlContextIdMAP.entrySet()) {
            CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "Entry :: url : " + entry.getKey() + " contextId : " + entry.getValue());
        }
        return CommsUtil.readBASEURLFromUserURL(baseURL);
    }

    public void clearSession() {
        this.userFirstTime = false;
        if (this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
        }
        this.isMasterURLAvailable = false;
        setMasterContextId("");
        invalidateToken();
    }

    protected void refreshToken(final String baseURL, final CommsListener commsListener) {

        final String baseUrl = (baseURL.endsWith("/")) ? baseURL.substring(0, baseURL.length() - 1) : baseURL;
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.POST, baseUrl, REFRESH_AUTH_TOKEN_ENDPOINT);
        if (this.getCertificate(baseUrl) != null) {
            requestObject.setNonBezirkRequest(true);
            requestObject.setCertFileStream(this.getCertificate(baseUrl));
        }
        addContextIdHeader(requestObject);
        processRequest(requestObject, new CommsListener() {
            @Override
            public void onResponse(ResponseObject responseObject) {
                if (responseObject.getStatusCode() == HttpURLConnection.HTTP_OK) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject) jsonParser.parse(responseObject.getResponseBody().toString());
                    final String contextId = String.valueOf(jsonObject.get(CONTEXT_ID_KEY));
                    final Long contextIdTime = Long.valueOf(jsonObject.get(TIME_EXPIRE_KEY).toString());
                    setContextIDExpiryTime(contextIdTime);
                    boolean isMasterURL = false;
                    if(isMasterURLAvailable){
                        isMasterURL = (masterContextId).equals(getUrlContextIdMAP().get(baseUrl));
                    }
                    setContextID(isMasterURL, baseUrl, contextId);
                    commsListener.onResponse(responseObject);
                } else {
                    CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "refreshToken onResponse");
                    commsListener.onFailure(responseObject.getStatusCode(), responseObject.getResponseBody().toString());
                }
            }

            @Override
            public void onFailure(int statusCode, String errMessage) {
                CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Comms on onFailure in refreshToken.");
                commsListener.onFailure(statusCode, errMessage);
            }
        });
    }


    private void invalidateToken() {
        for (Map.Entry<String, String> entry : urlContextIdMAP.entrySet()) {
            final String baseUrl = entry.getKey();
            final RequestObject requestObject = new RequestObject(RequestObject.RequestType.POST, baseUrl, INVALIDATE_TOKEN);
            if (this.getCertificate(baseUrl) != null) {
                requestObject.setNonBezirkRequest(true);
                requestObject.setCertFileStream(this.getCertificate(baseUrl));
            }
            processRequest(requestObject, new CommsListener() {
                @Override
                public void onResponse(ResponseObject responseObject) {
                    if (HttpURLConnection.HTTP_OK == responseObject.getStatusCode()) {
                        CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, TAG, "Invalidated successfully");
                    } else {
                        CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Failed to invalidate token");
                    }
                }

                @Override
                public void onFailure(int statusCode, String errMessage) {
                    CommsUtil.addLogs(CommsUtil.LOG_STATUS.ERROR, TAG, "Failed to invalidate token " + errMessage);
                }
            });
        }
    }
}