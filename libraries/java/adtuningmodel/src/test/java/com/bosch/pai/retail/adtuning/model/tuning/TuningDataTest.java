package com.bosch.pai.retail.adtuning.model.tuning;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TuningDataTest {


@Test
    public void testTuningDataTest(){


    TuningData tuningData = new TuningData(2,6,45,12);


    tuningData.setAdCount(2);
    tuningData.setEnd(6);
    tuningData.setIndex(45);
    tuningData.setStart(12);


    assertEquals(2,tuningData.getAdCount());
    assertEquals(6,tuningData.getEnd());
    assertEquals(45,tuningData.getIndex());
    assertEquals(12,tuningData.getStart());

    assertNotNull(tuningData.toString());
    assertNotNull(tuningData.hashCode());





}








}
