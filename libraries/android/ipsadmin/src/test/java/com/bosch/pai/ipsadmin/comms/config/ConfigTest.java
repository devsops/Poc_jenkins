package com.bosch.pai.ipsadmin.comms.config;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConfigTest {

    @Test
    public void testConstructorAndGetterSetter() {
        final Config config = new Config();
        final Config configUsingBuilder = new Config.Builder().create();
        Config.Builder builder = new Config.Builder();
        builder.setHostURL(config.getHostURL());

        final String absolutePath = "/temp";
        org.junit.Assert.assertEquals(config.getHostURL(), configUsingBuilder.getHostURL());
        config.setAbsoluteFilePath(absolutePath);
        configUsingBuilder.setAbsoluteFilePath(absolutePath);
        org.junit.Assert.assertEquals(absolutePath, config.getAbsoluteFilePath());
        org.junit.Assert.assertEquals(absolutePath, configUsingBuilder.getAbsoluteFilePath());
    }
}
