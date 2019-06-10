package com.bosch.pai.bearing.core.util;

import android.os.Environment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Constants.class)
public class ConstantsTest {



    @Before
    public void testBeforeSetUp()
    {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testConstants()
    {
        assertEquals(Constants.MAX_ARRAY_SIZE,10);
        assertEquals(Constants.LOCATION_FILE_EXTENSION,".csv");
        assertEquals(Constants.SITE_FILE_EXTENSION,".snapshot");
        assertEquals(Constants.SITE_SET_FILE_EXTENSION,".set");
        assertEquals(Constants.RESULT_OK,0);
        assertEquals(Constants.RESULT_CANCEL,1);
        assertEquals(Constants.PERMISSION_DENEID,-1);
        assertEquals(Constants.SITE_SUCCESS,"SITE_ADDED_SUCCESS");
        assertEquals(Constants.PERMISSION_NOT_ENABLED,"PERMISSION_NOT_ENABLED");
        assertEquals(Constants.SITE_ALREADY_EXISTS,"SITE_ALREADY_EXISTS");

        assertEquals(Constants.APPEND_DATA_NULL,"APPEND_DATA_NULL");
        assertEquals(Constants.SITE_NOT_EXISTS,"SITE_NOT_EXISTS");
        assertEquals(Constants.NO_SIGNALS_FOUND,"NO_SIGNALS_FOUND");
        assertEquals(Constants.LOCATION_ALREADY_EXISTS,"LOCATION_ALREADY_EXISTS");
        assertEquals(Constants.SITE_NOT_EXIST,"SITE_NOT_EXIST");
        assertEquals(Constants.SITE_DATA_NOT_EXIST,"SITE_DATA_NOT_EXIST");
        assertEquals(Constants.NO_LOCATIONS_FOUND,"NO_LOCATIONS_FOUND");

        assertEquals(Constants.SITE_DATA_FOR_SENSOR_TYPE_NOT_EXIST,"SITE_DATA_FOR_SENSOR_TYPE_NOT_EXIST");
        assertEquals(Constants.MINIMUM_TWO_LOCATIONS_NEEDED,"Train minimum two locations");
        assertEquals(Constants.SITE_ERROR,"NO SITE DATA ON DEVICE");
        assertEquals(Constants.SITE_UNKNOWN,"SITE_UNKNOWN");
        assertEquals(Constants.SENSOR_NOT_ENABLED,"SENSOR_NOT_ENABLED");
        assertEquals(Constants.LOCATION_NOT_EXIST,"LOCATION_NOT_EXIST");
        assertEquals(Constants.LOCATION_ADDED_SUCCESS,"LOCATION_ADDED_SUCCESSFULLY");
        assertEquals(Constants.INVALID_TRAINING_DATA_LOCATION,"INVALID_TRAINING_DATA_FOR_LOCATION");

    }


}
