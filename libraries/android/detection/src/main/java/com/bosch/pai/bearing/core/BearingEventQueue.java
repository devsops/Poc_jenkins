package com.bosch.pai.bearing.core;

import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The type Bearing event queue.
 */
abstract class BearingEventQueue extends Thread {

    private final String TAG = BearingEventQueue.class.getName();

    /**
     * Storage for the messages to process
     */
    protected static final BlockingQueue<IncomingEvent> queue = new LinkedBlockingQueue<>();

    /**
     * The type Incoming event.
     */
    static class IncomingEvent {
        private String requestID;
        private Event event;
        private EventType eventType;
        private Sender sender;

        /**
         * Instantiates a new Incoming event.
         *
         * @param requestID the request id
         * @param event     the event
         * @param eventType the event type
         * @param sender    the sender
         */
        IncomingEvent(String requestID, Event event, EventType eventType, Sender sender) {
            this.requestID = requestID;
            this.eventType = eventType;
            this.event = event;
            this.sender = sender;
        }

        /**
         * Gets sender.
         *
         * @return the sender
         */
        public Sender getSender() {
            return sender;
        }


        /**
         * Gets request id.
         *
         * @return the request id
         */
        public String getRequestID() {
            return requestID;
        }

        /**
         * Gets event.
         *
         * @return the event
         */
        public Event getEvent() {
            return event;
        }


        /**
         * Gets event type.
         *
         * @return the event type
         */
        public EventType getEventType() {
            return eventType;
        }


    }

    @Override
    public void run() {
        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.VERBOSE, TAG, "Run definition is defined in its sub-class and hence not defined here");
    }

    /**
     * Thread safe, blocking operation of adding the tuple (topic, message, sender) to the queue for processing
     *
     * @param requestID the request id
     * @param event     the event
     * @param eventType the event type
     */
    public void enqueue(String requestID, Event event, EventType eventType) {
        try {
            queue.put(new IncomingEvent(requestID, event, eventType, null));
        } catch (InterruptedException e) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, "InterruptedExcep:", e);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }
    }

}
