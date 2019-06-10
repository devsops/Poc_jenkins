package com.bosch.pai.bearing.core.util;

import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Helper.class,Collections.class})
public class HelperTest {


    private Helper helper;

    @Before
    public void testBefore()
    {
        helper = Helper.getInstance();
    }

    @Test
    public void testStaticMethods()
    {
        List<String> getRecordBuffers = helper.getRecordBuffer();
        assertNotNull(getRecordBuffers);
        helper.addToBuffer("Sample");
        Assert.assertTrue(!helper.getRecordBuffer().isEmpty());
        helper.setRecordBufferCount(1);
        Assert.assertEquals(1,  helper.getRecordBufferCount());

        final SnapshotItem snapshotItem1 = new SnapshotItem();
        snapshotItem1.setSourceId("SOURCE_ID");
        snapshotItem1.setMeasuredValues(new double[]{});
        snapshotItem1.setCustomField(new String[]{});
        final List<SnapshotItem> snapshotItems1 = new ArrayList<>();
        snapshotItems1.add(snapshotItem1);
        helper.setSnapShotItems(snapshotItems1);

        Assert.assertEquals(snapshotItems1, helper.getSnapShotItems());
        final List<SnapshotItem> snapshotItems2 = new ArrayList<>(snapshotItems1);
        snapshotItems2.add(snapshotItem1);
        helper.setSnapShotItems(snapshotItems2);
        Assert.assertEquals(1, helper.getSnapShotItems().size());

    }

    @Test
    public void setAndGetAndCountTest(){
        helper.setRecordCount(2);
        helper.setSnapshotSize(2);
        Assert.assertEquals(2,helper.getRecordCount());
        Assert.assertEquals(2,helper.getSnapshotSize());
        helper.clearBuffer();
    }


}
