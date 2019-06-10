package com.bosch.pai.ipsadmin.comms.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;

@RunWith(PowerMockRunner.class)
public class CommsUtilTest {
    @Test
    public void testConvertCrtStreamToString() {
        final String testCrtString = "TEST";
        final String got = CommsUtil.convertCrtStreamToString(new ByteArrayInputStream(testCrtString.getBytes()));
        Assert.assertEquals(testCrtString, got);
    }

    @Test
    public void readBASEURLFromUserURLTest(){
        Assert.assertEquals("https://test",CommsUtil.readBASEURLFromUserURL("https://test/gatewayService/"));
        Assert.assertEquals("https://test",CommsUtil.readBASEURLFromUserURL("https://test/registration/"));
    }
}
