package com.bosch.pai.ipswrapper;

import android.content.Context;

import com.bosch.pai.IeroIPSPlatform;
import com.bosch.pai.IeroIPSPlatformListener;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IPSPlatformFactory.class})
public class IPSPlatformFactoryTest {

    @Mock
    private Context context1;

    @Mock
    private IeroIPSPlatformListener callback1;

    @Mock
    private Context context2;

    @Mock
    private IeroIPSPlatformListener callback2;

    @Mock
    private IeroIPSPlatformImpl ieroIPSPlatform;

    @Before
    public void init() throws Exception {
        PowerMockito.whenNew(IeroIPSPlatformImpl.class)
                .withArguments(Mockito.any(Context.class), Mockito.any(IeroIPSPlatformListener.class)).thenReturn(ieroIPSPlatform);
    }

    @Test
    public void testAPI() {
        final IeroIPSPlatform instance1 = IPSPlatformFactory
                .getInstance(context1, IPSPlatformFactory.PlatformType.IERO_IPS_PLATFORM, callback1);
        Assert.assertNotNull(instance1);
        Assert.assertTrue(instance1 instanceof IeroIPSPlatformImpl);

        final IeroIPSPlatform instance2 = IPSPlatformFactory
                .getInstance(context2, IPSPlatformFactory.PlatformType.IERO_IPS_PLATFORM, callback2);
        Assert.assertEquals(instance1, instance2);
    }
}
