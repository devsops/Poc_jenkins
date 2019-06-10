package com.bosch.pai.detection.authentication;

public interface AuthenticationCallback {
    /**
     * Callback on Authentication success
     *
     */
    void onAuthenticationSuccess();
    /**
     * Callback on Authentication fail
     *
     * @param message
     */
    void onAuthenticationFail(String message);

}
