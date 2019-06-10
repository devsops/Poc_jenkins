package com.bosch.pai.ipsadmin.retail.pmadminlib.authentication;

import com.google.gson.annotations.SerializedName;

public class PasswordChangeModel {

    @SerializedName("oldPassword")
    private String oldPassword;
    @SerializedName("newPassword")
    private String newPassword;
    @SerializedName("credentialsExpiry")
    private int credentialsExpiry;

    public PasswordChangeModel() {
        //Needed
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public int getCredentialsExpiry() {
        return credentialsExpiry;
    }

    public void setCredentialsExpiry(int credentialsExpiry) {
        this.credentialsExpiry = credentialsExpiry;
    }
}
