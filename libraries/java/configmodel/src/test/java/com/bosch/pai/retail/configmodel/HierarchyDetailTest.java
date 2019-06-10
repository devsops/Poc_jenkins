package com.bosch.pai.retail.configmodel;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HierarchyDetailTest {

    @Test


    public void testHierarchyDetail(){


        HierarchyDetail hierarchyDetail = new HierarchyDetail();

        hierarchyDetail.setEntries(new ArrayList<String>());
        hierarchyDetail.setHierarchyLevel(12);
        hierarchyDetail.setHierarchyName("test");
        hierarchyDetail.setRequired(true);

        assertEquals(12,hierarchyDetail.getHierarchyLevel());
        assertEquals("test",hierarchyDetail.getHierarchyName());
        assertEquals(true,hierarchyDetail.getRequired());
        assertEquals(0,hierarchyDetail.getEntries().size());
















    }















}
