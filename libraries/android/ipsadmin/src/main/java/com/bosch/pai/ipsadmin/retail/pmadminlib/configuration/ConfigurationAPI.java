package com.bosch.pai.ipsadmin.retail.pmadminlib.configuration;

import com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.callback.IConfigurationCallback;

import java.util.Set;

public interface ConfigurationAPI {

    void saveStoreLocations(final String company,
                            final String store,
                            final String siteName,
                            final Set<String> locationNames,
                            final String baseUrl,
                            final IConfigurationCallback.ISaveStoreLocationsCallback listener);

}
