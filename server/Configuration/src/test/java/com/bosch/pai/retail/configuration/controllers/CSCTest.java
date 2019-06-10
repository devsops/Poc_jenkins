package com.bosch.pai.retail.configuration.controllers;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;
import com.bosch.pai.retail.configmodel.BaymapDetail;
import com.bosch.pai.retail.configmodel.ConfigModel;
import com.bosch.pai.retail.configmodel.SiteLocationDetails;
import com.bosch.pai.retail.configmodel.SiteLocationHierarchyDetail;
import com.bosch.pai.retail.configuration.service.ConfigurationService;
import com.bosch.pai.retail.requests.SaveLocationBaymapRequest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by hug5kor on 11/22/2017.
 */


public class CSCTest {

    @Mock
    private ConfigurationService configurationService;

    private static final String SITENAME = "test_site";

    @InjectMocks
    private CSC csc;

    @BeforeClass
    public static void setUpBeforeClass()  {

    }

    @Before
    public void setUp() {
        RequestContext requestContext = new RequestContext();
        requestContext.setUserId("test");
        ContextHolder.setContext(requestContext);
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown()  {
    }

    @AfterClass
    public static void tearDownAfterClass()  {
    }

    @Test
    public void testslbm(){
        SaveLocationBaymapRequest saveLocationBaymapRequest = new SaveLocationBaymapRequest("{\"locationType\":\"aisle\",\"locationDepartments\":[\"test\"],\"locationCategorys\":[\"test\"],\"locationBrands\":[\"test\"],\"locationCateDeptBrands\":[\"test\"]}",false);
        when(configurationService.saveOrUpdateCateDeptBrandMappingDetails(any(),any(),any(),any(),any(),anyString(),anyBoolean())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,""));
        ResponseEntity<StatusMessage> responseEntity = csc.saveOrUpdateCateDeptBrandMappingDetails("test","test",SITENAME,"test_location",saveLocationBaymapRequest,"android");
        assertEquals(StatusMessage.STATUS.SUCCESS,responseEntity.getBody().getStatus() );
        verify(configurationService).saveOrUpdateCateDeptBrandMappingDetails(any(),any(),any(),any(),any(),anyString(),anyBoolean());
    }

    @Test
    public void testglbm(){
        SiteLocationDetails siteLocation = new SiteLocationDetails();
        siteLocation.setLocationName("test_loc");
        siteLocation.setCompanyId("test_company");
        siteLocation.setStoreId("test_store");
        siteLocation.setSiteName(SITENAME);
        List<SiteLocationDetails> siteLocations = new ArrayList<>();
        siteLocations.add(siteLocation);
        when(configurationService.getCateDeptBrandMappingDetails(any(), any(),any(),any(),any())).thenReturn(siteLocations);
        ResponseEntity<List<SiteLocationDetails>> sls = csc.glbm("test_comp","test_store",SITENAME,"test_loc","android");
        assertEquals(sls.getBody().size(),1);
        verify(configurationService).getCateDeptBrandMappingDetails(any(), any(),any(),any(),anyString());
    }


    @Test
    public void testGetLocationInfo(){
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        when(configurationService.getLocationDetail(anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(responseEntity);

        ResponseEntity result = csc.getLocationInfo("test","test","test","test","android");

        assertEquals(HttpStatus.OK,result.getStatusCode());

        verify(configurationService).getLocationDetail(anyString(),anyString(),anyString(),anyString(),anyString());
    }


    @Test
    public void testDeleteCateDeptBrandMappingDetails(){

        when(configurationService.deleteStoreLocationDetails(anyString(),anyString(),anyString(),anyString(),anyString())).
                thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,"success"));
        ResponseEntity<StatusMessage> responseEntity = csc.deleteCateDeptBrandMappingDetails("test","test","test","test","android");

        assertEquals(HttpStatus.ACCEPTED,responseEntity.getStatusCode());

        verify(configurationService).deleteStoreLocationDetails(anyString(),anyString(),anyString(),anyString(),anyString());

    }

    @Test
    public void testSaveLocationBayMap(){
        SaveLocationBaymapRequest saveLocationBaymapRequest = new SaveLocationBaymapRequest("[]",false);
        when(configurationService.saveLocationBayMap(anyString(),anyString(),anyString(),anyString(),any(),anyString(),anyBoolean())).
                thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,"success"));
        ResponseEntity<StatusMessage> responseEntity = csc.saveLocationBayMap("test","test","test","test",saveLocationBaymapRequest,"android");
        assertEquals(HttpStatus.ACCEPTED,responseEntity.getStatusCode());
        verify(configurationService).saveLocationBayMap(anyString(),anyString(),anyString(),anyString(),any(),anyString(),anyBoolean());
    }

    @Test
    public void testGetLocationBayMap(){
        BaymapDetail baymapDetail = new BaymapDetail("test","test","test","test",new HashSet<>());
        List<BaymapDetail> baymapDetails = new ArrayList<>();
        baymapDetails.add(baymapDetail);
        when(configurationService.getLocationBayMap(anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(baymapDetails);
        ResponseEntity<List<BaymapDetail>> responseEntity = csc.getLocationBayMap("test_comp","test_store","test_site","test_loc","android");
        assertEquals(responseEntity.getStatusCode(),HttpStatus.ACCEPTED);
        verify(configurationService).getLocationBayMap(anyString(),anyString(),anyString(),anyString(),anyString());

    }

    @Test
    public void testDeleteLocationBayMap(){
        when(configurationService.deleteLocationBayMap(anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,"test success"));
        ResponseEntity<StatusMessage> responseEntity = csc.deleteLocationBayMap("test_comp","test_store","test_site","test_loc","android");
        assertEquals(responseEntity.getStatusCode(),HttpStatus.ACCEPTED);
        verify(configurationService).deleteLocationBayMap(anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void testssc(){
        when(configurationService.saveBearingConfiguration(anyString(),anyString(),anyString(),anyMap(),anyString())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,"test success"));
        ResponseEntity<StatusMessage> responseEntity = csc.ssc("test_comp","test_store","test_site",new HashMap<>(),"android");
        assertEquals(responseEntity.getStatusCode(),HttpStatus.ACCEPTED);
        verify(configurationService).saveBearingConfiguration(anyString(),anyString(),anyString(),anyMap(),anyString());
    }

    @Test
    public void testgsc(){
        ConfigModel configModel = new ConfigModel("test","test_store","test_site",new HashMap<>());
        List<ConfigModel> configModels = new ArrayList<>();
        configModels.add(configModel);
        when(configurationService.getBearingConfiguration(anyString(),anyString(),anyString(),anyString())).thenReturn(configModels);
        ResponseEntity<List<ConfigModel>> responseEntity = csc.gsc("test_comp","test_store","test_site","android");
        assertEquals(responseEntity.getStatusCode(),HttpStatus.ACCEPTED);
        verify(configurationService).getBearingConfiguration(anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void testdsc(){
        when(configurationService.deleteBearingConfiguration(anyString(),anyString(),anyString(),anyString())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,"test success"));
        ResponseEntity<StatusMessage> responseEntity = csc.dsc("test_comp","test_store","test_site","android");
        assertEquals(responseEntity.getStatusCode(),HttpStatus.ACCEPTED);
        verify(configurationService).deleteBearingConfiguration(anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void testSaveOrUpdateCateDeptBrandMappingDetails(){
        when(configurationService.saveOrUpdateHierarchyMappingDetails(anyString(),anyString(),anyString(),anyString(),any(),anyString())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,"test success"));
        ResponseEntity<StatusMessage> responseEntity = csc.saveOrUpdateCateDeptBrandMappingDetails("test_comp","test_store","test_site","test_loc",new ArrayList<>(),"android");
        assertEquals(responseEntity.getStatusCode(),HttpStatus.ACCEPTED);
        verify(configurationService).saveOrUpdateHierarchyMappingDetails(anyString(),anyString(),anyString(),anyString(),any(),anyString());

    }

    @Test
    public void testGetSiteLocationHierarchyMapping(){
        SiteLocationHierarchyDetail siteLocationHierarchyDetail = new SiteLocationHierarchyDetail("test_comp","test_store","test_site","test_loc",new ArrayList<>());
        List<SiteLocationHierarchyDetail> siteLocationHierarchyDetails = new ArrayList<>();
        siteLocationHierarchyDetails.add(siteLocationHierarchyDetail);
        when(configurationService.getSiteLocationHierarchyMapping(anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(siteLocationHierarchyDetails);
        ResponseEntity<List<SiteLocationHierarchyDetail>> responseEntity = csc.getSiteLocationHierarchyMapping("test_comp","test_store","test_site","test_loc","android");
        assertEquals( HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        verify(configurationService).getSiteLocationHierarchyMapping(anyString(),anyString(),anyString(),anyString(),anyString());
    }


    /*

    @Test
    public void testdlbm(){
        when(configurationService.deleteStoreLocationDetails(any(),any(),any(),any())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,""));
        ResponseEntity<StatusMessage> response = csc.dlbm("test_comp","test_store","test_site","test_loc");
        assertEquals(response.getBody().getStatus(), StatusMessage.STATUS.SUCCESS);
        verify(configurationService).deleteStoreLocationDetails(any(),any(),any(),any());
    }

    @Test
    public void testssc(){
        Map<String, String> map = new HashMap<>();
        map.put("test_key","test_value");
        when(configurationService.saveBearingConfiguration(any(),any(),any(),any())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,""));
        ResponseEntity<StatusMessage> response = csc.ssc("test_comp","test_store","test_site",map);
        assertEquals(response.getBody().getStatus(), StatusMessage.STATUS.SUCCESS);
        verify(configurationService).saveBearingConfiguration(any(),any(),any(),any());
    }

    @Test
    public void testgsc(){
        Map<String, String> map = new HashMap<>();
        map.put("test_key","test_value");
        ConfigModel configModel = new ConfigModel();
        configModel.setCompanyId("test_comp");
        configModel.setStoreId("test_store");
        configModel.setSiteName("test_site");
        configModel.setSiteConfigMap(map);
        List<ConfigModel> cms = new ArrayList<>();
        cms.add(configModel);
        when(configurationService.getBearingConfiguration(any(),any(),any())).thenReturn(cms);
        ResponseEntity<List<ConfigModel>> responseEntity = csc.gsc("test_comp","test_store","test_site");
        assertEquals(responseEntity.getBody().size(),1);
        verify(configurationService).getBearingConfiguration(any(),any(),any());
    }

    @Test
    public void testdsc(){
        when(configurationService.deleteBearingConfiguration(any(),any(),any())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,""));
        ResponseEntity<StatusMessage> responseEntity = csc.dsc("test_comp","test_store","test_site");
        assertEquals(responseEntity.getBody().getStatus(), StatusMessage.STATUS.SUCCESS);
        verify(configurationService).deleteBearingConfiguration(any(),any(),any());
    }
*/

}
