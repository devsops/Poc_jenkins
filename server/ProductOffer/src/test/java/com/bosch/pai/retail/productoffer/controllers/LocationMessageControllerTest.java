package com.bosch.pai.retail.productoffer.controllers;

import com.bosch.pai.retail.adtuning.model.offer.OfferResponse;
import com.bosch.pai.retail.adtuning.model.offer.UserOfferResponse;
import com.bosch.pai.retail.adtuning.responses.GetOfferResponse;
import com.bosch.pai.retail.adtuning.responses.PromoMapOfferResponse;
import com.bosch.pai.retail.adtuning.responses.PromoOfferResponse;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;
import com.bosch.pai.retail.productoffer.service.RetailProductService;

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
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class LocationMessageControllerTest {

    @Mock
    private RetailProductService retailProductService;

    @Mock
    private OfferResponse offerResponse;

    @InjectMocks
    private LocationMessageController locationMessageController;

    private GetOfferResponse getOfferResponse ;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        RequestContext requestContext = new RequestContext();
        requestContext.setUserId("test");
        ContextHolder.setContext(requestContext);
        MockitoAnnotations.initMocks(this);
        getOfferResponse = new GetOfferResponse();

    }

    @After
    public void tearDown() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }


    @Test
    public void testGetUSPOFF(){
        getOfferResponse.setStatusMessage(new StatusMessage(StatusMessage.STATUS.SUCCESS,""));
        when(retailProductService.getPromosForSection(any(),any(),any(),any())).thenReturn(getOfferResponse);
        ResponseEntity<GetOfferResponse> getOfferResponseResponseEntity = locationMessageController.getUSPOFF("test","test","test","test");
        assertEquals(StatusMessage.STATUS.SUCCESS,getOfferResponseResponseEntity.getBody().getStatusMessage().getStatus());
        verify(retailProductService).getPromosForSection(any(),any(),any(),any());
    }

    @Test
    public void testGetUSPOFF_noLocation(){
        getOfferResponse.setStatusMessage(new StatusMessage(StatusMessage.STATUS.SUCCESS,""));
        when(retailProductService.getPromosForSection(any(),any(),any(),any())).thenReturn(getOfferResponse);
        ResponseEntity<GetOfferResponse> getOfferResponseResponseEntity = locationMessageController.getUSPOFF("test","test","test",null);
        assertEquals(StatusMessage.STATUS.SUCCESS,getOfferResponseResponseEntity.getBody().getStatusMessage().getStatus());
        verify(retailProductService).getPromosForSection(any(),any(),any(),any());
    }

    @Test
    public void testGetUSPOFF_noSite(){
        getOfferResponse.setStatusMessage(new StatusMessage(StatusMessage.STATUS.SUCCESS,""));
        when(retailProductService.getPromosForSection(any(),any(),any(),any())).thenReturn(getOfferResponse);
        ResponseEntity<GetOfferResponse> getOfferResponseResponseEntity = locationMessageController.getUSPOFF("test","test",null,"test");
        assertEquals(StatusMessage.STATUS.FAILURE,getOfferResponseResponseEntity.getBody().getStatusMessage().getStatus());
    }

    @Test
    public void testAdCusOR1(){
        when(retailProductService.saveCompleteOfferResponse(any(),any(),any())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS, ""));
        ResponseEntity<StatusMessage> statusMessage = locationMessageController.adCCusOR("test",new ArrayList<>(),"android");
        assertEquals(StatusMessage.STATUS.SUCCESS,statusMessage.getBody().getStatus() );
        verify(retailProductService).saveCompleteOfferResponse(any(),any(),any());
    }

    @Test
    public void testCompletePromoOfferResponse(){
        when(retailProductService.saveCompletePromoOfferResponse(any(),any(),any())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,"test success"));
        ResponseEntity<StatusMessage> statusMessage = locationMessageController.completePromoOfferResponse("test_comp",new ArrayList<>(),"android");
        assertEquals(StatusMessage.STATUS.SUCCESS,statusMessage.getBody().getStatus());
        verify(retailProductService).saveCompletePromoOfferResponse(any(),any(),anyString());

    }

    @Test
    public void testgetSITEOFF(){
        PromoMapOfferResponse promoMapOfferResponse = new PromoMapOfferResponse();
        promoMapOfferResponse.setPromoDetailMap(new HashMap<>());
        promoMapOfferResponse.setStatusMessage(new StatusMessage(StatusMessage.STATUS.SUCCESS,"test"));
        when(retailProductService.getPromosForSite(any(),anyString(),anyString())).thenReturn(promoMapOfferResponse);
        ResponseEntity<PromoMapOfferResponse> promoMapOfferResponseResponseEntity = locationMessageController.getSITEOFF("test_comp","test_store","test_site");
        assertEquals(StatusMessage.STATUS.SUCCESS,promoMapOfferResponseResponseEntity.getBody().getStatusMessage().getStatus());
        verify(retailProductService).getPromosForSite(any(),anyString(),anyString());
    }

    @Test
    public void testgetSOFF(){
        PromoMapOfferResponse promoMapOfferResponse = new PromoMapOfferResponse();
        promoMapOfferResponse.setPromoDetailMap(new HashMap<>());
        promoMapOfferResponse.setStatusMessage(new StatusMessage(StatusMessage.STATUS.SUCCESS,"test"));
        when(retailProductService.getPromosForStore(any(),anyString())).thenReturn(promoMapOfferResponse);
        ResponseEntity<PromoMapOfferResponse> responseEntity = locationMessageController.getSOFF("test_comp","test_store");
        assertEquals(StatusMessage.STATUS.SUCCESS,responseEntity.getBody().getStatusMessage().getStatus());
        verify(retailProductService).getPromosForStore(any(),anyString());
    }

    @Test
    public void testgetASOFF(){
        PromoOfferResponse promoOfferResponse = new PromoOfferResponse();
        promoOfferResponse.setPromoDetailList(new ArrayList<>());
        promoOfferResponse.setStatusMessage(new StatusMessage(StatusMessage.STATUS.SUCCESS,"test"));
        when(retailProductService.getAllPromosForStore(any(),anyString(),any())).thenReturn(promoOfferResponse);
        ResponseEntity<PromoOfferResponse> responseResponseEntity = locationMessageController.getASOFF("test_comp","test_store","android");
        assertEquals(StatusMessage.STATUS.SUCCESS,responseResponseEntity.getBody().getStatusMessage().getStatus());
        verify(retailProductService).getAllPromosForStore(any(),anyString(),any());
    }
}


