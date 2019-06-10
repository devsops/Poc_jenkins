
package com.bosch.pai.ipsadmin.bearing.sensordatastore.sensorobservation.rawobs;

import android.net.wifi.ScanResult;
import android.util.Log;

import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.config.ConfigurationSettings;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotItem;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RawSnapshotConvertor.class, LogAndToastUtil.class})
public class RawSnapshotConvertorTest {
    private static final String TAG = RawSnapshotConvertorTest.class.getName();
    private List<ScanResult> scanResults;
    private ScanResult sr;
    private List<String> bleTestAdressList;
    private List<Integer> bleTestRssiList;

    private List<SnapshotObservation> testSnapshotObservationsWifi;
    private List<SnapshotObservation> testSnapshotObservationsBle;
    private List<SnapshotObservation> testSnapshotObservationsGps;
    private List<SnapshotObservation> testSnapshotObservationMagneto;
    private List<SnapshotObservation> testSnapshotObservationsIMU;
    private double testGPSlat;
    private double testGPSlong;


    @Before
    public void init() {
        PowerMockito.mockStatic(LogAndToastUtil.class);
        try {
            Constructor<ScanResult> ctor = ScanResult.class.getDeclaredConstructor(null);
            ctor.setAccessible(true);
            try {
                sr = ctor.newInstance(null);
            } catch (InstantiationException e) {
                Log.e(TAG, "init: ", e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "init: ", e);
            } catch (InvocationTargetException e) {
                Log.e(TAG, "init: ", e);
            }

            sr.BSSID = "aa:aa:aa:aa:aa:aa";
            sr.SSID = "Test123";
            sr.frequency = 0000;
            sr.capabilities = "[WPA2-PSK-CCMP][ESS]";
            sr.level = (int) Math.random();   // create a random integer

            scanResults = new ArrayList<>();


            for (int i = 0; i < 12; i++) {
                scanResults.add(sr);
            }


        } catch (NoSuchMethodException e) {
            Log.e(TAG, "init: ", e);
        }
        testSnapshotObservationsWifi = createTestObjectforwifi(sr);
        testSnapshotObservationsBle = createTestObjectforBle();
        testSnapshotObservationMagneto = createTestObjectforMagneto();
        testSnapshotObservationsIMU = createTestObjectFotIMU();

        bleTestAdressList = createTestAddressList();
        bleTestRssiList = createTestRssiList();
        testGPSlat = Math.random();
        testGPSlong = Math.random();
        testSnapshotObservationsGps = createTestObjforGPS();


    }


    private List<SnapshotObservation> createTestObjectforwifi(ScanResult sr) {


        final SnapshotObservation testSnapshotObservation = new SnapshotObservation();
        final List<SnapshotObservation> testObservations = new ArrayList<>();
        final SnapshotItem snapshotItem = new SnapshotItem();

        snapshotItem.setSourceId(sr.BSSID);

        double[] measured = new double[1];
        measured[0] = sr.level;
        snapshotItem.setMeasuredValues(measured);
        List<SnapshotItem> testsnapshotitems = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            testsnapshotitems.add(snapshotItem);
        }

        testSnapshotObservation.setSensorType(BearingConfiguration.SensorType.ST_WIFI);
        testSnapshotObservation.setSnapShotItemList(testsnapshotitems);
        testObservations.add(testSnapshotObservation);

        return testObservations;

    }

    private List<SnapshotObservation> createTestObjectforBle() {

        final List<SnapshotItem> testBleSnapList = new ArrayList<>();
        final SnapshotItem snapshotItem = new SnapshotItem();
        final SnapshotObservation snapshotObservation = new SnapshotObservation();
        final List<SnapshotObservation> testObservation = new ArrayList<>();
        snapshotItem.setSourceId("aa:aa:aa:aa:aa:aa");
        double[] rssi = new double[1];
        rssi[0] = -12;
        snapshotItem.setMeasuredValues(rssi);


        for (int i = 0; i < 12; i++) {
            testBleSnapList.add(snapshotItem);
        }

        snapshotObservation.setSensorType(BearingConfiguration.SensorType.ST_BLE);
        snapshotObservation.setSnapShotItemList(testBleSnapList);
        testObservation.add(snapshotObservation);

        return testObservation;
    }


    private List<SnapshotObservation> createTestObjectFotIMU() {


        final List<SnapshotItem> testIMUSnapList = new ArrayList<>();
        final SnapshotItem snapshotItem = new SnapshotItem();
        final SnapshotObservation snapshotObservation = new SnapshotObservation();
        final List<SnapshotObservation> testObservation = new ArrayList<>();
        snapshotItem.setSourceId("STEP");
        double[] distance = new double[1];
        distance[0] = 10;
        snapshotItem.setMeasuredValues(distance);

        testIMUSnapList.add(snapshotItem);
        snapshotObservation.setSensorType(BearingConfiguration.SensorType.ST_IMU);
        snapshotObservation.setSnapShotItemList(testIMUSnapList);
        testObservation.add(snapshotObservation);

        return testObservation;

    }

    private List<SnapshotObservation> createTestObjectforMagneto() {

        final List<SnapshotItem> testMagnetoSnapList = new ArrayList<>();

        final SnapshotObservation snapshotObservation = new SnapshotObservation();
        final List<SnapshotObservation> testObservation = new ArrayList<>();
        final SnapshotItem snapshotItemX = new SnapshotItem();
        snapshotItemX.setSourceId("X");
        double[] magX = new double[1];
        magX[0] = -1;
        snapshotItemX.setMeasuredValues(magX);
        testMagnetoSnapList.add(snapshotItemX);

        final SnapshotItem snapshotItemY = new SnapshotItem();
        snapshotItemY.setSourceId("Y");
        double[] magY = new double[1];
        magY[0] = -1;
        snapshotItemX.setMeasuredValues(magY);
        testMagnetoSnapList.add(snapshotItemY);


        final SnapshotItem snapshotItemZ = new SnapshotItem();
        snapshotItemZ.setSourceId("Z");
        double[] magZ = new double[1];
        magZ[0] = -1;
        snapshotItemX.setMeasuredValues(magZ);
        testMagnetoSnapList.add(snapshotItemZ);


        snapshotObservation.setSensorType(BearingConfiguration.SensorType.ST_MAGNETO);
        snapshotObservation.setSnapShotItemList(testMagnetoSnapList);
        testObservation.add(snapshotObservation);

        return testObservation;
    }


    private List<SnapshotObservation> createTestObjforGPS() {
        final List<SnapshotItem> testGPSSnapList = new ArrayList<>();
        final SnapshotItem snapshotItem = new SnapshotItem();
        final SnapshotObservation snapshotObservation = new SnapshotObservation();
        final List<SnapshotObservation> testObservation = new ArrayList<>();
        double[] rssi = new double[2];
        rssi[0] = testGPSlat;
        rssi[1] = testGPSlong;
        snapshotItem.setMeasuredValues(rssi);
        snapshotItem.setSourceId("Test123");
        String[] customField = new String[1];
        snapshotItem.setCustomField(customField);
        testGPSSnapList.add(snapshotItem);
        snapshotObservation.setSnapShotItemList(testGPSSnapList);
        snapshotObservation.setSensorType(BearingConfiguration.SensorType.ST_GPS);
        snapshotObservation.setDetectionLevel(BearingConfiguration.DetectionLevel.MACRO);
        testObservation.add(snapshotObservation);
        return testObservation;


    }

    private List<String> createTestAddressList() {
        List<String> testAdressList = new ArrayList<>();
        String addressList = "aa:aa:aa:aa:aa:aa";
        for (int i = 0; i < 12; i++) {
            testAdressList.add(addressList);

        }
        return testAdressList;
    }

    private List<Integer> createTestRssiList() {
        List<Integer> testRssiList = new ArrayList<>();
        Integer rssiValue = -12;

        for (int i = 0; i < 12; i++) {
            testRssiList.add(rssiValue);
        }
        return testRssiList;
    }

    @Test
    public void createSnapshotObservationforWIFITest() {
        ConfigurationSettings.setConfigFileLocation(File.separator);
        List<SnapshotObservation> snapshotObservationforWIFI = RawSnapshotConvertor.createSnapshotObservationforWIFI(scanResults);
        final List<SnapshotObservation> temp = new ArrayList<>();
        final SnapshotObservation testSnapshotObservation = new SnapshotObservation();
        final SnapshotItem snapshotItem = new SnapshotItem();

        snapshotItem.setSourceId(sr.BSSID);

        double[] measured = new double[1];
        measured[0] = sr.level;
        snapshotItem.setMeasuredValues(measured);
        List<SnapshotItem> testsnapshotitems = new ArrayList<>();
        testsnapshotitems.add(snapshotItem);
        testSnapshotObservation.setSensorType(BearingConfiguration.SensorType.ST_WIFI);
        testSnapshotObservation.setSnapShotItemList(testsnapshotitems);
        temp.add(testSnapshotObservation);
        assertSame(snapshotObservationforWIFI.get(0).getSensorType(), temp.get(0).getSensorType());
       // assertEquals(snapshotObservationforWIFI, temp);
    }

    @Test
    public void createSnapshotObservationforBLETest() {
        List<SnapshotObservation> snapshotObservationforBLE = RawSnapshotConvertor.createSnapshotObservationforBLE(bleTestAdressList, bleTestRssiList);
        assertEquals(snapshotObservationforBLE.get(0).getSensorType(), testSnapshotObservationsBle.get(0).getSensorType());
    }

    @Test
    public void createSnapshotObservationforGPSTest() {
        List<SnapshotObservation> snapshotObservationforGPS = RawSnapshotConvertor.createSnapshotObservationforGPS(testGPSlat, testGPSlong, "Test123");
        assertEquals(snapshotObservationforGPS.get(0).getSensorType(), testSnapshotObservationsGps.get(0).getSensorType());

    }

    @Test
    public void createSnapshotObservationforMagnetoTest() {
        List<SnapshotObservation> snapshotObservationforMagneto = RawSnapshotConvertor.createSnapshotObservationMagneto(-1, -1, -1);
        assertEquals(snapshotObservationforMagneto.get(0).getSensorType(), testSnapshotObservationMagneto.get(0).getSensorType());
    }

    @Test
    public void createSnapshotObservationforIMUTest() {
        List<SnapshotObservation> snapshotObservationforIMU = RawSnapshotConvertor.createSnapshotObservationIMU(10);
        assertEquals(snapshotObservationforIMU.get(0).getSensorType(), testSnapshotObservationsIMU.get(0).getSensorType());
    }


}

