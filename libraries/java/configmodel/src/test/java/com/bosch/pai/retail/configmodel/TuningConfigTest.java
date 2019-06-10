package com.bosch.pai.retail.configmodel;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TuningConfigTest {


    @Test


    public  void testTuningConfigTest(){


        TuningConfig tuningConfig = new TuningConfig();
        TuningConfig tuningConfig1= new TuningConfig();

        tuningConfig.setDuplicateOfferAllowed(false);
        tuningConfig.setLocationFrequencyList(new ArrayList<LocationFrequency>());
        tuningConfig.setTimeFrequencyList(new ArrayList<TimeFrequency>());
        tuningConfig.setTimetype(Timetype.HOURS);


        assertEquals(false,tuningConfig.isDuplicateOfferAllowed());
        assertEquals(0,tuningConfig.getLocationFrequencyList().size());
        assertEquals(0,tuningConfig.getTimeFrequencyList().size());
        assertEquals(Timetype.HOURS,tuningConfig.getTimetype());


        assertNotNull(tuningConfig.toString());
        assertNotNull(tuningConfig.hashCode());
        //assertTrue(tuningConfig.equals(tuningConfig1));













    }






















}
