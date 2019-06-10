package com.bosch.pai.bearing.core;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Looper;

import com.bosch.pai.bearing.core.operation.ObservationHandlerAndListener;
import com.bosch.pai.bearing.core.operation.RequestDataHolder;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;

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

import java.util.Map;
import java.util.UUID;

import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BearingHandler.class, HandlerThread.class, ObservationHandlerAndListener.class})
public class BearingHandlerTest {

    @Mock
    private Context context;

    @Mock
    private HandlerThread mockHandlerThread;

    @Mock
    private Looper looper;

    @Mock
    private BearingHandler bearingHandlerMock;

    private BearingHandler bearingHandler;

    @Mock
    private ObservationHandlerAndListener observationHandlerAndListener;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(HandlerThread.class);
        PowerMockito.whenNew(BearingHandler.class).withArguments(Context.class).thenReturn(bearingHandlerMock);
        PowerMockito.whenNew(HandlerThread.class).withArguments(String.class).thenReturn(mockHandlerThread);
        PowerMockito.whenNew(ObservationHandlerAndListener.class).withAnyArguments().thenReturn(observationHandlerAndListener);
        PowerMockito.when(mockHandlerThread.getLooper()).thenReturn(looper);
        BearingHandler.init(context);
        bearingHandler = BearingHandler.getInstance();
    }

    @Test
    public void getUuidToRequestDataHolderMapTestNull() {

        final Map<String, RequestDataHolder> uuidToRequestDataHolderMap = bearingHandler.getUuidToRequestDataHolderMap();
        Assert.assertNotNull(uuidToRequestDataHolderMap);
    }

    @Test
    public void addRequestToRequestDataHolderMapValid() {
        RequestDataHolder requestDataHolder = new RequestDataHolder(UUID.randomUUID(),
                RequestDataHolder.ObservationDataType.LOCATION_DATA_RECORD, observationHandlerAndListener);
        Mockito.verify(bearingHandlerMock, times(1)).addRequestToRequestDataHolderMap(requestDataHolder);
    }

    @Test
    public void removeRequestFromRequestDataHolderMapNull(){
        bearingHandlerMock.removeRequestFromRequestDataHolderMap(null, null);
        Mockito.verify(bearingHandlerMock, times(1)).removeRequestFromRequestDataHolderMap(null, null);

    }

    @Test
    public void removeRequestFromRequestDataHolderMapValid(){
        bearingHandlerMock.removeRequestFromRequestDataHolderMap(UUID.randomUUID(), BearingConfiguration.Approach.FINGERPRINT);
        Mockito.verify(bearingHandlerMock, times(1)).removeRequestFromRequestDataHolderMap(UUID.randomUUID(), BearingConfiguration.Approach.FINGERPRINT);

    }
}
