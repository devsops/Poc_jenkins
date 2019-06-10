package com.bosch.pai.retail.analytics.responses;

import com.bosch.pai.retail.analytics.model.entryexit.EntryExit;
import com.bosch.pai.retail.analytics.model.entryexit.EntryExitDetails;
import com.bosch.pai.retail.analytics.model.entryexit.IntervalDetails;
import com.bosch.pai.retail.common.responses.StatusMessage;

import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EntryExitResponseTest {

    EntryExitDetails entryExitDetails =new EntryExitDetails( );
     StatusMessage statusMessage =new StatusMessage(StatusMessage.STATUS.SUCCESS,"test");

    //IntervalDetails intervalDetails = new IntervalDetails();
    @Test


    public void testEntryExitResponseTest(){

        EntryExitResponse entryExitResponse= new EntryExitResponse();
        EntryExitResponse entryExitResponse2= new EntryExitResponse(IntervalDetails.HOURLY,entryExitDetails,statusMessage);

        entryExitResponse.setStatusMessage(statusMessage);
        entryExitResponse.setEntryExitDetails(entryExitDetails);
        entryExitResponse.setIntervalDetails(IntervalDetails.HOURLY);

        assertEquals(statusMessage.getStatus(),entryExitResponse.getStatusMessage().getStatus());
        assertEquals(entryExitDetails,entryExitResponse.getEntryExitDetails());
        assertEquals(IntervalDetails.HOURLY,entryExitResponse.getIntervalDetails());

        assertEquals(statusMessage.getStatus(),entryExitResponse2.getStatusMessage().getStatus());
        assertEquals(entryExitDetails,entryExitResponse2.getEntryExitDetails());
        assertEquals(IntervalDetails.HOURLY,entryExitResponse2.getIntervalDetails());
        assertNotNull(entryExitResponse.toString());
        assertNotNull(entryExitResponse2.toString());














    }















}
