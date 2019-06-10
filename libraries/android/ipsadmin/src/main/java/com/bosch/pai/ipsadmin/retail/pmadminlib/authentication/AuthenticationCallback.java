package com.bosch.pai.ipsadmin.retail.pmadminlib.authentication;

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
