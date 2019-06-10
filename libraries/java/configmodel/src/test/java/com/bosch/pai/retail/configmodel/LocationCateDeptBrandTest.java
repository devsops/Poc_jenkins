package com.bosch.pai.retail.configmodel;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LocationCateDeptBrandTest {

    @Test

    public  void testLocationCateDeptBrandTest(){

        LocationCateDeptBrand locationCateDeptBrand = new LocationCateDeptBrand("test",new HashSet<String>(),new HashSet<String>(),new HashSet<String>(),new HashSet<String>());

        LocationCateDeptBrand locationCateDeptBrand2 = new LocationCateDeptBrand("test",new HashSet<String>(),new HashSet<String>(),new HashSet<String>(),new HashSet<String>());
        LocationCateDeptBrand locationCateDeptBrand3 = new LocationCateDeptBrand();

        locationCateDeptBrand.setLocationType("test");
        locationCateDeptBrand.setLocationBrands(new HashSet<String>());
        locationCateDeptBrand.setLocationCateDeptBrands(new HashSet<String>());
        locationCateDeptBrand.setLocationCategorys(new HashSet<String>());
        locationCateDeptBrand.setLocationDepartments(new HashSet<String>());




        assertEquals("test",locationCateDeptBrand.getLocationType());
        assertEquals(0,locationCateDeptBrand.getLocationBrands().size());
        assertEquals(0,locationCateDeptBrand.getLocationCategorys().size());
        assertEquals(0,locationCateDeptBrand.getLocationDepartments().size());
        assertEquals(0,locationCateDeptBrand.getLocationCateDeptBrands().size());
        assertNotNull(locationCateDeptBrand.toString());
        assertNotNull(locationCateDeptBrand.hashCode());
        assertNotNull(locationCateDeptBrand);

        assertTrue(locationCateDeptBrand.equals(locationCateDeptBrand2));














    }

























}
