package com.bosch.pai.ipsadmin.bearing.core.util;

import android.os.Environment;

import java.io.File;

/**
 * The type Constants.
 */
public final class Constants {

    private Constants() {
    }

    /**
     * Base folder location where all the sites and location data resides
     */
    public static final String BASE_FOLDER_LOCATION = Environment.getExternalStorageDirectory().getPath() + File.separator + "WiFiLocalizeData" + File.separator;

    /**
     * The constant MAX_ARRAY_SIZE.
     */
    public static final int MAX_ARRAY_SIZE = 10;
    /**
     * The constant LOCATION_FILE_EXTENSION.
     */
    public static final String LOCATION_FILE_EXTENSION = ".csv";
    /**
     * Site data record file extension
     */
    public static final String SITE_FILE_EXTENSION = ".snapshot";

    /**
     * The constant SITE_SET_FILE_EXTENSION.
     */
//TODO remove this later
    public static final String SITE_SET_FILE_EXTENSION = ".set";

    /**
     * The constant RESULT_OK.
     */
    public static final int RESULT_OK = 0;
    /**
     * The constant RESULT_CANCEL.
     */
    public static final int RESULT_CANCEL = 1;
    /**
     * The constant PERMISSION_DENEID.
     */
    public static final int PERMISSION_DENEID = -1;
    /**
     * Error messages
     */
    public static final String SITE_SUCCESS = "SITE_ADDED_SUCCESS";
    /**
     * The constant PERMISSION_NOT_ENABLED.
     */
    public static final String PERMISSION_NOT_ENABLED = "PERMISSION_NOT_ENABLED";
    /**
     * The constant SITE_ALREADY_EXISTS.
     */
    public static final String SITE_ALREADY_EXISTS = "SITE_ALREADY_EXISTS";
    /**
     * The constant APPEND_DATA_NULL.
     */
    public static final String APPEND_DATA_NULL = "APPEND_DATA_NULL";
    /**
     * The constant SITE_NOT_EXISTS.
     */
    public static final String SITE_NOT_EXISTS = "SITE_NOT_EXISTS";

    /**
     * Error messages
     */
    public static final String NO_SIGNALS_FOUND = "NO_SIGNALS_FOUND";
    /**
     * The constant LOCATION_ALREADY_EXISTS.
     */
    public static final String LOCATION_ALREADY_EXISTS = "LOCATION_ALREADY_EXISTS";
    /**
     * The constant SITE_NOT_EXIST.
     */
    public static final String SITE_NOT_EXIST = "SITE_NOT_EXIST";
    /**
     * The constant SITE_DATA_NOT_EXIST.
     */
    public static final String SITE_DATA_NOT_EXIST = "SITE_DATA_NOT_EXIST";
    /**
     * The constant NO_LOCATIONS_FOUND.
     */
    public static final String NO_LOCATIONS_FOUND = "NO_LOCATIONS_FOUND";
    /**
     * The constant SITE_DATA_FOR_SENSOR_TYPE_NOT_EXIST.
     */
    public static final String SITE_DATA_FOR_SENSOR_TYPE_NOT_EXIST = "SITE_DATA_FOR_SENSOR_TYPE_NOT_EXIST";
    /**
     * The constant MINIMUM_TWO_LOCATIONS_NEEDED.
     */
    public static final String MINIMUM_TWO_LOCATIONS_NEEDED = "Train minimum two locations";

    /**
     * The constant SITE_ERROR.
     */
    public static final String SITE_ERROR = "NO SITE DATA ON DEVICE";
    /**
     * The constant SITE_UNKNOWN.
     */
    public static final String SITE_UNKNOWN = "SITE_UNKNOWN";
    /**
     * The constant SENSOR_NOT_ENABLED.
     */
    public static final String SENSOR_NOT_ENABLED = "SENSOR_NOT_ENABLED";


    /**
     * The constant LOCATION_NOT_EXIST.
     */
    public static final String LOCATION_NOT_EXIST = "LOCATION_NOT_EXIST";
    /**
     * The constant LOCATION_ADDED_SUCCESS.
     */
    public static final String LOCATION_ADDED_SUCCESS = "LOCATION_ADDED_SUCCESSFULLY";
    /**
     * The constant INVALID_TRAINING_DATA_LOCATION.
     */
    public static final String INVALID_TRAINING_DATA_LOCATION = "INVALID_TRAINING_DATA_FOR_LOCATION";


}
