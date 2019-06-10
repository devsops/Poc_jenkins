
package com.bosch.pai.retail.analytics.model.entryexit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;




public class HourDetailsTest {

    private static final Integer hour = 2;
    private static final Long entryCount = 5L;
    private static final Long exitCount = 2L;

    private final String ASSERT_MESSAGE = "Assertion failed";

    @Test
    public void testHourDetails() {

        HourDetails hourDetails = new HourDetails();
        hourDetails.setEntryCount(entryCount);
        hourDetails.setExitCount(exitCount);
        hourDetails.setHour(hour);

        final Long entryCountActual = hourDetails.getEntryCount();
        final Long  exitActual = hourDetails.getExitCount();
        final Integer hourActual = hourDetails.getHour();
        assertEquals(ASSERT_MESSAGE, HourDetailsTest.entryCount, entryCountActual);
        assertEquals(ASSERT_MESSAGE, HourDetailsTest.exitCount, exitActual);
        assertEquals(ASSERT_MESSAGE, HourDetailsTest.hour, hourActual);


    }

    @Test
    public void testEntryCount() {
        EntryExit dayDetails = new HourDetails(hour, entryCount, exitCount);

        final Long testObj = dayDetails.entryCount();
        assertNotNull(ASSERT_MESSAGE, testObj);

    }

    @Test
    public void testExitCount() {
        EntryExit dayDetails = new HourDetails(hour, entryCount, exitCount);
        final Long actual = dayDetails.exitCount();
        assertNotNull(ASSERT_MESSAGE, actual);

    }

    @Test
    public void testToString() {
        HourDetails dayDetails = new HourDetails(hour, entryCount, exitCount);
        final String objectTest = dayDetails.toString();
        assertNotNull(ASSERT_MESSAGE,objectTest);
    }
}
