package com.bosch.pai.session;

import org.junit.Test;

public class SubSessionInfoTest {

    private SubSessionInfo subSessionInfo = new SubSessionInfo("userId", "siteName", "locationName");

    @Test
    public void tSubSessionInfoTest(){
        subSessionInfo.setEndTime(10210);
        subSessionInfo.setStoreId("storeId");
        subSessionInfo.setValid(false);
        subSessionInfo.setStartTime(10210);
    }
}
