package com.bosch.pai.retail.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MapLabelDetailTest {

    @Test
    public void testMapLabelDetailTest(){

        MapLabelDetail mapLabelDetail = new MapLabelDetail();


        mapLabelDetail.setCategoryCode("new");
        mapLabelDetail.setCategoryName("test");
        mapLabelDetail.setMapLabel("xyz");
        mapLabelDetail.setMapLabelId("121");

        assertEquals("new",mapLabelDetail.getCategoryCode());
       assertEquals("test",mapLabelDetail.getCategoryName());
assertEquals("xyz",mapLabelDetail.getMapLabel());
assertEquals("121",mapLabelDetail.getMapLabelId());

        assertNotNull(mapLabelDetail.toString());





    }














}
