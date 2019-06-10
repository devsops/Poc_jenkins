package com.bosch.pai.retail.configmodel;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LocationBayMapTest {


    @Test

    public void testLocationBayMapTest(){

        LocationBayMap locationBayMap = new LocationBayMap("test",new ArrayList<String>());
        LocationBayMap locationBayMap1= new LocationBayMap("test",new ArrayList<String>());
         LocationBayMap locationBayMap2 = new LocationBayMap();


        locationBayMap.setLocationCode("test");
        locationBayMap.setBayList(new ArrayList<String>());



        assertEquals("test",locationBayMap.getLocationCode());
        assertEquals(0,locationBayMap.getBayList().size());
        assertNotNull(locationBayMap2);
        assertNotNull(locationBayMap.hashCode());
        assertNotNull(locationBayMap.toString());
        assertTrue(locationBayMap.equals(locationBayMap1));










    }













}
