package com.bosch.pai.retail.analytics.model.entryexit;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class IntervalDetailsTest {

    @Test

    public void TestIntervalDetails(){

        assertNotNull(IntervalDetails.DAILY);
        assertNotNull(IntervalDetails.HOURLY);
        assertNotNull(IntervalDetails.MONTHLY);
        assertNotNull(IntervalDetails.YEARLY);




    }



}
