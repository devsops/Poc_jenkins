package com.bosch.pai.ipsadmin.retail.pmadminlib.training.helper;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.ScannedBleDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by sjn8kor on 5/30/2018.
 */

public class TrainingHelper {

    private TrainingHelper() {

    }

    public static List<SnapshotItem> getBLESnapshotItems(List<SnapshotObservation> snapshotObservations) {
        final List<SnapshotItem> snapshotItems = new ArrayList<>();
        if (snapshotObservations == null || snapshotObservations.isEmpty())
            return snapshotItems;
        for (SnapshotObservation snapshotObservation : snapshotObservations) {
            if (BearingConfiguration.SensorType.ST_BLE == snapshotObservation.getSensorType()) {
                snapshotItems.addAll(snapshotObservation.getSnapShotItemList());
                return snapshotItems;
            }
        }
        return snapshotItems;
    }

  /*  public static List<String> getBLESourceIdsAsList(List<SnapshotItem> bleSnapshotItems) {
        if (bleSnapshotItems == null || bleSnapshotItems.isEmpty())
            return new ArrayList<>();
        final List<String> strings = new ArrayList<>();
        for (SnapshotItem snapshotItem : bleSnapshotItems) {
            strings.add(snapshotItem.getSourceId());


            snapshotItem.getMeasuredValues();
        }
        return strings;
    }*/

    public static List<ScannedBleDetails> getBLESourceIdsAsList(List<SnapshotItem> bleSnapshotItems) {
        if (bleSnapshotItems == null || bleSnapshotItems.isEmpty())
            return new ArrayList<>();
        final List<ScannedBleDetails> strings = new ArrayList<>();
        for (SnapshotItem snapshotItem : bleSnapshotItems) {


            String sourceId = snapshotItem.getSourceId();
            double v = snapshotItem.getMeasuredValues()[0];


            strings.add(new ScannedBleDetails(sourceId,v));
        }
        return strings;
    }

    public static boolean matchesRSSIThresholdFormat(double i) {
        return i < 0 && i > -100;
    }

    public static List<SnapshotObservation> updateSnapshotObservationsWithSelectedIds(Set<String> selectedIds, List<SnapshotObservation> snapshotObservations) {
        if (snapshotObservations == null || snapshotObservations.isEmpty())
            return Collections.emptyList();
        final List<SnapshotItem> snapshotItems = getBLESnapshotItems(snapshotObservations);
        final List<SnapshotObservation> list = new ArrayList<>(snapshotObservations);
        final List<SnapshotItem> items = new ArrayList<>();
        for (String s : selectedIds) {
            for (SnapshotItem sI : snapshotItems) {
                if (s.trim().equals(sI.getSourceId().trim())) {
                    items.add(sI);
                }
            }
        }
        list.get(0).setSnapShotItemList(items);
        return list;
    }

    public static List<SnapshotItem> getBLESnapshotItemListForThreshold(String bleId, double bleThreshold) {
        final List<SnapshotItem> snapshotList = new ArrayList<>();

        final SnapshotItem snapshotItem = new SnapshotItem();
        snapshotItem.setSourceId(bleId);
        double[] threshValueList = new double[1];
        threshValueList[0] = bleThreshold;
        snapshotItem.setMeasuredValues(threshValueList);

        snapshotList.add(snapshotItem);

        return snapshotList;
    }
}
