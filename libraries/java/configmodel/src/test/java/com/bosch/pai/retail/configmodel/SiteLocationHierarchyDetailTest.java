package com.bosch.pai.retail.configmodel;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SiteLocationHierarchyDetailTest {



    @Test


    public  void testSiteLocationHierarchyDetailTest(){

                SiteLocationHierarchyDetail siteLocationHierarchyDetail = new SiteLocationHierarchyDetail("test","kor","test1","ban",new ArrayList<HierarchyDetail>());
        SiteLocationHierarchyDetail siteLocationHierarchyDetail2 = new SiteLocationHierarchyDetail("test","kor","test1","ban",new ArrayList<HierarchyDetail>());


        siteLocationHierarchyDetail.setCompanyId("test");
                siteLocationHierarchyDetail.setStoreId("kor");
                siteLocationHierarchyDetail.setLocationName("ban");
                siteLocationHierarchyDetail.setSiteName("test1");
                siteLocationHierarchyDetail.setHierarchies(new ArrayList<HierarchyDetail>());


        assertEquals("test",siteLocationHierarchyDetail.getCompanyId());
        assertEquals("kor",siteLocationHierarchyDetail.getStoreId());
        assertEquals("ban",siteLocationHierarchyDetail.getLocationName());
        assertEquals("test1",siteLocationHierarchyDetail.getSiteName());
        assertEquals(0,siteLocationHierarchyDetail.getHierarchies().size());
        assertTrue(siteLocationHierarchyDetail.equals(siteLocationHierarchyDetail2));
        assertNotNull(siteLocationHierarchyDetail.toString());
        assertNotNull(siteLocationHierarchyDetail.hashCode());




    }



















}
