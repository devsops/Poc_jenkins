package com.bosch.pai.ipsadmin.bearing.core.util;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TrainingData.class})
public class TrainingDataTest {

    private TrainingData trainingData;
    private TrainingData trainingDataMock;

    public TrainingDataTest() throws Exception {
        initMockEnvironment();

        trainingData = TrainingData.getInstance();
    }

    private void initMockEnvironment() throws Exception {
        MockitoAnnotations.initMocks(this);
        trainingDataMock = PowerMockito.mock(TrainingData.class);
        PowerMockito.whenNew(TrainingData.class).withNoArguments().thenReturn(trainingDataMock);
    }






    @Before
    public void initialise() throws Exception {

        trainingData = TrainingData.getInstance();
    }

//    @Test
//    public void clearTrainingDataTest() {
//        trainingData.clearTrainingData();
//        Mockito.verify(trainingDataMock, Mockito.times(1)).clearTrainingData();
//
//    }


    @Test
    public void getTrainDataFileNameAtLocationTestNull() {
        PowerMockito.when(trainingDataMock.getTrainDataFileNameAtLocation(0)).thenReturn(null);
        final String trainDataFileNameAtLocationResult = trainingData.getTrainDataFileNameAtLocation(0);
        Assert.assertNull(trainDataFileNameAtLocationResult);
    }

//    @Test
//    public void getTrainDataFileNameAtLocationTestNonNull() {
//        PowerMockito.when(trainingDataMock.getTrainDataFileNameAtLocation(0)).thenReturn("test");
//        final String trainDataFileNameAtLocationResultFinal = trainingData.getTrainDataFileNameAtLocation(0);
//        Assert.assertNotNull(trainDataFileNameAtLocationResultFinal);
//
//
//    }


}
