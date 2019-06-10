package com.bosch.pai.ipsadmin.bearing.core.operation.detection.location;

import com.bosch.pai.bearing.datamodel.bearingdatamodels.Snapshot;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.restclient.BearingClientCallback;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.restclient.BearingRESTClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ClusterDataDownloader.class, BearingRESTClient.class})
public class ClusterDataDownloaderTest {

    @Mock
    private BearingRESTClient bearingRESTClient;
    @Mock
    private PersistenceHandler persistenceHandler;
    @Mock
    private Thread thread;

    @Before
    public void init() throws Exception {
        final Snapshot snapshot = new Snapshot();
        snapshot.setDocVersion(0L);
        PowerMockito.mockStatic(BearingRESTClient.class);
        PowerMockito.when(BearingRESTClient.getInstance()).thenReturn(bearingRESTClient);
        PowerMockito.whenNew(PersistenceHandler.class).withAnyArguments().thenReturn(persistenceHandler);
        PowerMockito.when(persistenceHandler.readSnapShot(Mockito.anyString())).thenReturn(snapshot);
        PowerMockito.when(persistenceHandler.getDocVersionAvailableLocally(Mockito.anyString())).thenReturn(-1L);
        PowerMockito.whenNew(Thread.class).withAnyArguments().thenReturn(thread);
    }

    @Test
    public void testValidateClusterDataAndDownload() {
        ClusterDataDownloader.validateClusterDataAndDownload("SITE_NAME");
        Mockito.verify(bearingRESTClient, Mockito.times(1)).downloadSiteThreshData(Mockito.anyString(),
                Mockito.any(BearingClientCallback.class));
        Mockito.verify(thread, Mockito.times(1)).start();
    }
}
