package com.bosch.pai.retail.session.service;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;
import com.bosch.pai.retail.db.model.SessionDetail;
import com.bosch.pai.retail.db.model.SubSessionDetail;
import com.bosch.pai.retail.session.dao.SessionDetailDAO;
import com.bosch.pai.retail.session.dao.SubSessionDetailDAO;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RetailSessionServiceTest {

    @Mock
    private SessionDetailDAO sessionDetailDAO;

    @Mock
    private SubSessionDetailDAO subSessionDetailDAO;

    @InjectMocks
    private RetailSessionService retailSessionService;

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
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testStartUserSession(){
        when(sessionDetailDAO.startSession(anyString(),any(),any(),anyString())).thenReturn("test");
        when(subSessionDetailDAO.getNonTerminatedSubSessions(anyString(),anyString(),anyString(),anyString())).thenReturn(new ArrayList<>());
        String result = retailSessionService.startUserSession("test_comp","test_store","test_user","test_site","test_loc","android");
        assertEquals("test",result);
        verify(sessionDetailDAO).startSession(anyString(),any(),any(),anyString());
        verify(subSessionDetailDAO).getNonTerminatedSubSessions(anyString(),anyString(),anyString(),anyString());
    }

//    @Test
//    public void testCheckAndUpdateSessionDetails(){
//        when(subSessionDetailDAO.getNonTerminatedSubSessions(anyString(),anyString(),anyString(),anyString())).thenReturn(new ArrayList<>());
//        when(subSessionDetailDAO.saveSubSessionDetail(anyString(),any(),any(),anyString())).thenReturn("test");
//        String result = retailSessionService.checkAndUpdateSessionDetails("test_comp","test_store","test_user","123","test_site",
//                "test_loc","UTC",false,"android");
//        assertEquals("123",result);
//        verify(subSessionDetailDAO).saveSubSessionDetail(anyString(),any(),any(),anyString());
//        verify(subSessionDetailDAO).getNonTerminatedSubSessions(anyString(),anyString(),anyString(),anyString());
//    }
//
//    @Test
//    public void testCheckAndUpdateSessionDetails_ExitLoc(){
//        when(sessionDetailDAO.endSession(anyString(),anyString(),anyString(),anyString(),any(),any())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,"test"));
//        String result = retailSessionService.checkAndUpdateSessionDetails("test_comp","test_store","test_user","123","test_site",
//                "test_loc","UTC",true,"android");
//        assertEquals("123",result);
//        verify(sessionDetailDAO).endSession(anyString(),anyString(),anyString(),anyString(),any(),any());
//    }
//
//    @Test
//    public void testCheckAndUpdateSessionDetails_InvalidSession(){
//        List<String> sessionId = new ArrayList<>();
//        sessionId.add("session1");
//        when(sessionDetailDAO.getNonTerminatedSessions(anyString(),anyString(),anyString())).thenReturn(sessionId);
//        when(sessionDetailDAO.endSession(anyString(),anyString(),anyString(),anyString(),any(),any())).thenReturn(new StatusMessage(StatusMessage.STATUS.SUCCESS,"test"));
//        when(subSessionDetailDAO.saveSubSessionDetail(anyString(),any(),any(),anyString())).thenReturn("test");
//        when(sessionDetailDAO.startSession(anyString(),any(),any(),anyString())).thenReturn("test");
//        String result = retailSessionService.checkAndUpdateSessionDetails("test_comp","test_store","test_user","test","test_site",
//                "test_loc","UTC",true,"android");
//        assertEquals("test",result);
//        verify(sessionDetailDAO).endSession(anyString(),anyString(),anyString(),anyString(),any(),any());
//        verify(sessionDetailDAO).startSession(anyString(),any(),any(),anyString());
//        verify(sessionDetailDAO).getNonTerminatedSessions(anyString(),anyString(),anyString());
//    }

    @Test
    public void testStartSessionSubSession(){
        when(sessionDetailDAO.startSession(anyString(),anyString(),any(),anyString())).thenReturn("test");
        when(subSessionDetailDAO.saveSubSessionDetail(anyString(),anyString(),any(),anyString())).thenReturn("test");
        Map<SessionDetail,List<SubSessionDetail>> sessionDetailListMap = new HashMap<>();
        SessionDetail sessionDetail = new SessionDetail();
        sessionDetail.setEndTime(1550156842305L);
        sessionDetail.setIsValid(true);
        sessionDetail.setSessionId("123");
        sessionDetail.setStartTime(1550136842305L);
        sessionDetail.setStoreId("test_store");
        sessionDetail.setUserId("test_user");

        SubSessionDetail subSessionDetail = new SubSessionDetail();
        subSessionDetail.setHierarchyDetails(new ArrayList<>());
        subSessionDetail.setEndTime(1550156842305L);
        subSessionDetail.setIsValid(true);
        subSessionDetail.setLocationName("test_loc");
        subSessionDetail.setSessionId("123");
        subSessionDetail.setSiteName("test_site");
        subSessionDetail.setStartTime(1550136842305L);
        subSessionDetail.setStoreId("test_store");
        subSessionDetail.setSubSessionId("1234");
        subSessionDetail.setUserId("test_user");
        subSessionDetail.setValid(true);
        List<SubSessionDetail> subSessionDetails = new ArrayList<>();
        subSessionDetails.add(subSessionDetail);
        sessionDetailListMap.put(sessionDetail,subSessionDetails);

        StatusMessage result = retailSessionService.startSessionSubSession("test_comp",sessionDetailListMap,"android");

        assertEquals(StatusMessage.STATUS.SUCCESS,result.getStatus());

    }

}
