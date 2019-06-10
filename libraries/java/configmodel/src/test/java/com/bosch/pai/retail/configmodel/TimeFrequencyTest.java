package com.bosch.pai.retail.configmodel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TimeFrequencyTest {

    @Test
    public void testTimeFrequencyTest() {
        TimeFrequency timeFrequency = new TimeFrequency(120,140,100);
        TimeFrequency timeFrequency1 = new TimeFrequency(120,140,100);
         TimeFrequency timeFrequency2 = new TimeFrequency();

        timeFrequency.setEndTime(140);
        timeFrequency.setMaxOfferCount(100);
        timeFrequency.setStartTime(120);

        assertEquals(140,timeFrequency.getEndTime());
        assertEquals(120,timeFrequency.getStartTime());
        assertEquals(100,timeFrequency.getMaxOfferCount());
        assertNotNull(timeFrequency2);
        assertNotNull(timeFrequency.toString());
        assertNotNull(timeFrequency.hashCode());
        assertTrue(timeFrequency1.equals(timeFrequency1));




















    }
}