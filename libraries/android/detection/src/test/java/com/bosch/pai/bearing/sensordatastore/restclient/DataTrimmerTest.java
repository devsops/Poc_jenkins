package com.bosch.pai.bearing.sensordatastore.restclient;

import com.bosch.pai.bearing.persistence.StorageType;
import com.bosch.pai.comms.CommsManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataTrimmer.class, CommsManager.class})
public class DataTrimmerTest {

    private DataTrimmer dataTrimmer;
    @Mock
    CommsManager commsManager;
    @Mock
    private BearingClientCallback callback;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(CommsManager.class);
        dataTrimmer = new DataTrimmer(commsManager);
        final String crtStr = "SAMPLE_STRING";
        dataTrimmer.setCertificateStream(crtStr);
    }

    @Test
    public void deleteSiteDataTest(){
        dataTrimmer.deleteSiteData("siteName", callback);
    }

    @Test
    public void deleteLocationDataTest(){
        final StorageType storageType = null;
        dataTrimmer.deleteLocationData("siteName", "locationName", storageType, callback);
    }
}
