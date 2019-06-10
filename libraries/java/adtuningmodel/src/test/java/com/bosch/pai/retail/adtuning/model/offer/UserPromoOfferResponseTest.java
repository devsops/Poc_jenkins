package com.bosch.pai.retail.adtuning.model.offer;

import com.bosch.pai.retail.configmodel.HierarchyDetail;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserPromoOfferResponseTest {


    @Test

    public void testUserPromoOfferResponse (){

          UserPromoOfferResponse  userPromoOfferResponse = new UserPromoOfferResponse();



        userPromoOfferResponse.setSiteName("lab");
        userPromoOfferResponse.setLocationName("pai");
        userPromoOfferResponse.setStoreId("123");
        userPromoOfferResponse.setMessageCode("589");
        userPromoOfferResponse.setUserId("testuser");
        //userPromoOfferResponse.setHierarchyDetails(new List<HierarchyDetail>());
        userPromoOfferResponse.setOfferActiveDuration(12L);
        userPromoOfferResponse.setUserResponseTimeStamp(10L);


        assertEquals("lab",userPromoOfferResponse.getSiteName());
        assertEquals("pai",userPromoOfferResponse.getLocationName());
        assertEquals("123",userPromoOfferResponse.getStoreId());
        assertEquals("589",userPromoOfferResponse.getMessageCode());
        assertEquals("testuser",userPromoOfferResponse.getUserId());
        assertNotNull(userPromoOfferResponse.getOfferActiveDuration());
        assertNotNull(userPromoOfferResponse.getUserResponseTimeStamp());

        assertNotNull(userPromoOfferResponse.toString());




    }







}
