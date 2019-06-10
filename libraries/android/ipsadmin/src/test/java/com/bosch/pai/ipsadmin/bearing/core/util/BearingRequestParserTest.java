package com.bosch.pai.ipsadmin.bearing.core.util;

import com.bosch.pai.ipsadmin.bearing.core.util.BearingRequestParser;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.BearingData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.LocationMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.bearingdata.SiteMetaData;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.enums.BearingMode;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BearingRequestParser.class, BearingData.class, BearingMode.class})
public class BearingRequestParserTest {

    @Test
    public void testAllMethods() {
        final SiteMetaData siteMetaData = new SiteMetaData("SITE_NAME");
        final LocationMetaData locationMetaData = new LocationMetaData("LOC_NAME");
        final List<LocationMetaData> metaDataList = new ArrayList<>();
        metaDataList.add(locationMetaData);
        siteMetaData.setLocationMetaData(metaDataList);
        final BearingData bearingData = new BearingData(siteMetaData);
        final String siteName = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingData, BearingMode.SITE);
        Assert.assertEquals("SITE_NAME", siteName);
        final String locName = BearingRequestParser.parseBearingDataForSiteOrLocationName(bearingData, BearingMode.LOCATION);
        Assert.assertEquals("LOC_NAME", locName);

        final Map<BearingConfiguration.Approach, List<BearingConfiguration.SensorType>> map = new HashMap<>();
        final List<BearingConfiguration.SensorType> sensorTypes = new ArrayList<>();
        sensorTypes.add(BearingConfiguration.SensorType.ST_WIFI);
        map.put(BearingConfiguration.Approach.FINGERPRINT, sensorTypes);
        final BearingConfiguration bearingConfiguration = new BearingConfiguration(BearingConfiguration.OperationType.DETECT_LOC, map);
        final List<BearingConfiguration.SensorType> result = BearingRequestParser.getSensorList(bearingConfiguration, BearingConfiguration.Approach.FINGERPRINT);
        Assert.assertEquals(sensorTypes, result);


        final BearingMode bearingMode = BearingRequestParser.getBearingModeForDetection(bearingData);
        Assert.assertEquals(BearingMode.LOCATION, bearingMode);
    }

}
