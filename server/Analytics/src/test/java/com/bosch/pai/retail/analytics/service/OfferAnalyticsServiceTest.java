/*
package com.bosch.pai.retail.analytics.service;

import com.bosch.pai.retail.analytics.dao.OfferResponseDAO;
import com.bosch.pai.retail.analytics.responses.OfferAnalyticsResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class OfferAnalyticsServiceTest {

    @Mock
    private OfferResponseDAO offerResponseDAO;

    @InjectMocks
    private OfferAnalyticsService offerAnalyticsService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void goaTest() {
        final List<OfferAnalyticsResponse> offerAnalyticsResponses = new ArrayList<>();
        OfferAnalyticsResponse offerAnalyticsResponse = new OfferAnalyticsResponse("test_comp","test_store","test_site","test_loc",12L,13L,123L,124L);
        offerAnalyticsResponses.add(offerAnalyticsResponse);
        when(offerResponseDAO.getOfferAnalytics(any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(offerAnalyticsResponses);
        final List<OfferAnalyticsResponse> responseEntity = offerAnalyticsService.goa(
                 1502895600000L, 1660662000000L,"Proximity_marketing", "20011", "siteTest", "locationTest","android");
        Assert.assertEquals(1, responseEntity.size());
        Mockito.verify(offerResponseDAO).getOfferAnalytics(any(), any(), any(), any(), any(), any(),any(),any());
    }

}

*/
