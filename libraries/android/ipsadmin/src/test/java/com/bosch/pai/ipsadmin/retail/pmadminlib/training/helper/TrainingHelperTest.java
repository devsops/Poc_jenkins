package com.bosch.pai.ipsadmin.retail.pmadminlib.training.helper;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.ScannedBleDetails;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TrainingHelper.class})
public class TrainingHelperTest {

    private List<SnapshotItem> snapshotItems = new ArrayList<>();
    private List<SnapshotObservation> snapshotObservations = new ArrayList<>();

    private List<ScannedBleDetails> scannedBleDetails = new ArrayList<>();

    private Set<String> selectedIds = new HashSet<>();

    private List<SnapshotItem> snapshotItems1 = new ArrayList<>();
    private List<SnapshotObservation> snapshotObservations1 = new ArrayList<>();

    private List<ScannedBleDetails> scannedBleDetails1 = new ArrayList<>();

    private Set<String> selectedIds1 = new HashSet<>();

    @Before
    public void init(){
        SnapshotItem snapshotItem = new SnapshotItem();
        snapshotItem.setSourceId("sample");
        double[] threshValueList = new double[1];
        threshValueList[0] = 5.5;
        snapshotItem.setMeasuredValues(threshValueList);
        snapshotItems.add(snapshotItem);

        SnapshotObservation snapshotObservation = new SnapshotObservation();
        snapshotObservation.setSensorType(BearingConfiguration.SensorType.ST_BLE);
        snapshotObservation.setDetectionLevel(BearingConfiguration.DetectionLevel.INTERMEDIATE);
        snapshotObservation.setSnapShotItemList(snapshotItems);
        snapshotObservations.add(snapshotObservation);

        ScannedBleDetails scannedBleDetail = new ScannedBleDetails();
        scannedBleDetail.setBleId("sample");
        scannedBleDetail.setBleRssi(5.5);
        scannedBleDetails.add(scannedBleDetail);

        selectedIds.add("sample");
    }

    @Test
    public void getBLESnapshotItemsTest(){
        Assert.assertEquals(snapshotItems, TrainingHelper.getBLESnapshotItems(snapshotObservations));
        Assert.assertEquals(snapshotItems1, TrainingHelper.getBLESnapshotItems(snapshotObservations1));
    }

    @Test
    public void getBLESourceIdsAsListTest(){
        Assert.assertNotEquals(scannedBleDetails, TrainingHelper.getBLESourceIdsAsList(snapshotItems));
        Assert.assertEquals(scannedBleDetails1, TrainingHelper.getBLESourceIdsAsList(snapshotItems1));
    }

    @Test
    public void matchesRSSIThresholdFormatTest(){
        Assert.assertTrue(TrainingHelper.matchesRSSIThresholdFormat(-5.5));
    }

    @Test
    public void updateSnapshotObservationsWithSelectedIdsTest(){
        Assert.assertEquals(snapshotObservations,TrainingHelper.updateSnapshotObservationsWithSelectedIds(selectedIds, snapshotObservations));
        Assert.assertEquals(snapshotObservations1,TrainingHelper.updateSnapshotObservationsWithSelectedIds(selectedIds1, snapshotObservations1));
    }

    @Test
    public void getBLESnapshotItemListForThresholdTest(){
        Assert.assertEquals(snapshotItems,TrainingHelper.getBLESnapshotItemListForThreshold("sample",5.5));
    }
}
