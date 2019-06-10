
package com.bosch.pai.retail.analytics.model.entryexit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;



public class YearsDetailsTest {
    private final String ASSERT_MESSAGE = "Assertion failed";
    private YearsDetails yearsDetails;

    private final Integer year = 3;
    private final Long entryCount = 5L;
    private final Long exitCount = 2L;
    private final List<MonthsDetails> months = new ArrayList<MonthsDetails>();

    @Before
    public void setUpBeforeEachTest() {
        yearsDetails = new YearsDetails(year,
                entryCount, exitCount, months);
    }


    @Test
    public void testYear() {

        final Integer actualYear = yearsDetails.getYear();

        Assert.assertNotNull(ASSERT_MESSAGE, actualYear);
        Assert.assertEquals(ASSERT_MESSAGE, year, actualYear);

    }

    @Test
    public void testMonth() {
        yearsDetails.setMonths(months);
        final List<MonthsDetails> actualMonth = yearsDetails.getMonths();

        Assert.assertNotNull(ASSERT_MESSAGE, actualMonth);
        Assert.assertEquals(ASSERT_MESSAGE, months, actualMonth);

    }

    @Test
    public void testEntryCount() {

        final Long actualCount = yearsDetails.getEntryCount();

        Assert.assertNotNull(ASSERT_MESSAGE, actualCount);

        Assert.assertEquals(ASSERT_MESSAGE, entryCount, actualCount);
    }

    @Test
    public void testExitCount() {

        final Long actualCount = yearsDetails.getExitCount();

        Assert.assertNotNull(ASSERT_MESSAGE, actualCount);
        Assert.assertEquals(ASSERT_MESSAGE, exitCount, actualCount);

    }

    @Test
    public void testEntry() {
        YearsDetails yearsDetail = new YearsDetails();
        yearsDetail.setYear(3);
        yearsDetail.setEntryCount(entryCount);
        yearsDetail.setExitCount(exitCount);
        final Long allEntryCount = yearsDetail.entryCount();
        Assert.assertEquals(ASSERT_MESSAGE, entryCount, allEntryCount);
    }

    @Test
    public void testExit() {
        YearsDetails yearsDetail = new YearsDetails();
        yearsDetail.setYear(3);
        yearsDetail.setEntryCount(entryCount);
        yearsDetail.setExitCount(exitCount);
        final Long allExitCount = yearsDetail.exitCount();
        Assert.assertEquals(ASSERT_MESSAGE, exitCount, allExitCount);
    }

    @Test
    public void testToString() {
        YearsDetails yearsDetail = new YearsDetails();
        yearsDetail.setYear(3);
        final String testString = yearsDetail.toString();
        Assert.assertNotNull(ASSERT_MESSAGE, testString);

    }
}