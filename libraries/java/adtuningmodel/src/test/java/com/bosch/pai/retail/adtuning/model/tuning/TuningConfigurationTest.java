package com.bosch.pai.retail.adtuning.model.tuning;


import org.junit.Test;


import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TuningConfigurationTest {

    @Test

    public void testTuningConfigurationTest(){

        TuningConfiguration tuningConfiguration = new TuningConfiguration(5,"second",true);
            Number Interval =5;
            String TimeUnit="second";
            Boolean Duplicate=true;

        tuningConfiguration.setInterval(5);
        tuningConfiguration.setTimeUnit("second");
        tuningConfiguration.setDuplicate(true);
        //tuningConfiguration.setAdTuningConfig(List<>);
tuningConfiguration.setAdTuningConfig(new ArrayList<TuningData>());
        assertEquals(5,tuningConfiguration.getInterval());
        assertEquals("second",tuningConfiguration.getTimeUnit());
        assertEquals(true,tuningConfiguration.getDuplicate());
        assertEquals(0,tuningConfiguration.getAdTuningConfig().size());

        assertNotNull(tuningConfiguration.toString());




    }





}
