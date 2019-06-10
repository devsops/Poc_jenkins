package com.bosch.pai.retail.db.model;

import com.bosch.pai.retail.adtuning.model.offer.OfferResponseStatus;

import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OfferResponseDetailTest {

    // offerResponseStatus = new OfferResponseStatus(OfferResponseStatus.STATUS.valueOf("accepted"));
  @Test

    public  void testOfferResponseDetailTest(){


      OfferResponseDetail offerResponseDetail = new OfferResponseDetail();

      offerResponseDetail.setLocationName("ban");
      offerResponseDetail.setMessageCode("sucess");
      offerResponseDetail.setOfferActiveDuration(12L);
      offerResponseDetail.setOfferResponseId("accepted");
      offerResponseDetail.setUserId("test");
      offerResponseDetail.setSiteName("kor");
    //  offerResponseDetail.setOfferResponseStatus(offerResponseStatus);
      offerResponseDetail.setUserResponseTimeStamp(new Timestamp(13));

      assertEquals("ban",offerResponseDetail.getLocationName());
      assertEquals("sucess",offerResponseDetail.getMessageCode());
     assertEquals("accepted",offerResponseDetail.getOfferResponseId());
     assertEquals("test",offerResponseDetail.getUserId());
     assertEquals("kor",offerResponseDetail.getSiteName());
    // assertEquals(offerResponseStatus,offerResponseDetail.getOfferResponseStatus());
     assertEquals(new Timestamp(13),offerResponseDetail.getUserResponseTimeStamp());
      assertNotNull(offerResponseDetail.getOfferActiveDuration());

















  }




}
