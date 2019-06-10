package com.bosch.pai.bearing.sensordatastore.restclient;

import java.util.List;

/**
 * The interface Bearing client callback.
 */
public interface BearingClientCallback {

    /**
     * The enum Status.
     */
    enum Status {

        /**
         * The Upload all locations error.
         */
        UPLOAD_ALL_LOCATIONS_ERROR("Unable to Upload all locations for the site"),
        /**
         * The Upload all locations success.
         */
        UPLOAD_ALL_LOCATIONS_SUCCESS("Uploaded all locations for the site"),
        /**
         * The Internal error.
         */
        INTERNAL_ERROR("Something went wrong internally"),
        /**
         * The Site data fetched.
         */
        SITE_DATA_FETCHED("Site Data fetched and written successfully"),
        /**
         * The Svmt data fetched.
         */
        SVMT_DATA_FETCHED("SVMT Data fetched and written successfully"),
        /**
         * The Server error.
         */
        SERVER_ERROR("Something went wrong on the Server"),
        /**
         * The Write failure.
         */
        WRITE_FAILURE("Something went wrong in file Write Operation"),
        /**
         * The Invalid inputs.
         */
        INVALID_INPUTS("Invalid inputs for request"),
        /**
         * The Invalid data for site persist.
         */
        INVALID_DATA_FOR_SITE_PERSIST("No valid site or Sensor data present for Request"),
        /**
         * The Invalid data for location persist.
         */
        INVALID_DATA_FOR_LOCATION_PERSIST("No valid location data present for Request"),
        /**
         * The Invalid data for training persist.
         */
        INVALID_DATA_FOR_TRAINING_PERSIST("No valid training data present for Request");


        private String msg;

        Status(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return msg.toString();
        }
    }

    /**
     * On request success.
     *
     * @param message the message
     */
    void onRequestSuccess(String message);

    /**
     * On request failure.
     *
     * @param errMessage the err message
     */
    void onRequestFailure(String errMessage);

    /**
     * The interface Get data callback.
     */
    interface GetDataCallback {
        /**
         * On data received.
         *
         * @param dataList the data list
         */
        void onDataReceived(List<String> dataList);

        /**
         * On data received error.
         *
         * @param errorMessage the error message
         */
        void onDataReceivedError(String errorMessage);
    }


}
