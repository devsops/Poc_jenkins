package com.bosch.pai.retail.configuration.controllers;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;
import com.bosch.pai.retail.configmodel.HierarchyDetail;
import com.bosch.pai.retail.configuration.service.ConfigurationService;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StoreDetailsControllerTest {

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private StoreDetailsController storeDetailsController;

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
    public void testGSD(){
        List list = new ArrayList();
        list.add("test");
        when(configurationService.getStoreInfo(anyString(),anyString(),anyString(),anyString(),anyString())).thenReturn(list);
        ResponseEntity<List> response = storeDetailsController.gSD("test_comp","test_store","1","2","3");
        assertEquals(1,response.getBody().size());
        verify(configurationService).getStoreInfo(anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void testGAC(){
        List<String> list = new ArrayList<>();
        list.add("test");
        when(configurationService.getCategory(anyString(),anyString())).thenReturn(list);
        ResponseEntity<List<String>> response = storeDetailsController.gAC("test_comp","test_store");
        assertEquals(1,response.getBody().size());
        verify(configurationService).getCategory(anyString(),anyString());
    }


    @Test
    public void testGetStoreHierarchy(){
        List<HierarchyDetail> list = new ArrayList<>();
        HierarchyDetail hierarchyDetail = new HierarchyDetail();
        hierarchyDetail.setHierarchyLevel(1);
        hierarchyDetail.setHierarchyName("test");
        hierarchyDetail.setRequired(true);
        hierarchyDetail.setEntries(new ArrayList<>());
        list.add(hierarchyDetail);
        when(configurationService.getHierarchies(anyString(),anyString(),anyString())).thenReturn(list);
        ResponseEntity<List<HierarchyDetail>> response = storeDetailsController.getStoreHierarchy("test_comp","test_store","android");
        assertEquals(1,response.getBody().size());
        verify(configurationService).getHierarchies(anyString(),anyString(),anyString());
    }

    @Test
    public void testSaveStoreHierarchy(){
        when(configurationService.saveHierarchies(anyString(),any(),any(),anyString())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,"test success"));
        ResponseEntity<StatusMessage> response = storeDetailsController.saveStoreHierarchy("test_comp","test_store",new ArrayList<>(),"android");
        assertEquals(StatusMessage.STATUS.SUCCESS,response.getBody().getStatus());
        verify(configurationService).saveHierarchies(anyString(),anyString(),any(),any());
    }
}
