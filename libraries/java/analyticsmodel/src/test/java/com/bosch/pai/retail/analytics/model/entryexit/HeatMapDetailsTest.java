package com.bosch.pai.retail.analytics.model.entryexit;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HeatMapDetailsTest {
@Test
    public void testHeatMapDetailsTest(){

        HeatMapDetails heatMapDetails = new HeatMapDetails();


        heatMapDetails.setEntries("test");
        heatMapDetails.setUserCount("540");

        assertEquals("test",heatMapDetails.getEntries());
        assertEquals("540",heatMapDetails.getUserCount());

        assertNotNull(heatMapDetails.toString());


    }









}
