package com.bosch.pai.retail.analytics.responses;

import com.bosch.pai.retail.analytics.model.entryexit.DwellTimeDetails;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserDwellTimeAnalyticsResponseTest {

    @Test

    public void testUserDwellTimeAnalyticsResponse(){

        UserDwellTimeAnalyticsResponse userDwellTimeAnalyticsResponse = new UserDwellTimeAnalyticsResponse();

    userDwellTimeAnalyticsResponse.setSiteName("test");
    userDwellTimeAnalyticsResponse.setHierarchyType("category");
 userDwellTimeAnalyticsResponse.setHierarchyDwellTimeDetails(new ArrayList<DwellTimeDetails>());

assertEquals("test",userDwellTimeAnalyticsResponse.getSiteName());
assertEquals("category",userDwellTimeAnalyticsResponse.getHierarchyType());
assertNotNull(userDwellTimeAnalyticsResponse.getHierarchyDwellTimeDetails());
assertNotNull(userDwellTimeAnalyticsResponse.toString());
}

}
