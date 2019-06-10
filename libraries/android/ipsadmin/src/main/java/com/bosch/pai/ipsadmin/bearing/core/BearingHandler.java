package com.bosch.pai.ipsadmin.bearing.core;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Looper;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.ipsadmin.bearing.core.event.RequestRetrieveEvent;
import com.bosch.pai.ipsadmin.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.ipsadmin.bearing.core.operation.processor.AsyncReadRequestProcessor;
import com.bosch.pai.ipsadmin.bearing.core.operation.processor.AsyncRetrieveRequestProcessor;
import com.bosch.pai.ipsadmin.bearing.core.operation.processor.AsyncUpdateRequestProcessor;
import com.bosch.pai.ipsadmin.bearing.core.operation.processor.AsyncUploadRequestProcessor;
import com.bosch.pai.ipsadmin.bearing.core.operation.processor.DataCaptureEventProcessor;
import com.bosch.pai.ipsadmin.bearing.core.operation.processor.DataCaptureResponseEventProcessor;
import com.bosch.pai.ipsadmin.bearing.core.operation.processor.DetectionStartRequestProcessor;
import com.bosch.pai.ipsadmin.bearing.core.operation.processor.DetectionStopRequestProcessor;
import com.bosch.pai.ipsadmin.bearing.core.operation.processor.GenerateClassifierEventProcessor;
import com.bosch.pai.ipsadmin.bearing.core.operation.processor.ShutDownDetectionProcessor;
import com.bosch.pai.ipsadmin.bearing.core.operation.processor.ThreshDataEntryEventProcessor;
import com.bosch.pai.ipsadmin.bearing.core.operation.processor.ThreshDetectionStartProcessor;
import com.bosch.pai.ipsadmin.bearing.core.operation.processor.ThreshDetectionStopProcessor;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.event.DataCaptureRequestEvent;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.restclient.BearingRESTClient;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.api.SensorObservation;
import com.bosch.pai.ipsadmin.comms.exception.CertificateLoadException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Bearing handler.
 */
public final class BearingHandler extends BearingEventQueue {

    private final String TAG = BearingHandler.class.getSimpleName();
    // Holds the uuid registered for sensorObservation layer to request holder
    private final static Map<String, RequestDataHolder> UUID_TO_REQUESTDATAHOLDER_MAP = new ConcurrentHashMap<>();
    private final ExecutorService executorService;

    private volatile static BearingHandler instance;

    private BearingHandler(Context context) throws CertificateLoadException {
        final int availableCores = Runtime.getRuntime().availableProcessors();
        executorService = Executors.newFixedThreadPool(availableCores);
        HandlerThread handlerThread = new HandlerThread("SensorObservation Handler");
        startSensorObservationHandler(handlerThread);
        final Looper looper = handlerThread.getLooper();
        SensorObservation.init(looper);
        SensorObservation.getInstance().setContext(context);
        Thread.currentThread().setName(BearingHandler.class.getName());
        try {
            BearingRESTClient.initComsWithContext(context);
        } catch (CertificateLoadException e) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "BearingHandler: ", e);
            throw new CertificateLoadException(e.toString());
        }
    }

    /**
     * Init.
     *
     * @param context the context
     * @throws CertificateLoadException the certificate load exception
     */
    public static void init(Context context) throws CertificateLoadException {
        if (instance == null) {
            instance = new BearingHandler(context);
        }

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static BearingHandler getInstance() {
        return instance;
    }

    /**
     * Gets uuid to request data holder map.
     *
     * @return the uuid to request data holder map
     */
    public static Map<String, RequestDataHolder> getUuidToRequestDataHolderMap() {
        return UUID_TO_REQUESTDATAHOLDER_MAP;
    }

    /**
     * Add request to request data holder map.
     *
     * @param requestDataHolderReq the request data holder req
     */
    public static void addRequestToRequestDataHolderMap(RequestDataHolder requestDataHolderReq) {

        final String identifier = requestDataHolderReq.getRequestId() + "_" + requestDataHolderReq.getApproach();
        UUID_TO_REQUESTDATAHOLDER_MAP.put(identifier, requestDataHolderReq);
    }

    /**
     * Remove request from request data holder map.
     *
     * @param transactionId the transaction id
     * @param approach      the approach
     */
    public static void removeRequestFromRequestDataHolderMap(UUID transactionId, BearingConfiguration.Approach approach) {

        final String url = transactionId + "_" + approach;
        UUID_TO_REQUESTDATAHOLDER_MAP.remove(url);
    }


    @Override
    public void run() {
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "************* Started processor thread *************");
        while (true) {
            try {
                IncomingEvent incomingEvent = queue.take();
                EventType eventType = incomingEvent.getEventType();
                switch (eventType) {
                    case CAPTURE_DATA_EVENT:
                        processDataCaptureRequest(incomingEvent);
                        break;
                    case CAPTURE_DATA_RESP:
                        processDataCaptureResponse(incomingEvent);
                        break;
                    case TRIGGER_TRAINING:
                        processGenerateClassifierRequest(incomingEvent);
                        break;
                    case TRIGGER_DETECTION:
                        processRequestDetectionStart(incomingEvent);
                        break;
                    case ASYNC_READ:
                        processAsyncReadRequest(incomingEvent);
                        break;
                    case ASYNC_RETRIEVE:
                        processAsyncRetrieveRequest(incomingEvent);
                        break;
                    case ASYNC_UPDATE:
                        processAsyncUpdateRequest(incomingEvent);
                        break;
                    case ASYNC_UPLOAD:
                        processAsyncUploadRequest(incomingEvent);
                        break;
                    case THRESH_DATA_ENTRY:
                        processThreshDataEntryRequest(incomingEvent);
                        break;
                    case THRESH_DETECTION:
                        processThreshDetectionStart(incomingEvent);
                        break;
                    case STOP_DETECTION:
                        processRequestDetectionStopRequest(incomingEvent);
                        break;
                    case STOP_THRESH_DETECTION:
                        processThreshDetectionStopRequest(incomingEvent);
                        break;
                    case SHUTDOWN_DETECTION:
                        processShutdownDetectionRequest(incomingEvent);
                        break;
                    default:
                        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "run: Unsupported");
                        break;

                }
            } catch (InterruptedException e) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "Error: ", e);
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processAsyncUploadRequest(IncomingEvent incomingEvent) {

        final String requestID = incomingEvent.getRequestID();
        final Event event = incomingEvent.getEvent();
        final Sender sender = incomingEvent.getEvent().getSender();
        final Runnable asyncUploadRequestProcessor = new AsyncUploadRequestProcessor(requestID, event, sender);
        executorService.submit(asyncUploadRequestProcessor);


    }


    private void processRequestDetectionStart(IncomingEvent incomingEvent) {
        final String requestID = incomingEvent.getRequestID();
        final Event event = incomingEvent.getEvent();
        final Sender sender = incomingEvent.getEvent().getSender();
        final Runnable workerRequestDetectionStartProcessor = new DetectionStartRequestProcessor(requestID, event, sender);
        executorService.submit(workerRequestDetectionStartProcessor);
    }

    private void processDataCaptureRequest(IncomingEvent incomingEvent) {
        final String requestID = incomingEvent.getRequestID();
        final Event event = incomingEvent.getEvent();
        final Sender sender = incomingEvent.getEvent().getSender();
        final Runnable workerDetectionRequestProcessor = new DataCaptureEventProcessor(requestID, event, sender);
        executorService.submit(workerDetectionRequestProcessor);
    }

    private void processDataCaptureResponse(IncomingEvent incomingEvent) {
        final String requestID = incomingEvent.getRequestID();
        final Event event = incomingEvent.getEvent();
        final Sender sender = incomingEvent.getEvent().getSender();
        final BearingConfiguration.Approach approach = incomingEvent.getEvent().getApproach();
        final RequestDataHolder requestDataHolder = UUID_TO_REQUESTDATAHOLDER_MAP.get(requestID + "_" + approach);
        final Runnable workerDetectionResponseProcessor = new DataCaptureResponseEventProcessor(requestID, event, sender, requestDataHolder);
        executorService.submit(workerDetectionResponseProcessor);
    }

    private void processThreshDataEntryRequest(IncomingEvent incomingEvent) {
        final String requestID = incomingEvent.getRequestID();
        final Event event = incomingEvent.getEvent();
        final Sender sender = incomingEvent.getEvent().getSender();
        final Runnable workerThreshDataEntryEventProcessor = new ThreshDataEntryEventProcessor(requestID, event, sender);
        executorService.submit(workerThreshDataEntryEventProcessor);
    }


    private void processThreshDetectionStart(IncomingEvent incomingEvent) {
        final String requestID = incomingEvent.getRequestID();
        final Event event = incomingEvent.getEvent();
        final Sender sender = incomingEvent.getEvent().getSender();
        final Runnable workerThreshDetectionStartProcessor = new ThreshDetectionStartProcessor(requestID, event, sender);
        executorService.submit(workerThreshDetectionStartProcessor);

    }

    private void processGenerateClassifierRequest(IncomingEvent incomingEvent) {
        final String requestID = incomingEvent.getRequestID();
        final Event event = incomingEvent.getEvent();
        final Sender sender = incomingEvent.getEvent().getSender();
        final Runnable workerGenerateClassifierProcessor = new GenerateClassifierEventProcessor(requestID, event, sender);
        executorService.submit(workerGenerateClassifierProcessor);
    }


    private void processRequestDetectionStopRequest(IncomingEvent incomingEvent) {
        final String requestID = incomingEvent.getRequestID();
        final Event event = incomingEvent.getEvent();
        final Sender sender = incomingEvent.getEvent().getSender();
        final Runnable workerGenerateClassifierProcessor = new DetectionStopRequestProcessor(requestID, event, sender);
        executorService.submit(workerGenerateClassifierProcessor);
    }

    private void processThreshDetectionStopRequest(IncomingEvent incomingEvent) {
        final String requestID = incomingEvent.getRequestID();
        final Event event = incomingEvent.getEvent();
        final Sender sender = incomingEvent.getEvent().getSender();
        final Runnable workerGenerateClassifierProcessor = new ThreshDetectionStopProcessor(requestID, event, sender);
        executorService.submit(workerGenerateClassifierProcessor);
    }

    private void processAsyncReadRequest(IncomingEvent incomingEvent) {
        final String requestID = incomingEvent.getRequestID();
        final Event event = incomingEvent.getEvent();
        final Sender sender = incomingEvent.getEvent().getSender();
        final Runnable workerAsyncReadProcessor = new AsyncReadRequestProcessor(requestID, event, sender);
        executorService.submit(workerAsyncReadProcessor);
    }

    private void processAsyncRetrieveRequest(IncomingEvent incomingEvent) {
        final String requestID = incomingEvent.getRequestID();
        final Event event = incomingEvent.getEvent();
        final Sender sender = incomingEvent.getEvent().getSender();
        final RequestRetrieveEvent requestRetrieveEvent = (RequestRetrieveEvent) event;
        if (requestRetrieveEvent.getFetchRequest().equals(RequestRetrieveEvent.ServerFetch.SCAN_SENSOR)) {
            DataCaptureRequestEvent dataCaptureRequestEvent = new DataCaptureRequestEvent(incomingEvent.getRequestID(), EventType.CAPTURE_DATA_EVENT, sender);
            dataCaptureRequestEvent.setSite(true);
            dataCaptureRequestEvent.setSiteMerge(true);
            dataCaptureRequestEvent.setApproach(requestRetrieveEvent.getApproach());
            dataCaptureRequestEvent.setSiteName(requestRetrieveEvent.getSiteName());
            dataCaptureRequestEvent.setSensors(requestRetrieveEvent.getSensors());
            final Runnable workerDetectionRequestProcessor = new DataCaptureEventProcessor(dataCaptureRequestEvent.getRequestID(), dataCaptureRequestEvent, dataCaptureRequestEvent.getSender());
            executorService.submit(workerDetectionRequestProcessor);
        } else {
            final Runnable workerAsyncReadProcessor = new AsyncRetrieveRequestProcessor(requestID, event, sender);
            executorService.submit(workerAsyncReadProcessor);

        }


    }

    private void processAsyncUpdateRequest(IncomingEvent incomingEvent) {
        final String requestID = incomingEvent.getRequestID();
        final Event event = incomingEvent.getEvent();
        final Sender sender = incomingEvent.getEvent().getSender();
        final Runnable workerAsyncUpdateProcessor = new AsyncUpdateRequestProcessor(requestID, event, sender);
        executorService.submit(workerAsyncUpdateProcessor);
    }

    private void startSensorObservationHandler(HandlerThread handlerThread) {
        handlerThread.start();

    }

    private void processShutdownDetectionRequest(IncomingEvent incomingEvent) {
        final String requestID = incomingEvent.getRequestID();
        final Event event = incomingEvent.getEvent();
        final Sender sender = incomingEvent.getEvent().getSender();
        final Runnable workerShutdownDetectionProcessor = new ShutDownDetectionProcessor(requestID, event, sender);
        executorService.submit(workerShutdownDetectionProcessor);
    }
}
