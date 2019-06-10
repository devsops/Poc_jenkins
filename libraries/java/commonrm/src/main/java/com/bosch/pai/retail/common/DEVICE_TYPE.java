package com.bosch.pai.retail.common;



public enum DEVICE_TYPE {

    DEVICE_TYPE_IOS("device_type_ios"),
    DEVICE_TYPE_ANDROID("device_type_android");

    private String deviceValue;

    DEVICE_TYPE(String deviceValue) {
        this.deviceValue = deviceValue;
    }

    public String deviceValue() {
        return deviceValue;
    }

    @Override
    public String toString() {
        return deviceValue();
    }
}

