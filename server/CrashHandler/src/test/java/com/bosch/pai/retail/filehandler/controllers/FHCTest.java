//package com.bosch.pai.retail.filehandler.controllers;
//
//import com.bosch.pai.retail.common.responses.StatusMessage;
//
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.io.IOException;
//
//import static org.junit.Assert.assertEquals;
//
//public class FHCTest {
//
//    @BeforeClass
//    public static void setUpBeforeClass() throws IOException {
//        ReflectionTestUtils.setField(FHC.class,"apk_download_path","/");
//        ReflectionTestUtils.setField(FHC.class,"crash_upload_path","/");
//
//    }
//
//    @Before
//    public void setUp() throws IOException {
//        System.setProperty("crash.upload.path","/");
//        System.setProperty("apk.download.path","/");
//    }
//
//    private FHC fhc = new FHC();
//
//    private MockMultipartFile multipartFile = new MockMultipartFile("test file","test data".getBytes());
//
//    @Test
//    public void testUcr(){
//        ResponseEntity<StatusMessage> response = fhc.ucr(multipartFile,2018,8,8);
//        assertEquals(StatusMessage.STATUS.FAILED_TO_UPLOAD_CRASH_REPORTS,response.getBody().getStatus());
//    }
//
//    @Test
//    public void testIsaua(){
//        ResponseEntity<StatusMessage> response = fhc.isaua("1.1");
//        assertEquals(StatusMessage.STATUS.SUCCESS,response.getBody().getStatus());
//    }
//}
