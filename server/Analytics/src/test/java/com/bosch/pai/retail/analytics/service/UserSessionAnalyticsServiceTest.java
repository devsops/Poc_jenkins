/*
package com.bosch.pai.retail.analytics.service;

import com.bosch.pai.retail.analytics.dao.SubSessionDetailDAO;
import com.bosch.pai.retail.analytics.model.dwelltime.LocationDwellTime;
import com.bosch.pai.retail.analytics.model.heatmap.HeatMapDetail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class UserSessionAnalyticsServiceTest {

    @Mock
    private SubSessionDetailDAO subSessionDetailDAO;


    @InjectMocks
    private UserSessionAnalyticsService userSessionAnalyticsService;

    @Before
    public void setUp()  {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getHeatMapsTest() {

        final List<HeatMapDetail> heatMapDetails = new ArrayList<>();
        heatMapDetails.add(new HeatMapDetail());
        heatMapDetails.add(new HeatMapDetail());
        heatMapDetails.add(new HeatMapDetail());
//        when(subSessionDetailDAO.getHeatMaps(any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(heatMapDetails);

        final List<HeatMapDetail> heatMapDetailList = userSessionAnalyticsService.getHeatMaps(
                "test_comp", "20011", "siteTest", "locationTest", 1502895600000L, 1660662000000L,"android");

        assertEquals(heatMapDetails.size(), heatMapDetailList.size());

//        verify(subSessionDetailDAO).getHeatMaps(any(), any(), any(), any(), any(), any(), any(),any());

    }

   */
/* @Test
    public void getLocationsTest()   {

        try {
            final Set<String> first = new HashSet<>();
            first.add("12");
            first.add("13");

            final Set<String> second = new HashSet();
            second.add("14");
            second.add("15");

//            final List<SiteLocation> siteLocations = new ArrayList<>();
//            siteLocations.add(new SiteLocation("PPZ", "20011", "siteTest", "locationTest1", first));
//            siteLocations.add(new SiteLocation("PPZ", "20011", "siteTest", "locationTest2", second));
//
//            when(sldao.getSiteLocationList(any(), any())).thenReturn(siteLocations);

            final Set<String> locations = new HashSet<>();
            locations.add("locationTest1");
            locations.add("locationTest2");

            final Method method = classes.getDeclaredMethod("getLocations", String.class, String.class);
            method.setAccessible(true);
            final Set<String> actualLocations = Whitebox.invokeMethod(userSessionAnalyticsService, "getLocations", "PPZ", "20011");

            assertEquals(locations, actualLocations);

            verify(sldao).getSiteLocationList(any(), any());
        }catch (Exception e){
            fail();
        }
    }*//*


  */
/*  @Test
    public void getDwellTimeDetailsTest() {

        final List<LocationDwellTime> locationDwellTimes = new ArrayList<>();
        locationDwellTimes.add(new LocationDwellTime());
        locationDwellTimes.add(new LocationDwellTime());
        locationDwellTimes.add(new LocationDwellTime());

//        final List<SiteLocation> siteLocations = new ArrayList<>();
//        siteLocations.add(new SiteLocation());
//        siteLocations.add(new SiteLocation());
//
//        when(sldao.getSiteLocationList(any(), any())).thenReturn(siteLocations);
        when(subSessionDetailDAO.getDwellTimeDetails(any(), any(), any(), any(), any(), any(), any(),any())).thenReturn(locationDwellTimes);

        final List<LocationDwellTime> detailList = userSessionAnalyticsService.getDwellTimeDetails(
                "Proximity_marketing", "20011", "siteTest", "locationTest", 1502895600000L, 1660662000000L,"android");

        assertEquals(locationDwellTimes.size(), detailList.size());

        verify(subSessionDetailDAO).getDwellTimeDetails(any(), any(), any(), any(), any(), any(), any(),any());

    }*//*


}

*/
