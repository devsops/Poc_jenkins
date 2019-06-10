package com.bosch.pai.retail.analytics.service;

import com.bosch.pai.retail.analytics.dao.EntryExitDAO;
import com.bosch.pai.retail.analytics.model.entryexit.EntryExitDetails;
import com.bosch.pai.retail.analytics.model.entryexit.IntervalDetails;
import com.bosch.pai.retail.analytics.responses.EntryExitResponse;
import com.bosch.pai.retail.common.responses.StatusMessage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EntryExitAnalyticsServiceTest {

    @InjectMocks
    private EntryExitAnalyticsService entryExitAnalyticsService;

    @Mock
    private EntryExitDAO entryExitDAO;

    @Before
    public void setUp()  {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getEntryExitTest() {

        final EntryExitResponse response = new EntryExitResponse();
        response.setStatusMessage(new StatusMessage(StatusMessage.STATUS.SUCCESS, "Success"));
        response.setIntervalDetails(IntervalDetails.HOURLY);
        response.setEntryExitDetails(new EntryExitDetails());
        when(entryExitDAO.getEntryExit(any(), any(), any(), any(), any(),any())).thenReturn(response);
        final EntryExitResponse entryExitResponse = entryExitAnalyticsService.getEntryExit("Proximity_marketing", "20011",
                "HOURLY", 1502895600000L, 1660662000000L,"android");
        assertEquals(StatusMessage.STATUS.SUCCESS, entryExitResponse.getStatusMessage().getStatus());
        verify(entryExitDAO).getEntryExit(any(), any(), any(), any(), any(),any());
    }

}
