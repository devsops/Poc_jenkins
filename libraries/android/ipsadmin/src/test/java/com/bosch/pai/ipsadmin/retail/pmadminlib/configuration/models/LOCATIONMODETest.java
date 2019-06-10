package com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.models;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LOCATIONMODE.class)
public class LOCATIONMODETest {


    @Before
    public void init() throws Exception {

    }

    @Test
    public void checkTest(){
        Assert.assertEquals(LOCATIONMODE.BRAND,LOCATIONMODE.BRAND);
    }


}
