
package com.bosch.pai.retail.analytics.model.entryexit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;






public class DayDetailsTest {
    private final Integer day = 2;
    private final Long entryCount = 5L;
    private final Long exitCount = 2L;
    private final List<HourDetails> hours = new ArrayList<>();

    private final String ASSERT_MESSAGE = "Assertion failed";

    @Test
    public void testDayDetails() {
        hours.add(new HourDetails(1, 2L, 1L));
        DayDetails dayDetails = new DayDetails();
        dayDetails.setDay(day);
        dayDetails.setEntryCount(entryCount);
        dayDetails.setExitCount(exitCount);
        dayDetails.setHours(hours);


        final Long entryCountActual = dayDetails.getEntryCount();
        final Long exitActual = dayDetails.getExitCount();
        final Integer dayActual = dayDetails.getDay();
        assertEquals(ASSERT_MESSAGE, entryCount, entryCountActual);
        assertEquals(ASSERT_MESSAGE, exitCount, exitActual);
        assertEquals(ASSERT_MESSAGE, day, dayActual);
        //assertThat(dayDetails, hasProperty("entryCount", equalTo(entryCount)));
        //assertThat(dayDetails, hasProperty("exitCount", equalTo(exitCount)));
        //assertThat(dayDetails, hasProperty("hours", equalTo(hours)));

    }

    @Test
    public void testEntryCount() {
        hours.add(new HourDetails(1, 2L, 1L));
        EntryExit dayDetails = new DayDetails(day, entryCount, exitCount, hours);
        final Long objectTest = dayDetails.entryCount();
        assertNotNull(ASSERT_MESSAGE, objectTest);

    }

    @Test
    public void testExitCount() {
        hours.add(new HourDetails(1, 2L, 1L));
        EntryExit dayDetails = new DayDetails(day, entryCount, exitCount, hours);
        final Long objectTest = dayDetails.exitCount();
        assertNotNull(ASSERT_MESSAGE, objectTest);

    }

    @Test
    public void testToString() {
        hours.add(new HourDetails(1, 2L, 1L));
        DayDetails dayDetails = new DayDetails();
        dayDetails.setDay(day);
        dayDetails.setEntryCount(entryCount);
        dayDetails.setExitCount(exitCount);
        dayDetails.setHours(hours);

        final String objectTest = dayDetails.toString();
        assertNotNull(ASSERT_MESSAGE, objectTest);
    }
}
