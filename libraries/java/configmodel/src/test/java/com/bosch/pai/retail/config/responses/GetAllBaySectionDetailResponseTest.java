package com.bosch.pai.retail.config.responses;

import com.bosch.pai.retail.common.responses.StatusMessage;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GetAllBaySectionDetailResponseTest {



  StatusMessage statusMessage = new StatusMessage();


    @Test

    public void testGetAllBaySectionDetailResponseTest() {

        GetAllBaySectionDetailResponse getAllBaySectionDetailResponse = new GetAllBaySectionDetailResponse();

getAllBaySectionDetailResponse.setBaySectionDetailMap(new HashMap<String, String>());
getAllBaySectionDetailResponse.setStatusMessage(statusMessage);
        assertEquals(statusMessage,getAllBaySectionDetailResponse.getStatusMessage());
        assertEquals(0,getAllBaySectionDetailResponse.getBaySectionDetailMap().size());
assertNotNull(getAllBaySectionDetailResponse.toString());








    }










}


