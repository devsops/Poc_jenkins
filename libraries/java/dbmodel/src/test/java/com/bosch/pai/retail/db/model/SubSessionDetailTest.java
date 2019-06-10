package com.bosch.pai.retail.db.model;

import com.bosch.pai.retail.configmodel.HierarchyDetail;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SubSessionDetailTest {



    @Test

    public void testSubSessionDetailTest(){


        SubSessionDetail subSessionDetail = new SubSessionDetail();


        subSessionDetail.setEndTime(10l);
        subSessionDetail.setIsValid(true);
        subSessionDetail.setLocationName("ban");
        subSessionDetail.setSiteName("kor");
        subSessionDetail.setUserId("test");
        subSessionDetail.setStartTime(23l);
        subSessionDetail.setStoreId("demo");
        subSessionDetail.setSessionId("test1");
        subSessionDetail.setSubSessionId("test1A");
        subSessionDetail.setHierarchyDetails(new ArrayList<HierarchyDetail>());


        assertEquals(true,subSessionDetail.getIsValid());

        assertEquals("ban",subSessionDetail.getLocationName());
        assertEquals("kor",subSessionDetail.getSiteName());
        assertEquals("test",subSessionDetail.getUserId());

        assertEquals("demo",subSessionDetail.getStoreId());

        assertEquals("test1",subSessionDetail.getSessionId());
        assertEquals("test1A",subSessionDetail.getSubSessionId());

        assertNotNull(subSessionDetail.getEndTime());
        assertNotNull(subSessionDetail.getStartTime());
        assertNotNull(subSessionDetail.getHierarchyDetails());












    }


















}
