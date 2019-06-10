package com.bosch.pai.ipsadmin.retail.pmadminlib.training.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.config.Sensor;
import com.bosch.pai.bearing.core.BearingCallBack;
import com.bosch.pai.bearing.datamodel.BearingOutput;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.apimodels.output.body.Body;
import com.bosch.pai.bearing.datamodel.apimodels.output.body.DetectionDataForApproach;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.Header;
import com.bosch.pai.bearing.datamodel.apimodels.output.header.StatusCode;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.bearing.enums.Property;
import com.bosch.pai.ipsadmin.bearing.train.BearingTrainer;
import com.bosch.pai.ipsadmin.retail.pmadminlib.Util;
import com.bosch.pai.ipsadmin.retail.pmadminlib.common.CommonUtil;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.Training;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.callback.IBearingTrainingCallback;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.config.TrainingConfig;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.BearingSitedetails;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.SnapshotItemWithSensorType;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BearingTrainingImpl.class, Training.class, Context.class, TrainingConfig.class, Handler.class,
        CommonUtil.class, Util.class, BearingTrainer.class,ConfigurationSettings.class,Log.class})
public class BearingTrainingImplTest {

    @Mock
    private Training training;
    @Mock
    private Context context;
    @Mock
    private TrainingConfig trainingConfig;
    @Mock
    private InputStream inputStream;
    @Mock
    private IBearingTrainingCallback.IBearingSetServerEndpointListener iBearingTrainingCallback;
    @Mock
    private IBearingTrainingCallback.ITrainsite iTrainsite;
    @Mock
    private IBearingTrainingCallback.IBearingSiteSignalMergeListener iBearingSiteSignalMergeListener;
    @Mock
    private IBearingTrainingCallback.IBearingOnLocationTrainAndRetrain iBearingOnLocationTrainAndRetrain;
    @Mock
    private IBearingTrainingCallback.IBearingOnUpload iBearingOnUpload;
    @Mock
    private IBearingTrainingCallback.IBearingSuncWithServerListener iBearingSuncWithServerListener;
    @Mock
    private IBearingTrainingCallback.IUtilityGetSiteListListenerFromServer iUtilityGetSiteListListenerFromServer;
    @Mock
    private IBearingTrainingCallback.IDownloadSiteLocationsListener iDownloadSiteLocationsListener;
    @Mock
    private IBearingTrainingCallback.IUtilityGetLocationListListenerFromServer iUtilityGetLocationListListenerFromServer;
    @Mock
    private IBearingTrainingCallback.IBearingDataDelete iBearingDataDelete;
    @Mock
    private Handler handler;
    @Mock
    private BearingTrainer bearingTrainer;

    private BearingOutput bearingOutput;
    @Mock
    private ConfigurationSettings configurationSettings;
    @Mock
    private Sensor sensor;

    private BearingTrainingImpl bearingTraining;
    private List<DetectionDataForApproach> locationDetectionOutput = new ArrayList<>();
    private List<String> responseList = new ArrayList<>();
    private List<SnapshotObservation> snapshotObservations = new ArrayList<>();
    private List<SnapshotItemWithSensorType> snapshotItemWithSensorTypeList = new ArrayList<>();

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Handler.class);
        PowerMockito.mockStatic(CommonUtil.class);
        PowerMockito.mockStatic(Util.class);
        PowerMockito.mockStatic(BearingTrainer.class);
        PowerMockito.mockStatic(ConfigurationSettings.class);
        PowerMockito.mockStatic(Log.class);
        PowerMockito.when(Util.getCertificate(Mockito.any(Context.class))).thenReturn(inputStream);
        PowerMockito.when(BearingTrainer.getInstance(Mockito.any(Context.class))).thenReturn(bearingTrainer);
        PowerMockito.when(ConfigurationSettings.saveConfigObject(Mockito.any(ConfigurationSettings.class))).thenReturn(true);
        PowerMockito.when(ConfigurationSettings.getConfiguration()).thenReturn(configurationSettings);
        PowerMockito.when(configurationSettings.getSensorPreferences()).thenReturn(sensor);
        PowerMockito.when(sensor.withProperty(Mockito.any(Property.Sensor.class),Mockito.anyObject())).thenReturn(sensor);
        PowerMockito.when(configurationSettings.withSensorPreferences(Mockito.any(Sensor.class))).thenReturn(configurationSettings);
        bearingOutput = new BearingOutput();
        final Header header = new Header();
        header.setStatusCode(StatusCode.OK);
        bearingOutput.setHeader(header);
        final Body body = new Body();
        body.setErrorMessage("Error");
        body.setOutput("Output");
        body.setTimestamp("102145");
        body.setSnapshotObservations(new ArrayList<>());
        bearingOutput.setBody(body);
        PowerMockito.when(bearingTrainer.retrieve(Mockito.any(BearingConfiguration.class), Mockito.any(BearingData.class), Mockito.anyBoolean()
                , Mockito.any(BearingCallBack.class))).thenReturn(bearingOutput);

        PowerMockito.whenNew(Handler.class).withAnyArguments().thenReturn(handler);
        training = BearingTrainingImpl.getInstance(context, "");
    }

    @Test
    public void setBearingServerEndPointTest() {
        training.setBearingServerEndPoint("serverEndPoint", inputStream, iBearingTrainingCallback);
        Mockito.verify(handler, Mockito.times(1))
                .sendMessage(Mockito.any(Message.class));
    }

    @Test
    public void storeBearingDataExternalTest() {
        training.storeBearingData(true, context);
        Mockito.verify(configurationSettings, Mockito.times(1))
                .withDataStoragePathPreference(Mockito.any(ConfigurationSettings.DataStorePathPreference.EXTERNAL.getDeclaringClass()));
    }

    @Test
    public void storeBearingDataInternalTest() {
        training.storeBearingData(false, context);
        Mockito.verify(configurationSettings, Mockito.times(1))
                .withDataStoragePathPreference(Mockito.any(ConfigurationSettings.DataStorePathPreference.INTERNAL.getDeclaringClass()));
    }

    @Test
    public void snapshotFeatchSoonAfterTrainSiteTest(){
        training.snapshotFeatchSoonAfterTrainSite("siteName", iBearingSiteSignalMergeListener);
        Mockito.verify(handler, Mockito.times(1))
                .sendMessage(Mockito.any(Message.class));
    }

    @Test
    public void downloadSiteAndLocationsTest(){
        training.downloadSiteAndLocations("siteName", iDownloadSiteLocationsListener);
    }

    @Test
    public void getAllLocationNamesForSiteFromLocalTest(){
        Set<String> wifiLocationNames = new HashSet<>();
        Set<String> bleLocationNames = new HashSet<>();
        final BearingSitedetails bearingSitedetails = new BearingSitedetails();
        bearingSitedetails.setSiteName("siteName");
        bearingSitedetails.setBleLocationNames(bleLocationNames);
        bearingSitedetails.setWifiLocationNames(wifiLocationNames);
        Assert.assertNotEquals(bearingSitedetails,training.getAllLocationNamesForSiteFromLocal("siteName"));
    }

    @Test
    public void deleteBearingDataTest(){
        List<String> locations = new ArrayList<>();
        locations.add("Ranchi");
        locations.add("Bangalore");
        training.deleteBearingData("siteName", locations, iBearingDataDelete);
        Mockito.verify(handler, Mockito.times(1))
                .sendMessage(Mockito.any(Message.class));

    }

    @Test
    public void deleteBearingDataEmpty(){
        training.deleteBearingData(null, null, iBearingDataDelete);
        Mockito.verify(handler, Mockito.times(1))
                .sendMessage(Mockito.any(Message.class));
    }
}
