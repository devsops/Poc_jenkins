package com.bosch.pai.retail.analytics.controllers;

import com.bosch.pai.retail.analytics.exception.AnalyticsServiceException;
import com.bosch.pai.retail.analytics.model.dwelltime.LocationDwellTime;
import com.bosch.pai.retail.analytics.model.heatmap.HeatMapDetail;
import com.bosch.pai.retail.analytics.responses.UserDwellTimeAnalyticsResponse;
import com.bosch.pai.retail.analytics.responses.UserHeatMapAnalyticsResponse;
import com.bosch.pai.retail.analytics.service.UserSessionAnalyticsService;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class USACTest {

    @Mock
    private UserSessionAnalyticsService usas;

    @InjectMocks
    private USAC usac;

    @Before
    public void setUp()  {
        RequestContext requestContext = new RequestContext();
        requestContext.setUserId("test");
        ContextHolder.setContext(requestContext);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void gehsTest() {

        final List<HeatMapDetail> heatMapDetails = new ArrayList<>();
        heatMapDetails.add(new HeatMapDetail());
        heatMapDetails.add(new HeatMapDetail());
        heatMapDetails.add(new HeatMapDetail());

        when(usas.getHeatMaps(any(), any(), any(), any(), any(), any(),any())).thenReturn(heatMapDetails);

        final List<HeatMapDetail> heatMapDetailList = usac.gehs(
                "Proximity_marketing", "20011", "siteTest", "locationTest", 1502895600000L, 1660662000000L,"android");

        assertEquals(heatMapDetails.size(), heatMapDetailList.size());

        verify(usas).getHeatMaps(any(),any(),any(),any(),any(),any(),any());

    }

    @Test
    public void gddTest() {


        final List<LocationDwellTime> locationDwellTimes = new ArrayList<>();
        locationDwellTimes.add(new LocationDwellTime());
        locationDwellTimes.add(new LocationDwellTime());
        locationDwellTimes.add(new LocationDwellTime());

        when(usas.getDwellTimeDetails(any(), any(), any(), any(), any(), any(),any())).thenReturn(locationDwellTimes);

        final List<LocationDwellTime> detailList = usac.gdd(
                "Proximity_marketing", "20011", "siteTest", "locationTest", 1502895600000L, 1660662000000L,"android");

        assertEquals(locationDwellTimes.size(), detailList.size());

        verify(usas).getDwellTimeDetails(any(), any(), any(), any(), any(), any(),any());


    }

    @Test
    public void testGetHierarchyDwellTime(){

        UserDwellTimeAnalyticsResponse userDwellTimeAnalyticsResponse = new UserDwellTimeAnalyticsResponse();
        userDwellTimeAnalyticsResponse.setSiteName("test_site");
        userDwellTimeAnalyticsResponse.setHierarchyType("0");
        userDwellTimeAnalyticsResponse.setHierarchyDwellTimeDetails(new ArrayList<>());
        List<UserDwellTimeAnalyticsResponse> userDwellTimeAnalyticsResponses = new ArrayList<>();
        userDwellTimeAnalyticsResponses.add(userDwellTimeAnalyticsResponse);

        when(usas.getHierarchyDwellTimeDetails(anyString(),anyString(),anyString(),anyLong(),anyLong(),anyString(),any())).thenReturn(userDwellTimeAnalyticsResponses);

        final List<UserDwellTimeAnalyticsResponse> dwellTimeList = usac.getHierarchyDwellTime("test","100","test_site",
                "MTUwMDAwMDAwMDAwMA==","NDYwMDAwMDAwMDAwMDAw","ios","eyIwIjpbIlVSQUQgREFMIiwiT1JBTCBDQVJFIl0sICIxIjpbIjEiLCIyIl19");

        assertEquals(1,dwellTimeList.size());

        verify(usas).getHierarchyDwellTimeDetails(anyString(),anyString(),anyString(),anyLong(),anyLong(),anyString(),any());
    }


    @Test
    public void testGetHierarchyHeatMap(){

        UserHeatMapAnalyticsResponse userHeatMapAnalyticsResponse = new UserHeatMapAnalyticsResponse();
        userHeatMapAnalyticsResponse.setSiteName("test_site");
        userHeatMapAnalyticsResponse.setHierarchyType("0");
        userHeatMapAnalyticsResponse.setHierarchyHeatMapDetails(new ArrayList<>());
        List<UserHeatMapAnalyticsResponse> userHeatMapAnalyticsResponses = new ArrayList<>();
        userHeatMapAnalyticsResponses.add(userHeatMapAnalyticsResponse);

        when(usas.getHierarchyHeatMaps(anyString(),anyString(),anyString(),anyLong(),anyLong(),anyString(),any())).thenReturn(userHeatMapAnalyticsResponses);

        final List<UserHeatMapAnalyticsResponse> heatMapList = usac.getHierarchyHeatMap("test","100","test_site",
                "MTUwMDAwMDAwMDAwMA==","NDYwMDAwMDAwMDAwMDAw","ios","eyIwIjpbIlVSQUQgREFMIiwiT1JBTCBDQVJFIl0sICIxIjpbIjEiLCIyIl19");

        assertEquals(1,heatMapList.size());

        verify(usas).getHierarchyHeatMaps(anyString(),anyString(),anyString(),anyLong(),anyLong(),anyString(),any());
    }

}
