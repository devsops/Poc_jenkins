package com.bosch.pai.retail.analytics.controllers;

import com.bosch.pai.retail.analytics.exception.AnalyticsServiceException;
import com.bosch.pai.retail.analytics.responses.OfferAnalyticsResponse;
import com.bosch.pai.retail.analytics.responses.UserOfferAnalyticsResponse;
import com.bosch.pai.retail.analytics.service.OfferAnalyticsService;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;


public class OACTest {

    @Mock
    private OfferAnalyticsService offerAnalyticService;

    @InjectMocks
    private OAC oac;

    @Before
    public void setUp()  {
        RequestContext requestContext = new RequestContext();
        requestContext.setUserId("test");
        ContextHolder.setContext(requestContext);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void goaTest() {

        final int expectedListSize = 3;

        // added 3 items into list
        final List<OfferAnalyticsResponse> offerAnalyticsResponses = new ArrayList<>();
        offerAnalyticsResponses.add(new OfferAnalyticsResponse());
        offerAnalyticsResponses.add(new OfferAnalyticsResponse());
        offerAnalyticsResponses.add(new OfferAnalyticsResponse());

        Mockito.when(offerAnalyticService.goa(any(), any(), any(), any(), any(), any(),any())).thenReturn(offerAnalyticsResponses);

        final List<OfferAnalyticsResponse> analyticsResponses = oac.goa(
                "Proximity_marketing", "20011", "siteTest", "locationTest", 1502895600000L, 1660662000000L,"android");

        assertEquals(expectedListSize, analyticsResponses.size());

        verify(offerAnalyticService).goa(any(), any(), any(), any(), any(), any(),any());
    }


    @Test
    public void testGetOfferAnalytics(){
        List<UserOfferAnalyticsResponse> offerAnalyticsResponses = new ArrayList<>();
        UserOfferAnalyticsResponse userOfferAnalyticsResponse = new UserOfferAnalyticsResponse();
        userOfferAnalyticsResponse.setHierarchyType("1");
        userOfferAnalyticsResponse.setSiteName("test");
        userOfferAnalyticsResponse.setDetails(new ArrayList<>());
        offerAnalyticsResponses.add(userOfferAnalyticsResponse);

        Mockito.when(offerAnalyticService.getOfferAnalytics(anyLong(),anyLong(),anyString(),anyString(),anyString(),any(),any())).thenReturn(offerAnalyticsResponses);

        final List<UserOfferAnalyticsResponse> uoar = oac.getOfferAnalytics("test","test","test",
                "MTUwMDAwMDAwMDAwMA==","MTUwMDAwMDAwMDAwMA==","android","eyIwIjpbIlVSQUQgREFMIiwiT1JBTCBDQVJFIl0sICIxIjpbIjEiLCIyIl19");


        assertEquals(1,uoar.size());

        verify(offerAnalyticService).getOfferAnalytics(anyLong(),anyLong(),anyString(),anyString(),anyString(),any(),any());

    }

    @Test(expected = AnalyticsServiceException.class)
    public void testGetOfferAnalytics_invalidEncoding(){
        List<UserOfferAnalyticsResponse> offerAnalyticsResponses = new ArrayList<>();
        UserOfferAnalyticsResponse userOfferAnalyticsResponse = new UserOfferAnalyticsResponse();
        userOfferAnalyticsResponse.setHierarchyType("1");
        userOfferAnalyticsResponse.setSiteName("test");
        userOfferAnalyticsResponse.setDetails(new ArrayList<>());
        offerAnalyticsResponses.add(userOfferAnalyticsResponse);

        Mockito.when(offerAnalyticService.getOfferAnalytics(anyLong(),anyLong(),anyString(),anyString(),anyString(),any(),any())).thenReturn(offerAnalyticsResponses);

        final List<UserOfferAnalyticsResponse> uoar = oac.getOfferAnalytics("test","test","test",
                "MTUwMDAwMDAwMDAwMA==","MTUwMDAwMDAwMDAwMA==","android","eyIwIjpbIlVSQUQgREFMIiwiT1JBTCBDQVJFIl0sICIxIjpbIjEiLCIyIl1");


        assertEquals(1,uoar.size());

        verify(offerAnalyticService).getOfferAnalytics(anyLong(),anyLong(),anyString(),anyString(),anyString(),any(),any());

    }
}
