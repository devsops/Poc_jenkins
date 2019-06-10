package com.bosch.pai.retail.db.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DBUtilTest {

    @Test

    public void DBUtilTest(){


        String collection = DBUtil.getCollectionName("test","kor","category");
        assertEquals("test_kor_category",collection);

        String collection2 = DBUtil.getCollectionName("test","new");
        assertEquals("test_new",collection2);






    }








}
