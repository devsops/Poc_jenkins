package com.bosch.pai.retail.analytics.model.entryexit;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;

public class EntryExitDetailsTest {

    @Test

    public void testEntryExitDetailsTest(){

        EntryExitDetails entryExitDetails = new EntryExitDetails();

        entryExitDetails.setYears(new ArrayList<YearsDetails>());

        assertNotNull(entryExitDetails.toString());
        assertNotNull(entryExitDetails.entryCount());
        assertNotNull(entryExitDetails.exitCount());





    }
















}
