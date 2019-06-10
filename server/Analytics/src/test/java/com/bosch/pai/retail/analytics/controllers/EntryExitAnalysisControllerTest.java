package com.bosch.pai.retail.analytics.controllers;


import com.bosch.pai.retail.analytics.model.entryexit.EntryExitDetails;
import com.bosch.pai.retail.analytics.model.entryexit.IntervalDetails;
import com.bosch.pai.retail.analytics.responses.EntryExitResponse;
import com.bosch.pai.retail.analytics.service.EntryExitAnalyticsService;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

public class EntryExitAnalysisControllerTest {

    @Mock
    private EntryExitAnalyticsService entryExitAnalyticsService;

    @InjectMocks
    private EntryExitAnalysisController entryExitAnalysisController;

    @Before
    public void setUp()   {
        RequestContext requestContext = new RequestContext();
        requestContext.setUserId("test");
        ContextHolder.setContext(requestContext);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getEntryExitAnalyticsTest() {
        final EntryExitResponse response = new EntryExitResponse();
        response.setStatusMessage(new StatusMessage(StatusMessage.STATUS.SUCCESS,"Success"));
        response.setIntervalDetails(IntervalDetails.HOURLY);
        response.setEntryExitDetails(new EntryExitDetails());
        Mockito.when(entryExitAnalyticsService.getEntryExit(any(),any(),any(),any(),any(),any())).thenReturn(response);
        final EntryExitResponse entryExitResponse = entryExitAnalysisController.getEntryExitAnalytics(
                "Proximity_marketing","20011","HOURLY",1502895600000L,1660662000000L,"android");
        assertEquals(StatusMessage.STATUS.SUCCESS,entryExitResponse.getStatusMessage().getStatus());
        Mockito.verify(entryExitAnalyticsService).getEntryExit(any(),any(),any(),any(),any(),any());
    }

}
