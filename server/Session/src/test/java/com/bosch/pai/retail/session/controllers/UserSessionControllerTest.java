package com.bosch.pai.retail.session.controllers;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;
import com.bosch.pai.retail.session.service.RetailSessionService;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserSessionControllerTest {

    @Mock
    private RetailSessionService retailSessionService;

    @InjectMocks
    private UserSessionController userSessionController;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        RequestContext requestContext = new RequestContext();
        requestContext.setUserId("test");
        ContextHolder.setContext(requestContext);
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testsSS(){
        String request = "[[{\"userId\" : \"admin\",\"endTime\" : 1574173265538,\"sessionId\":\"276C4C99-61D4-45B7-AD8C-09242A84A0B1\",\"storeId\" : \"123\",\"startTime\" : 1574153265538,\"isValid\" : true}, [{\"isValid\" : true,\"storeId\" : \"\",\"userId\" : \"admin\",\"siteName\" : \"TestWifi\",\"startTime\" : 1574153265539,\"endTime\" : 1574173265538,\"locationName\" : \"Two\"}]]]";
        when(retailSessionService.startSessionSubSession(anyString(),any(),anyString())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,"test_success"));
        ResponseEntity<StatusMessage> responseEntity = userSessionController.sSS("test_company",request,"android");
        Assert.assertEquals(StatusMessage.STATUS.SUCCESS,responseEntity.getBody().getStatus());
        verify(retailSessionService).startSessionSubSession(any(),any(),any());

    }

}
