package com.bosch.pai.util;

import com.bosch.pai.bearing.core.util.Constants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Constants.class)
public class ConstantTest {

    private static final String DEV_SERVER_URL = "https://prod1.bosch-iero.com";

    @Before
    public void testBeforeSetUp()
    {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testConstants()
    {
        Assert.assertEquals(DEV_SERVER_URL,Constant.getServerUrl());
    }
}
