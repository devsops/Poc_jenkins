package com.bosch.pai.retail.configuration.controllers;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;
import com.bosch.pai.retail.configmodel.SiteLocations;
import com.bosch.pai.retail.configuration.service.LocationsService;

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
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocationsControllerTest {

    @Mock
    private LocationsService locationsService;

    @InjectMocks
    private LocationsController locationsController;

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
    public void testsSaveOrUpdateLocations(){
        when(locationsService.saveOrUpdateLocations(anyString(),anyString(),anyString(),any(),anyString())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,"test success"));
        ResponseEntity<StatusMessage> response = locationsController.saveOrUpdateLocations("test_comp","100","test_site",new HashSet<>(),"android");
        assertEquals(StatusMessage.STATUS.SUCCESS,response.getBody().getStatus());
        verify(locationsService).saveOrUpdateLocations(anyString(),anyString(),anyString(),any(),anyString());
    }

    @Test
    public void testGetAllLocations(){
        List<SiteLocations> siteLocations = new ArrayList<>();
        SiteLocations siteLocation = new SiteLocations("test_comp","test_store","test_site",new HashSet<>());
        siteLocations.add(siteLocation);
        when(locationsService.getAllLocations(anyString(),anyString(),anyString(),anyString())).thenReturn(siteLocations);
        ResponseEntity<List<SiteLocations>> response = locationsController.getAllLocations("test_comp","test_store","test_site","android");
        assertEquals(1,response.getBody().size());
        verify(locationsService).getAllLocations(anyString(),anyString(),anyString(),anyString());

    }


}
