package com.bosch.pai.comms.config;

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

        final String absolutePath = "/temp";
        Assert.assertTrue(config.getHostURL().equals(configUsingBuilder.getHostURL()));
        config.setAbsoluteFilePath(absolutePath);
        configUsingBuilder.setAbsoluteFilePath(absolutePath);
        Assert.assertTrue(absolutePath.equals(config.getAbsoluteFilePath()));
        Assert.assertTrue(absolutePath.equals(configUsingBuilder.getAbsoluteFilePath()));
    }
}
