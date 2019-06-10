package com.bosch.pai.retail.configmodel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StoreConfigTest {

    @Test

    public  void testStoreConfigTest(){

        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setSiteName("test");
        storeConfig.setStoreId("test1");
        storeConfig.setStoreDescription("new store in ban");
        storeConfig.setSnapshotThreshold(12);

        assertEquals("test",storeConfig.getSiteName());
        assertEquals("test1",storeConfig.getStoreId());
        assertEquals("new store in ban",storeConfig.getStoreDescription());


        assertNotNull(storeConfig.getSnapshotThreshold());
        assertNotNull(storeConfig.toString());
         assertNotNull(storeConfig.hashCode());









    }

























}
