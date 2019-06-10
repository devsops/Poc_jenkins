package com.bosch.pai.retail.productoffer.service;

import com.bosch.pai.retail.adtuning.model.offer.OfferResponse;
import com.bosch.pai.retail.adtuning.responses.GetOfferResponse;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;
import com.bosch.pai.retail.configmodel.SiteLocationDetails;
import com.bosch.pai.retail.productoffer.dao.OfferResponseDetailDAO;
import com.bosch.pai.retail.productoffer.dao.SiteLocationDAO;
import com.bosch.pai.retail.productoffer.dao.ValidPromoDetailDAO;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class RetailProductServiceTest {

    @Mock
    private SiteLocationDAO siteLocationDAO = null;

    private OfferResponse offerResponse =new OfferResponse();

    @Mock
    private OfferResponseDetailDAO offerResponseDetailDAO;

    @Mock
    private ValidPromoDetailDAO validPromoDetailDAO = null;

    @InjectMocks
    private RetailProductService retailProductService;

    @InjectMocks
    private SiteLocationDetails siteLocation;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        RequestContext requestContext = new RequestContext();
        requestContext.setUserId("test");
        ContextHolder.setContext(requestContext);
        MockitoAnnotations.initMocks(this);
        siteLocation.setStoreId("test_store");
        siteLocation.setCompanyId("test_company");
        siteLocation.setSiteName("test_site");
        siteLocation.setLocationName("test_location");
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testGetPromosForSection(){
        GetOfferResponse getOfferResponse = new GetOfferResponse();
        getOfferResponse.setStatusMessage(new StatusMessage(StatusMessage.STATUS.SUCCESS,""));
        Set<String> bay = new HashSet<>();
        bay.add("test_bay");
//        when(siteLocationDAO.getSiteLocationDetail(any(),any(),any(),any())).thenReturn(siteLocation);
        when(validPromoDetailDAO.getValidPromosForLocation(any(),any(),any(),any())).thenReturn(getOfferResponse);
        GetOfferResponse getTestOfferResponse = retailProductService.getPromosForSection("test","test","test","test");
        assertEquals( StatusMessage.STATUS.SUCCESS,getTestOfferResponse.getStatusMessage().getStatus());
//        verify(siteLocationDAO).getSiteLocationDetail(any(String.class),any(String.class),any(String.class),any(String.class));
        verify(validPromoDetailDAO).getValidPromosForLocation(any(String.class),any(String.class),any(String.class),any(String.class));
    }

    /*@Test
    public void testGetPromosForSection_noBay(){
        when(siteLocationDAO.getSiteLocationDetail(any(),any(),any(),any())).thenReturn(siteLocation);
        GetOfferResponse getTestOfferResponse = retailProductService.getPromosForSection("test","test","test","test");
        assertEquals( StatusMessage.STATUS.FAILED_TO_FETCH_OFFERS,getTestOfferResponse.getStatusMessage().getStatus());
        verify(siteLocationDAO).getSiteLocationDetail(any(String.class),any(String.class),any(String.class),any(String.class));
    }*/

   /* @Test
    public void testGetPromosForSection_noSite(){
        when(siteLocationDAO.getSiteLocationDetail(any(),any(),any(),any())).thenReturn(null);
        GetOfferResponse getTestOfferResponse = retailProductService.getPromosForSection("test","test","test","test");
        assertEquals(StatusMessage.STATUS.FAILED_TO_FETCH_OFFERS,getTestOfferResponse.getStatusMessage().getStatus());
        verify(siteLocationDAO).getSiteLocationDetail(any(String.class),any(String.class),any(String.class),any(String.class));
    }*/

    @Test
    public void testSaveOfferResponseDetails(){
        offerResponse.setPromoCode("test_test_test");
        offerResponse.setUserId("test_user");
        offerResponse.setOfferActiveDuration(123L);
        offerResponse.setOfferResponseStatus(null);
        when(siteLocationDAO.getLocationNameByLocationCode(any(),any(),any())).thenReturn("test");
        when(offerResponseDetailDAO.addCustomerAcceptedOfferDetail(any(),any(),any())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS, ""));
        StatusMessage statusMessage = retailProductService.saveOfferResponseDetails(offerResponse,"test","test");
        assertEquals(StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());
        verify(siteLocationDAO).getLocationNameByLocationCode(any(),any(),any());
        verify(offerResponseDetailDAO).addCustomerAcceptedOfferDetail(any(),any(),any());

    }

}
