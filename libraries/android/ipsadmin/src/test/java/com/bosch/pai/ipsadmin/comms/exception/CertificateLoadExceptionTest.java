package com.bosch.pai.ipsadmin.comms.exception;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CertificateLoadException.class})
public class CertificateLoadExceptionTest extends Exception {

    private CertificateLoadException certificateLoadException;
    @Before
    public void init() throws Exception {
        Throwable throwable = null;
        certificateLoadException = new CertificateLoadException("message");
        certificateLoadException = new CertificateLoadException("message", throwable);
    }

    @Test
    public void testFunc(){
        Assert.assertEquals("message",certificateLoadException.getErrMessage());
    }
}
