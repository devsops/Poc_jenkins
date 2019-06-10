package com.bosch.pai.retail.analytics.responses;

import com.bosch.pai.retail.analytics.model.entryexit.HeatMapDetails;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserHeatMapAnalyticsResponseTest {

@Test
    public void testUserHeatMapAnalyticsResponseTest(){

        UserHeatMapAnalyticsResponse userHeatMapAnalyticsResponse = new UserHeatMapAnalyticsResponse();


        userHeatMapAnalyticsResponse.setSiteName("testloc");
        userHeatMapAnalyticsResponse.setHierarchyType("brand");
        userHeatMapAnalyticsResponse.setHierarchyHeatMapDetails(new ArrayList<HeatMapDetails>());
        assertEquals("testloc",userHeatMapAnalyticsResponse.getSiteName());
        assertEquals("brand",userHeatMapAnalyticsResponse.getHierarchyType());
        assertNotNull(userHeatMapAnalyticsResponse.getHierarchyHeatMapDetails());
        assertNotNull(userHeatMapAnalyticsResponse.toString());











    }












}
