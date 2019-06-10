package com.bosch.pai.retail.configmodel;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConfigModelTest {




    @Test

    public void testConfigModelTest(){

        ConfigModel configModel = new ConfigModel("test","kor","test1",new HashMap<String, String>());
        ConfigModel configModel1 =new ConfigModel("test","kor","test1",new HashMap<String, String>());

configModel.setCompanyId("test");
configModel.setStoreId("kor");
configModel.setSiteName("test1");
configModel.setSiteConfigMap(new HashMap<String, String>());

        assertEquals("test",configModel.getCompanyId());

        assertEquals("kor",configModel.getStoreId());
        assertEquals("test1",configModel.getSiteName());
        assertEquals(0,configModel.getSiteConfigMap().size());
        assertNotNull(configModel.toString());
        assertNotNull(configModel.hashCode());
        assertTrue(configModel.equals(configModel1));









    }




















}
