package com.bosch.pai.retail.config.responses;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.configmodel.StoreConfig;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StoreConfigResponseTest {


    StatusMessage statusMessage = new StatusMessage();
    @Test

    public void testStoreConfigResponseTest(){


        StoreConfigResponse storeConfigResponse = new StoreConfigResponse();


        storeConfigResponse.setStatusMessage(statusMessage);
        storeConfigResponse.setStoreConfig(new ArrayList<StoreConfig>());

        assertEquals(0,storeConfigResponse.getStoreConfig().size());
        assertEquals(statusMessage,storeConfigResponse.getStatusMessage());
         assertNotNull(storeConfigResponse.toString());











    }






















}
