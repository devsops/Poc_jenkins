package com.bosch.pai.retail.config.responses;

import com.bosch.pai.retail.common.responses.StatusMessage;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GetBaysForSectionResponseTest {
    StatusMessage statusMessage = new StatusMessage();
   @Test

    public void testGetBaysForSectionResponseTest(){
       GetBaysForSectionResponse getBaysForSectionResponse = new GetBaysForSectionResponse();
       GetBaysForSectionResponse getBaysForSectionResponse1 = new GetBaysForSectionResponse();

       getBaysForSectionResponse.setStatusMessage(statusMessage);
       getBaysForSectionResponse.setBays(new HashSet<String>());
       assertEquals(statusMessage,getBaysForSectionResponse.getStatusMessage());
       assertEquals(0,getBaysForSectionResponse.getBays().size());
       assertNotNull(getBaysForSectionResponse1);

       assertNotNull(getBaysForSectionResponse.toString());













   }


















}
