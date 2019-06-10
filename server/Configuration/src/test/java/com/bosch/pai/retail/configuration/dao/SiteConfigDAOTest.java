package com.bosch.pai.retail.configuration.dao;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;
import com.bosch.pai.retail.configmodel.ConfigModel;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

import static org.junit.Assert.assertEquals;


/**
 * Created by hug5kor on 11/21/2017.
 */


public class SiteConfigDAOTest {

    private SiteConfigDAO siteConfigDAO;
    private static MongoOperations mongoOperations = null;
    private static MongodExecutable mongodExe;
    private static MongodStarter starter = MongodStarter.getDefaultInstance();
    private static String BIND_IP = "localhost";
    private static int PORT = 12345;
    private static final String COLLECTION = "locations";



    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(BIND_IP, PORT, Network.localhostIsIPv6()))
                .build();
        mongodExe = starter.prepare(mongodConfig);
        MongodProcess mongod = mongodExe.start();
        MongoClient mongo = new MongoClient(BIND_IP, PORT);
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
        siteConfigDAO = new SiteConfigDAO(mongoOperations);
        mongoOperations.getCollection("TEST_COMP_SITE_CONFIG").remove(new BasicDBObject());
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        mongoOperations.dropCollection("TEST_COMP_SITE_CONFIG");
    }

    @Test
    public void testSaveSiteConfigDetails(){
        Map<String, String> map = new HashMap<>();
        map.put("test_key","test_value");
        StatusMessage sm = siteConfigDAO.saveSiteConfigDetails("test_comp","test_store","test_site",map,"android");
        assertEquals(sm.getStatus(), StatusMessage.STATUS.SUCCESS);
        Query query = new Query();
        query.addCriteria(Criteria.where("storeId").is("test_store"));
        List<ConfigModel> cm = mongoOperations.findAll(ConfigModel.class,"TEST_COMP_SITE_CONFIG");
        ConfigModel testcm = (ConfigModel) cm.toArray()[0];
        assertEquals("test_site",testcm.getSiteName());
        assertEquals("test_store",testcm.getStoreId());
        assertEquals(map,testcm.getSiteConfigMap());
    }


    @Test
    public void testGetSiteConfigDetails (){
        WriteResult writeResult = null;
        Map<String, String> map = new HashMap<>();
        map.put("test_key","test_value");
        final BasicDBObject query = new BasicDBObject();
        query.put("storeId", "test_store");
        query.put("siteName", "test_site");

        final BasicDBObject update = new BasicDBObject();
        update.put("storeId", "test_store");
        update.put("siteName", "test_site");
        update.put("siteConfigMap", map);
        try {
            writeResult = mongoOperations.getCollection("TEST_COMP_SITE_CONFIG")
                    .update(query, update, true, false);

        }catch (Exception ex){

        }
        if(writeResult.getN() == 1){
            List<ConfigModel> cm = siteConfigDAO.getSiteConfigDetails("test_comp","test_store","test_site","android");
            assertEquals(cm.size(),1);
        }
    }

    @Test
    public void testGetSiteConfigDetails_noData(){
        List<ConfigModel> cm = siteConfigDAO.getSiteConfigDetails("test_comp","test_store","test_site","android");
        assertEquals(cm.size(),0);
    }

    @Test
    public void testDeleteSiteConfigDetails() {
        WriteResult writeResult = null;
        Map<String, String> map = new HashMap<>();
        map.put("test_key", "test_value");
        final BasicDBObject query = new BasicDBObject();
        query.put("storeId", "test_store");
        query.put("siteName", "test_site");

        final BasicDBObject update = new BasicDBObject();
        update.put("storeId", "test_store");
        update.put("siteName", "test_site");
        update.put("siteConfigMap", map);
        try {
            writeResult = mongoOperations.getCollection("TEST_COMP_SITE_CONFIG")
                    .update(query, update, true, false);

        } catch (Exception ex) {

        }
        if (writeResult.getN() == 1) {
            StatusMessage statusMessage = siteConfigDAO.deleteSiteConfigDetails("test_comp","test_store","test_site","android");
            assertEquals(statusMessage.getStatus(), StatusMessage.STATUS.SUCCESS);
        }
    }

    @Test
    public void testDeleteSiteConfigDetails_noData() {
            StatusMessage statusMessage = siteConfigDAO.deleteSiteConfigDetails("test_comp","test_store","test_site","android");
            assertEquals(statusMessage.getStatus(), StatusMessage.STATUS.FAILURE);
    }



}
