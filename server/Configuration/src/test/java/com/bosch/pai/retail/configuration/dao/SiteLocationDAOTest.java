package com.bosch.pai.retail.configuration.dao;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;
import com.bosch.pai.retail.configmodel.BaymapDetail;
import com.bosch.pai.retail.configmodel.HierarchyDetail;
import com.bosch.pai.retail.configmodel.LocationCateDeptBrand;
import com.bosch.pai.retail.configmodel.SiteLocationDetails;
import com.bosch.pai.retail.configmodel.SiteLocationHierarchyDetail;
import com.bosch.pai.retail.configuration.Exception.InvalidUrlException;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


/**
 * Created by hug5kor on 11/21/2017.
 */


public class SiteLocationDAOTest {
    private SiteLocationDAO siteLocationDAO;
    private static MongoOperations mongoOperations = null;
    private static MongodExecutable mongodExe;
    private static MongodStarter starter = MongodStarter.getDefaultInstance();
    private static String bindIp = "localhost";
    private static int port = 12345;

    @Mock
    private SiteLocationDAO mockSiteLocationDAO;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(bindIp, port, Network.localhostIsIPv6()))
                .build();
        mongodExe = starter.prepare(mongodConfig);
        MongodProcess mongod = mongodExe.start();
        MongoClient mongo = new MongoClient(bindIp, port);
        MongoDbFactory factory = new SimpleMongoDbFactory(mongo,"test_embedded");
        mongoOperations = new MongoTemplate(factory);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // drop all created collections, indexes and sequence in test db
        if (mongodExe != null)
            mongodExe.stop();
    }


    @Before
    public void setUp() throws Exception {
        RequestContext requestContext = new RequestContext();
        requestContext.setUserId("test");
        ContextHolder.setContext(requestContext);
        siteLocationDAO = new SiteLocationDAO(mongoOperations);
        mongoOperations.getCollection("TEST_COMP_LOCATION_HIERARCHY_MAP").remove(new BasicDBObject());
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        mongoOperations.dropCollection("TEST_COMP_LOCATION_HIERARCHY_MAP");
    }

    @Test
    public void testSaveSiteLocationsDetails(){
        Set<String> bayList = new HashSet<>();
        bayList.add("test_bay");
        LocationCateDeptBrand locationCateDeptBrand = new LocationCateDeptBrand();
        locationCateDeptBrand.setLocationBrands(new HashSet<>());
        locationCateDeptBrand.setLocationCateDeptBrands(new HashSet<>());
        locationCateDeptBrand.setLocationCategorys(new HashSet<>());
        locationCateDeptBrand.setLocationDepartments(new HashSet<>());
        locationCateDeptBrand.setLocationType("");
        SiteLocationDetails siteLocationDetails = new SiteLocationDetails("test_comp","test_store","test_site","test_loc",locationCateDeptBrand);
        mongoOperations.insert(siteLocationDetails,"TEST_COMP_LOCATION_HIERARCHY_MAP");
        StatusMessage statusMessage = siteLocationDAO.saveSiteLocationsDetails("test_comp","test_store","test_site","test_location",locationCateDeptBrand,"android",true);
        assertEquals("testSaveSiteLocationsDetails",StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());
        /*List<SiteLocationDetails> siteLocations = mongoOperations.findAll(SiteLocationDetails.class,"TEST_COMP_LOCATION_HIERARCHY_MAP");
        SiteLocationDetails siteLocation = (SiteLocationDetails) siteLocations.toArray()[0];
        assertEquals("test_site",siteLocation.getSiteName());
        assertEquals("test_location",siteLocation.getLocationName());
        assertEquals("test_store",siteLocation.getStoreId());*/
    }

    @Test
    public void testGetSiteLocationDetails(){
        WriteResult writeResult =null;
        List<String> bayList = new ArrayList<>();
        bayList.add("test_bay");
        final BasicDBObject query = new BasicDBObject();
        query.put("storeId", "test_store");
        query.put("siteName", "test_site");
        query.put("locationName", "test_location");

        final BasicDBObject update = new BasicDBObject();
        update.put("storeId", "test_store");
        update.put("siteName", "test_site");
        update.put("locationName", "test_location");
        update.put("bayList", bayList);

        try {
             writeResult = mongoOperations.getCollection("TEST_COMP_LOCATION_HIERARCHY_MAP")
                    .update(query, update, true, false);
        }catch (Exception e){}
        if(writeResult.getN()==1){
            List<SiteLocationDetails> siteLocations = siteLocationDAO.getSiteLocationDetails("test_comp","test_store","test_site","test_location","android");
            assertEquals(siteLocations.size(),1);
        }
    }

    @Test
    public void testGetSiteLocationDetails_noSiteName(){
        WriteResult writeResult =null;
        List<String> bayList = new ArrayList<>();
        bayList.add("test_bay");
        final BasicDBObject query = new BasicDBObject();
        query.put("storeId", "test_store");
        query.put("siteName", "test_site");
        query.put("locationName", "test_location");

        final BasicDBObject update = new BasicDBObject();
        update.put("storeId", "test_store");
        update.put("siteName", "test_site");
        update.put("locationName", "test_location");
        update.put("bayList", bayList);

        try {
            writeResult = mongoOperations.getCollection("TEST_COMP_LOCATION_HIERARCHY_MAP")
                    .update(query, update, true, false);
        }catch (Exception e){
            assertFalse("Inside Exception",!e.getMessage().isEmpty());
        }
        if(writeResult.getN()==1){
            List<SiteLocationDetails> siteLocations = siteLocationDAO.getSiteLocationDetails("test_comp","test_store",null,"test_location","android");
            assertEquals(siteLocations.size(),1);
        }
    }

    @Test
    public void testGetSiteLocationDetails_noLocName(){
        WriteResult writeResult =null;
        List<String> bayList = new ArrayList<>();
        bayList.add("test_bay");
        final BasicDBObject query = new BasicDBObject();
        query.put("storeId", "test_store");
        query.put("siteName", "test_site");
        query.put("locationName", "test_location");

        final BasicDBObject update = new BasicDBObject();
        update.put("storeId", "test_store");
        update.put("siteName", "test_site");
        update.put("locationName", "test_location");
        update.put("bayList", bayList);

        try {
            writeResult = mongoOperations.getCollection("TEST_COMP_LOCATION_HIERARCHY_MAP")
                    .update(query, update, true, false);
            update.removeField("locationName");
            update.put("locationName", "test_location2");
            query.removeField("locationName");
            query.put("locationName", "test_location2");
            writeResult = mongoOperations.getCollection("TEST_COMP_LOCATION_HIERARCHY_MAP")
                    .update(query, update, true, false);

        }catch (Exception e){}
        if(writeResult.getN()==1){
            List<SiteLocationDetails> siteLocations = siteLocationDAO.getSiteLocationDetails("test_comp","test_store","test_site",null,"android");
            assertEquals(siteLocations.size(),2);
        }
    }

    @Test
    public void testGetSiteLocationDetails_emptyData(){
        List<SiteLocationDetails> siteLocations = siteLocationDAO.getSiteLocationDetails("test_comp","test_store","test_site","test_location","android");
        assertEquals(siteLocations.size(),0);
    }

    @Test
    public void testDeleteSiteLocationDetails(){
        WriteResult writeResult =null;
        List<String> bayList = new ArrayList<>();
        bayList.add("test_bay");
        final BasicDBObject query = new BasicDBObject();
        query.put("storeId", "test_store");
        query.put("siteName", "test_site");
        query.put("locationName", "test_location");

        final BasicDBObject update = new BasicDBObject();
        update.put("storeId", "test_store");
        update.put("siteName", "test_site");
        update.put("locationName", "test_location");
        update.put("bayList", bayList);

        try {
            writeResult = mongoOperations.getCollection("TEST_COMP_LOCATION_HIERARCHY_MAP")
                    .update(query, update, true, false);
        }catch (Exception e){}
        if(writeResult.getN()==1){
            StatusMessage statusMessage = siteLocationDAO.deleteSiteLocationDetails("test_comp","test_store","test_site","test_location","android");
            assertEquals(statusMessage.getStatus(), StatusMessage.STATUS.SUCCESS);
        }
    }

    @Test
    public void testDeleteSiteLocationDetails_noSite(){
        WriteResult writeResult =null;
        List<String> bayList = new ArrayList<>();
        bayList.add("test_bay");
        final BasicDBObject query = new BasicDBObject();
        query.put("storeId", "test_store");
        query.put("siteName", "test_site");
        query.put("locationName", "test_location");

        final BasicDBObject update = new BasicDBObject();
        update.put("storeId", "test_store");
        update.put("siteName", "test_site");
        update.put("locationName", "test_location");
        update.put("bayList", bayList);

        try {
            writeResult = mongoOperations.getCollection("TEST_COMP_LOCATION_HIERARCHY_MAP")
                    .update(query, update, true, false);
        }catch (Exception e){}
        if(writeResult.getN()==1){
            StatusMessage statusMessage = siteLocationDAO.deleteSiteLocationDetails("test_comp","test_store",null,"test_location","android");
            assertEquals( StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());
        }
    }
    @Test
    public void testDeleteSiteLocationDetails_noLoc(){
        WriteResult writeResult =null;
        List<String> bayList = new ArrayList<>();
        bayList.add("test_bay");
        final BasicDBObject query = new BasicDBObject();
        query.put("storeId", "test_store");
        query.put("siteName", "test_site");
        query.put("locationName", "test_location");

        final BasicDBObject update = new BasicDBObject();
        update.put("storeId", "test_store");
        update.put("siteName", "test_site");
        update.put("locationName", "test_location");
        update.put("bayList", bayList);

        try {
            writeResult = mongoOperations.getCollection("TEST_COMP_LOCATION_HIERARCHY_MAP")
                    .update(query, update, true, false);
        }catch (Exception e){}
        if(writeResult.getN()==1){
            StatusMessage statusMessage = siteLocationDAO.deleteSiteLocationDetails("test_comp","test_store","test_site",null,"android");
            assertEquals( StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());
        }
    }

    @Test
    public void testDeleteSiteLocationDetails_emptyData(){
        StatusMessage statusMessage = siteLocationDAO.deleteSiteLocationDetails("test_comp","test_store","test_site","test_location","android");
        assertEquals(StatusMessage.STATUS.FAILURE,statusMessage.getStatus());
    }

    @Test
    public void testGetSiteLocationDetail(){
        ResponseEntity response = siteLocationDAO.getSiteLocationDetail("test_comp","test_store","test_site","test_loc","android");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetSiteLocationDetail_site(){
        ResponseEntity response = siteLocationDAO.getSiteLocationDetail("test_comp","test_store","test_site",null,"android");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test(expected = InvalidUrlException.class)
    public void testGetSiteLocationDetail_InvalidCompanyId(){
        ResponseEntity response = siteLocationDAO.getSiteLocationDetail(null,"test_store","test_site",null,"test");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testSaveLocationBayMap(){
        Set<String> bays = new HashSet<>();
        bays.add("test_bay");
        BaymapDetail baymapDetail = new BaymapDetail("test_comp","test_store","test_site","test_loc",bays);
        mongoOperations.insert(baymapDetail,"TEST_COMP_LOCATION_HIERARCHY_MAP");
        StatusMessage statusMessage = siteLocationDAO.saveLocationBayMap("test_comp","test_store","test_site","test_loc2",bays,"android",true);
        assertEquals(StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());
    }

    @Test
    public void testSaveSiteLocationsHierarchyMappingDetails(){
        List<HierarchyDetail> hierarchyDetails = new ArrayList<>();
        HierarchyDetail hierarchyDetail = new HierarchyDetail();
        hierarchyDetail.setHierarchyLevel(1);
        hierarchyDetail.setHierarchyName("test");
        hierarchyDetail.setRequired(true);
        hierarchyDetail.setEntries(new ArrayList<>());
        hierarchyDetails.add(hierarchyDetail);
        StatusMessage statusMessage = siteLocationDAO.saveSiteLocationsHierarchyMappingDetails("test_comp","test_store","test_site","test_loc",hierarchyDetails,"android");
        assertEquals(StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());
    }

    @Test(expected = InvalidUrlException.class)
    public void testSaveSiteLocationsHierarchyMappingDetails_InvalidStoreId(){
        List<HierarchyDetail> hierarchyDetails = new ArrayList<>();
        HierarchyDetail hierarchyDetail = new HierarchyDetail();
        hierarchyDetail.setHierarchyLevel(1);
        hierarchyDetail.setHierarchyName("test");
        hierarchyDetail.setRequired(true);
        hierarchyDetail.setEntries(new ArrayList<>());
        hierarchyDetails.add(hierarchyDetail);
        StatusMessage statusMessage = siteLocationDAO.saveSiteLocationsHierarchyMappingDetails("test_comp",null,"test_site","test_loc",hierarchyDetails,"android");
        assertEquals(StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());
    }

    @Test
    public void testGetSiteLocationHierarchyMapping(){
        List<HierarchyDetail> hierarchyDetails = new ArrayList<>();
        HierarchyDetail hierarchyDetail = new HierarchyDetail();
        hierarchyDetail.setHierarchyLevel(1);
        hierarchyDetail.setHierarchyName("test");
        hierarchyDetail.setRequired(true);
        hierarchyDetail.setEntries(new ArrayList<>());
        hierarchyDetails.add(hierarchyDetail);
        SiteLocationHierarchyDetail siteLocationHierarchyDetail = new SiteLocationHierarchyDetail("test_comp","test_store","test_site","test_loc",hierarchyDetails);
        mongoOperations.insert(siteLocationHierarchyDetail,"TEST_COMP_LOCATION_HIERARCHY_MAP");
        List<SiteLocationHierarchyDetail> siteLocationHierarchyDetails = siteLocationDAO.getSiteLocationHierarchyMapping("test_comp","test_store","test_site","test_loc","android");
        assertEquals(1,siteLocationHierarchyDetails.size());
    }

    @Test(expected = InvalidUrlException.class)
    public void testGetSiteLocationHierarchyMapping_InvalidStoreId(){
        List<HierarchyDetail> hierarchyDetails = new ArrayList<>();
        HierarchyDetail hierarchyDetail = new HierarchyDetail();
        hierarchyDetail.setHierarchyLevel(1);
        hierarchyDetail.setHierarchyName("test");
        hierarchyDetail.setRequired(true);
        hierarchyDetail.setEntries(new ArrayList<>());
        hierarchyDetails.add(hierarchyDetail);
        SiteLocationHierarchyDetail siteLocationHierarchyDetail = new SiteLocationHierarchyDetail("test_comp","test_store","test_site","test_loc",hierarchyDetails);
        mongoOperations.insert(siteLocationHierarchyDetail,"TEST_COMP_LOCATION_HIERARCHY_MAP");
        List<SiteLocationHierarchyDetail> siteLocationHierarchyDetails = siteLocationDAO.getSiteLocationHierarchyMapping("test_comp",null,"test_site","test_loc","android");
        assertEquals(1,siteLocationHierarchyDetails.size());
    }
}
