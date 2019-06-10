package com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.callback;


public interface IConfigurationCallback {

    interface ISaveStoreLocationsCallback {

        void onSuccess();

        void onFailure(String errorMessage);

    }
}
