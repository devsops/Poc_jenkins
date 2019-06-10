package com.bosch.pai.retail.configuration.service;


import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.configmodel.ConfigModel;
import com.bosch.pai.retail.configmodel.SiteLocationDetails;
import com.bosch.pai.retail.configuration.dao.SiteConfigDAO;
import com.bosch.pai.retail.configuration.dao.SiteLocationDAO;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConfigurationServiceTest {

    @InjectMocks
    private ConfigurationService configurationService;

    @Mock
    private SiteConfigDAO siteConfigDAO;

    @Mock
    private SiteLocationDAO siteLocationDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }




    /*@Test
    public void testSaveStoreLocations_emptyBaylist(){
        List<String> bayList = new ArrayList<>();
        when(siteLocationDAO.saveSiteLocationsDetails(anyString(),anyString(),anyString(),anyString(),any(),any(),any())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,"test"));
        StatusMessage statusMessage = configurationService.saveOrUpdateCateDeptBrandMappingDetails("test_company","test_store","test_site","test_location",null,"android",false);
        assertEquals(StatusMessage.STATUS.FAILED_TO_SAVE_STORE_LOCATION_MAPPING,statusMessage.getStatus() );
    }*/

    @Test
    public void testGetStoreLocationDetail(){
        SiteLocationDetails siteLocation = new SiteLocationDetails();
        siteLocation.setStoreId("test_store");
        siteLocation.setSiteName("test_site");
        siteLocation.setCompanyId("test_company");
        siteLocation.setLocationName("test_location");
        Set<String> bay = new HashSet<>();
        bay.add("test_bay");
        List<SiteLocationDetails> sl = new ArrayList<>();
        sl.add(siteLocation);
        when(siteLocationDAO.getSiteLocationDetails(any(),any(),any(),any(),any())).thenReturn(sl);
        List<SiteLocationDetails> siteLocations = configurationService.getCateDeptBrandMappingDetails("test_comp","test_store","test_site","test_loc","android");
        assertEquals(1,siteLocations.size());
        verify(siteLocationDAO).getSiteLocationDetails(any(),any(),any(),any(),any());
    }

    /*@Test
    public void testSaveStoreLocations(){
        when(siteLocationDAO.saveSiteLocationsDetails(any(),any(),any(),any(),any(),any(),any())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,""));
        StatusMessage statusMessage = configurationService.saveOrUpdateCateDeptBrandMappingDetails("test_company","test_store","test_site","test_location",null,"android",false);
        assertEquals(StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());
        verify(siteLocationDAO).saveSiteLocationsDetails(any(),any(),any(),any(),any(),any(),any());
    }*/

    @Test
    public void testDeleteStoreLocationDetails(){
        when(siteLocationDAO.deleteSiteLocationDetails(any(),any(),any(),any(),any())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,"success"));
        StatusMessage sm = configurationService.deleteStoreLocationDetails("test_comp","test_store","test_site","test_loc","android");
        assertEquals(StatusMessage.STATUS.SUCCESS,sm.getStatus() );
        verify(siteLocationDAO).deleteSiteLocationDetails(any(),any(),any(),any(),any());
    }

    @Test
    public void testGetBearingConfiguration(){
        ConfigModel configModel = new ConfigModel();
        configModel.setSiteName("test_site");
        configModel.setStoreId("test_store");
        configModel.setCompanyId("test_company");
        Map<String,String> configMap = new HashMap<>();
        configMap.put("test_key","test_val");
        configModel.setSiteConfigMap(configMap);
        List<ConfigModel> cms = new ArrayList<>();
        cms.add(configModel);
        when(siteConfigDAO.getSiteConfigDetails(any(),any(),any(),any())).thenReturn(cms);
        List<ConfigModel> configModels = configurationService.getBearingConfiguration("test_comp","test_store","test_site","android");
        assertEquals(configModels.size(),1);
        verify(siteConfigDAO).getSiteConfigDetails(any(),any(),any(),any());
    }

    @Test
    public void testSaveBearingConfiguration(){
        Map<String,String> configMap = new HashMap<>();
        configMap.put("test_key","test_val");
        when(siteConfigDAO.saveSiteConfigDetails(any(),any(),any(),any(),any())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,""));
        StatusMessage sm = configurationService.saveBearingConfiguration("test_company","test_store","test_site",configMap,"android");
        assertEquals(StatusMessage.STATUS.SUCCESS,sm.getStatus());
        verify(siteConfigDAO).saveSiteConfigDetails(any(),any(),any(),any(),any());
    }

    @Test
    public void testSaveBearingConfiguration_emptyMap(){
        Map<String,String> configMap = new HashMap<>();
        when(siteConfigDAO.saveSiteConfigDetails(any(),any(),any(),any(),any())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,""));
        StatusMessage sm = configurationService.saveBearingConfiguration("test_company","test_store","test_site",configMap,"android");
        assertEquals(StatusMessage.STATUS.FAILED_TO_SAVE_BEARING_CONFIGURATION,sm.getStatus());
    }

    @Test
    public void testDeleteBearingConfiguration(){
        when(siteConfigDAO.deleteSiteConfigDetails(any(),any(),any(),any())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,""));
        StatusMessage sm = configurationService.deleteBearingConfiguration("test_comp","test_store","test_site","android");
        verify(siteConfigDAO).deleteSiteConfigDetails(any(),any(),any(),any());
        assertEquals(StatusMessage.STATUS.SUCCESS,sm.getStatus());
    }



}
