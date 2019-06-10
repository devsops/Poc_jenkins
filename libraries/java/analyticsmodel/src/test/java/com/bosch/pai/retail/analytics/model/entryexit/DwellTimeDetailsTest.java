package com.bosch.pai.retail.analytics.model.entryexit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DwellTimeDetailsTest {

    @Test

    public void testDwellTimeDetailsTest(){


        DwellTimeDetails dwellTimeDetails = new DwellTimeDetails();

        dwellTimeDetails.setEntries("test");
        dwellTimeDetails.setAverageDuration("23");
        dwellTimeDetails.setUserCount("12");
        assertEquals("test",dwellTimeDetails.getEntries());
        assertEquals("23",dwellTimeDetails.getAverageDuration());
        assertEquals("12",dwellTimeDetails.getUserCount());

        assertNotNull(dwellTimeDetails.toString());







    }

















}
