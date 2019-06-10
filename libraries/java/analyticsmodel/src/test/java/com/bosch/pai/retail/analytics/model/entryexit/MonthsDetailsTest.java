
package com.bosch.pai.retail.analytics.model.entryexit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;






public class MonthsDetailsTest {

    private final Integer month = 12;
    private final Long entryCount = 5L;
    private final Long exitCount = 2L;
    private final List<DayDetails> days = new ArrayList<>();
    private final List<HourDetails> hours = new ArrayList<>();
    private final String ASSERT_MESSAGE = "Assertion failed";


    @Test
    public void testMonthsDetails() {

        MonthsDetails monthsDetails = new MonthsDetails(month, entryCount, exitCount, days);

        final String objectTest = monthsDetails.toString();
        assertNotNull(ASSERT_MESSAGE, objectTest);


        final Integer monthActual = monthsDetails.getMonth();
        final Long entryActual = monthsDetails.getEntryCount();
        final Long exitActual = monthsDetails.getExitCount();
        final List<DayDetails> daysActual = monthsDetails.getDays();
        final Long aLongEntryActual = monthsDetails.entryCount();
        final Long aLongExitActual = monthsDetails.exitCount();
        assertEquals(ASSERT_MESSAGE, this.entryCount, aLongEntryActual);
        assertEquals(ASSERT_MESSAGE, this.exitCount, aLongExitActual);

        assertEquals(ASSERT_MESSAGE, this.exitCount, exitActual);
        assertEquals(ASSERT_MESSAGE, this.entryCount, entryActual);
        assertEquals(ASSERT_MESSAGE, this.month, monthActual);
        assertEquals(ASSERT_MESSAGE, this.days, daysActual);


    }



    @Test
    public void testEntryCount() {
        MonthsDetails monthsDetails = new MonthsDetails();
        monthsDetails.setEntryCount(entryCount);
        monthsDetails.setExitCount(exitCount);
        monthsDetails.setMonth(month);

        Integer day_day = 1;
        Long entryCountDay = 3L;
        Long exitCountDay = 2L;
        DayDetails dayDetails = new DayDetails(day_day, entryCountDay, exitCountDay, hours);
        days.add(dayDetails);
        monthsDetails.setDays(days);
        final Long objectTest = monthsDetails.entryCount();
        assertNotNull(ASSERT_MESSAGE, objectTest);

    }

    @Test
    public void testExitCount() {
        MonthsDetails monthsDetails = new MonthsDetails();
        monthsDetails.setEntryCount(entryCount);
        monthsDetails.setExitCount(exitCount);
        monthsDetails.setMonth(month);

        Integer day_day = 1;
        Long entryCountDay = 3L;
        Long exitCountDay = 2L;
        DayDetails dayDetails = new DayDetails(day_day, entryCountDay, exitCountDay, hours);
        days.add(dayDetails);
        monthsDetails.setDays(days);
        final Long objectTest = monthsDetails.exitCount();
        assertNotNull(ASSERT_MESSAGE, objectTest);
    }
}
