package com.bosch.pai.ipsadmin.bearing.sensordatastore.util;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.Snapshot;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.entity.Site;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ClientUtil.class, ConfigurationSettings.class, LogAndToastUtil.class})
public class ClientUtilTest {

    @Mock
    private ConfigurationSettings configurationSettings;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(ConfigurationSettings.class);
        PowerMockito.mockStatic(LogAndToastUtil.class);
        PowerMockito.when(ConfigurationSettings.getConfiguration()).thenReturn(configurationSettings);
    }

    @Test
    public void getPropertyTest() throws IOException {
        Assert.assertNull(ClientUtil.getProperty("key"));
    }

    @Test
    public void createServerSiteFromTrainedSiteDataTest(){
        double[] measuredValues = {0.1,0.2};
        String[] customField = {"a", "b"};
        List<SnapshotObservation> sensors = new ArrayList<>();
        final SnapshotObservation snapshotObservation = new SnapshotObservation();
        final SnapshotItem snapshotItem = new SnapshotItem();
        snapshotItem.setSourceId("sourceId");
        snapshotItem.setCustomField(customField);
        snapshotItem.setMeasuredValues(measuredValues);
        final List<SnapshotItem> snapShotItemList = new ArrayList<>();
        snapShotItemList.add(snapshotItem);
        snapshotObservation.setSensorType(BearingConfiguration.SensorType.ST_BLE);
        snapshotObservation.setDetectionLevel(BearingConfiguration.DetectionLevel.MACRO);
        snapshotObservation.setSnapShotItemList(snapShotItemList);
        sensors.add(snapshotObservation);
        long docVersion = (long) 0.56;
        Snapshot snapshot = new Snapshot();
        snapshot.setDocVersion(docVersion);
        snapshot.setNoOfFloors(6);
        snapshot.setProbabilityPercentage(60);
        snapshot.setRssiThreshHold(87);
        snapshot.setSchemaVersion("schemaVersion");
        snapshot.setTimeStamp("121654");
        snapshot.setSensors(sensors);
        Site site = ClientUtil.createServerSiteFromTrainedSiteData("SiteName", snapshot);
        Assert.assertEquals("SiteName", site.getSiteName());
    }
}
