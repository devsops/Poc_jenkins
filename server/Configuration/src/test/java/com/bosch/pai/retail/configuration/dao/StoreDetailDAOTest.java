package com.bosch.pai.retail.configuration.dao;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;
import com.bosch.pai.retail.config.responses.StoreConfigResponse;
import com.bosch.pai.retail.configmodel.StoreConfig;
import com.bosch.pai.retail.db.model.MapLabelCategories;
import com.bosch.pai.retail.db.util.DBUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

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

import java.util.ArrayList;
import java.util.List;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StoreDetailDAOTest {

    private static MongoOperations mongoOperations = null;
    private static MongodExecutable mongodExe;
    private static MongodStarter starter = MongodStarter.getDefaultInstance();
    private static String BIND_IP = "localhost";
    private static int PORT = 12345;
    private StoreDetailDAO storeDetailDAO;
    private static final String COLLECTION = "STORE_CONFIGURATION";


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
        storeDetailDAO = new StoreDetailDAO(mongoOperations);
        mongoOperations.getCollection("TEST_COMP_SITE_CONFIG").remove(new BasicDBObject());
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        mongoOperations.dropCollection("TEST_COMP_SITE_CONFIG");
    }


    @Test
    public void testGetStoreDetails(){
        List storeDetails = storeDetailDAO.getStoreDetails("test_comp","test_store","test_dept","test_cat","test_brand");
        assertEquals(0,storeDetails.size());
    }

    @Test
    public void testGetStoreDetails_Invalidcat(){
        List storeDetails = storeDetailDAO.getStoreDetails("test_comp","test_store","test_dept",null,"test_brand");
        assertEquals(0,storeDetails.size());
    }

    @Test
    public void testGetStoreDetails_InvalidDept(){
        List storeDetails = storeDetailDAO.getStoreDetails("test_comp","test_store",null,"test_cat","test_brand");
        assertEquals(0,storeDetails.size());
    }

    @Test
    public void testGetStoreDetails_InvalidBrand(){
        List storeDetails = storeDetailDAO.getStoreDetails("test_comp","test_store",null,"test_cat",null);
        assertEquals(0,storeDetails.size());
    }

    @Test
    public void testGetStoreConfiguration(){

        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setSiteName("test_site");
        storeConfig.setSnapshotThreshold(12);
        storeConfig.setStoreDescription("test");
        storeConfig.setStoreId("test_store");
        mongoOperations.insert(storeConfig,DBUtil.getCollectionName("test_comp",COLLECTION));
        StoreConfigResponse storeConfigResponse = storeDetailDAO.getStoreConfiguration("test_comp","test_store","test_site");
        assertNotNull(storeConfigResponse);
    }

    @Test
    public void testGetStoreConfiguration_invalidStore(){

        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setSiteName("test_site");
        storeConfig.setSnapshotThreshold(12);
        storeConfig.setStoreDescription("test");
        storeConfig.setStoreId("test_store");
        mongoOperations.insert(storeConfig,DBUtil.getCollectionName("test_comp",COLLECTION));
        StoreConfigResponse storeConfigResponse = storeDetailDAO.getStoreConfiguration("test_comp",null,"test_site");
        assertNotNull(storeConfigResponse);
    }

    @Test
    public void testGetStoreConfiguration_invalidSite(){

        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setSiteName("test_site");
        storeConfig.setSnapshotThreshold(12);
        storeConfig.setStoreDescription("test");
        storeConfig.setStoreId("test_store");
//        mongoOperations.insert(storeConfig,DBUtil.getCollectionName("test_comp",COLLECTION));
        StoreConfigResponse storeConfigResponse = storeDetailDAO.getStoreConfiguration("test_comp","test_store",null);
        assertNotNull(storeConfigResponse);
    }

    @Test
    public void testSaveStoreConfiguration(){

        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setSiteName("test_site");
        storeConfig.setSnapshotThreshold(12);
        storeConfig.setStoreDescription("test");
        storeConfig.setStoreId("test_store");

        StatusMessage statusMessage = storeDetailDAO.saveStoreConfiguration("test_comp",storeConfig);
        assertEquals(StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());
    }

    @Test(expected = Exception.class)
    public void testSaveStoreConfiguration_invalidRequest(){

        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setSiteName("test_site");
        storeConfig.setSnapshotThreshold(12);
        storeConfig.setStoreDescription("test");
        storeConfig.setStoreId("test_store");

        StatusMessage statusMessage = storeDetailDAO.saveStoreConfiguration("test_comp",null);
        assertEquals(StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());
    }

    @Test
    public void testGetCategories(){
        MapLabelCategories mapLabelCategories = new MapLabelCategories();
        mapLabelCategories.setCategoryName("test_cat");
        mongoOperations.insert(mapLabelCategories,"TEST_COMP_TEST_STORE_MAP_LABEL_CATEGORIES");
        List<String> list = storeDetailDAO.getCategories("test_comp","test_store");
        assertEquals(1,list.size());
    }
}
