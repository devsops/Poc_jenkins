package com.bosch.pai.ipsadmin.retail.pmadminlib.common;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MessageHandlerExceptionTest.class})
public class MessageHandlerExceptionTest {

    @Test
    public void init() throws Exception {
        MessageHandlerException messageHandlerException = new MessageHandlerException("message");
        Assert.assertNotNull(messageHandlerException.getMessage());
    }
}
