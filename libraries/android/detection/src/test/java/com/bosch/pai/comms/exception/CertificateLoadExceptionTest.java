package com.bosch.pai.comms.exception;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CertificateLoadException.class})
public class CertificateLoadExceptionTest {

    private CertificateLoadException certificateLoadException;
    private Throwable cause;

    @Test
    public void init() throws Exception{
        certificateLoadException = new CertificateLoadException("message");
        certificateLoadException = new CertificateLoadException("message", cause);
        Assert.assertEquals("message",certificateLoadException.getErrMessage());
    }
}
