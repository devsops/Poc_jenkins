package com.bosch.pai.ipsadmin.bearing.core.util;

import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by MCA7KOR.
 */
public class Helper {
    private static Helper helper;
    private int snapshotSize;


    private Helper() {

    }

    static {
        helper = new Helper();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static Helper getInstance() {
        return helper;
    }

    private List<SnapshotItem> snapShotItems = new LinkedList<>();
    private List<String> recordBuffer = new LinkedList<>();
    private int recordBufferCount = 0;
    private int recordCount = 0;

    /**
     * Gets record buffer.
     *
     * @return the record buffer
     */
    public List<String> getRecordBuffer() {
        return Collections.unmodifiableList(recordBuffer);
    }

    /**
     * Gets record buffer count.
     *
     * @return the record buffer count
     */
    public int getRecordBufferCount() {
        return recordBufferCount;
    }

    /**
     * Add to buffer.
     *
     * @param string the string
     */
    public void addToBuffer(String string) {
        this.recordBuffer.add(string);
    }

    /**
     * Clear buffer.
     */
    public void clearBuffer() {
        this.recordBuffer.clear();
    }

    /**
     * Sets record buffer count.
     *
     * @param recordBufferCount the record buffer count
     */
    public void setRecordBufferCount(int recordBufferCount) {
        this.recordBufferCount = recordBufferCount;
    }

    /**
     * Gets snap shot items.
     *
     * @return the snap shot items
     */
    public List<SnapshotItem> getSnapShotItems() {
        return Collections.unmodifiableList(snapShotItems);
    }

    /**
     * Sets snap shot items.
     *
     * @param snapShotItems the snap shot items
     */
    public void setSnapShotItems(List<SnapshotItem> snapShotItems) {
        this.snapShotItems = removeDuplicates(snapShotItems);
    }

    private List<SnapshotItem> removeDuplicates(List<SnapshotItem> snapShotItems) {
        final List<SnapshotItem> snapshotItems = new LinkedList<>();
        final List<String> tempStrings = new ArrayList<>();
        for (SnapshotItem snapshotItem : snapShotItems) {
            if (!tempStrings.contains(snapshotItem.getSourceId().trim())) {
                tempStrings.add(snapshotItem.getSourceId().trim());
                snapshotItems.add(snapshotItem);
            }
        }
        return snapshotItems;
    }

    /**
     * Gets record count.
     *
     * @return the record count
     */
    public int getRecordCount() {
        return recordCount;
    }

    /**
     * Sets record count.
     *
     * @param recordCount the record count
     */
    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    /**
     * Sets snapshot size.
     *
     * @param snapshotSize the snapshot size
     */
    public void setSnapshotSize(int snapshotSize) {
        this.snapshotSize = snapshotSize;
    }

    /**
     * Gets snapshot size.
     *
     * @return the snapshot size
     */
    public int getSnapshotSize() {
        return snapshotSize;
    }
}
