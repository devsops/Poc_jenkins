package com.bosch.pai.ipsadmin.bearing.core.operation;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.core.BearingCallBack;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BearingConfiguration.OperationType.class,BearingCallBack.class,BearingConfiguration.Approach.class})
public class BearingCallbackOperationTest {

    BearingCallbackOperation bearingCallbackOperation;

    @Mock
    BearingConfiguration.OperationType operationType;

    @Mock
    BearingCallBack bearingCallback;

    @Mock
    BearingConfiguration.Approach approach;

    /*private final BearingConfiguration.OperationType operationType;
    private final BearingCallBack bearingCallback;
    private final BearingConfiguration.Approach approach;*/

    @Before
    public void testBefore()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetters()
    {
       bearingCallbackOperation = new BearingCallbackOperation(operationType,approach,bearingCallback);
       assertEquals(approach,bearingCallbackOperation.getApproach());
       assertEquals(bearingCallback,bearingCallbackOperation.getBearingCallback());
       assertEquals(operationType,bearingCallbackOperation.getOperationType());
    }

}
