package com.bosch.pai.bearing.core;

import com.bosch.pai.bearing.enums.EventType;
import com.bosch.pai.bearing.event.Event;
import com.bosch.pai.bearing.event.Sender;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Event.class, BearingEventQueue.class})
public class BearingEventQueueTest extends BearingHandlerTest {


    private BearingEventQueue bearingEventQueue;

    @Mock
    private Event event;

    @Mock
    private BearingEventQueue.IncomingEvent incomingEventMock;

    @Mock
    private BearingEventQueue bearingEventQueueMock;


    public BearingEventQueueTest() throws Exception {

        super();

        PowerMockito.mockStatic(Event.class);
        PowerMockito.mockStatic(BearingEventQueue.class);
        PowerMockito.whenNew(Event.class).withArguments(String.class, EventType.class, Sender.class).thenReturn(event);
        PowerMockito.whenNew(BearingEventQueue.class).withAnyArguments().thenReturn(bearingEventQueueMock);


        bearingEventQueue = new BearingEventQueue() {
            @Override
            public void enqueue(String requestID, Event event, EventType eventType) {
                super.enqueue(requestID, event, eventType);
            }
        };


    }

    @Test
    public void testEnqueue() {

        bearingEventQueue.enqueue("test", event, EventType.ALGO_RESP);
        final boolean empty = bearingEventQueueMock.queue.isEmpty();
        Assert.assertFalse(empty);
    }

    @Test
    public void testGetters()
    {
        BearingEventQueue bearingEventQueue = new BearingEventQueue() {
            @Override
            public void enqueue(String requestID, Event event, EventType eventType) {
                super.enqueue(requestID, event, eventType);
            }
        };

        bearingEventQueue.setName("DemoSite");
        Event event = new Event("DemoEvent", EventType.TRIGGER_TRAINING, new Sender() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return super.equals(obj);
            }

            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone();
            }

            @Override
            public String toString() {
                return super.toString();
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }
        });

        Sender sender = new Sender() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return super.equals(obj);
            }

            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone();
            }

            @Override
            public String toString() {
                return super.toString();
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }
        };
        bearingEventQueue.enqueue("11122",event,EventType.TRIGGER_TRAINING);
        //
        BearingEventQueue.IncomingEvent incomingEvent = new BearingEventQueue.IncomingEvent("11122",event,EventType.TRIGGER_TRAINING,sender);
        //
        assertEquals(event,incomingEvent.getEvent());
        assertEquals("11122",incomingEvent.getRequestID());
        assertEquals(EventType.TRIGGER_TRAINING,incomingEvent.getEventType());
        assertEquals(sender,incomingEvent.getSender());
        assertEquals("DemoSite",bearingEventQueue.getName());


    }


   /* @Test
    public void testDequeue() {

        final BearingEventQueue.IncomingEvent testEvent = new BearingEventQueue.IncomingEvent("test", event, EventType.ALGO_RESP, null);
        final Object test = when(new BearingEventQueue.IncomingEvent("test", event, EventType.ALGO_RESP, null)).getMock();

        bearingEventQueue.enqueue("test", event, EventType.ALGO_RESP);
        final boolean dequeue = bearingEventQueueMock.queue.remove(test);
        Assert.assertTrue(dequeue);
    }*/


}
