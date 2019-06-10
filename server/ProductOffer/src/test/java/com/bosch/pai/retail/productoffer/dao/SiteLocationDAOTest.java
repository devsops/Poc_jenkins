package com.bosch.pai.retail.productoffer.dao;

import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;
import com.bosch.pai.retail.configmodel.SiteLocationDetails;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.HashSet;
import java.util.Set;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;




public class SiteLocationDAOTest {
    private SiteLocationDAO siteLocationDAO;

    @InjectMocks
    private SiteLocationDetails siteLocation ;

    private static MongoOperations mongoOperations = null;
    private static MongodExecutable mongodExe;
    static MongodStarter starter = MongodStarter.getDefaultInstance();
    private static String bindIp = "localhost";
    static int port = 12345;



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
        mongoOperations.getCollection("test_company_LOCATION_BAY_MAP").remove(new BasicDBObject());
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        mongoOperations.dropCollection("test_company_LOCATION_BAY_MAP");
    }

    @Test
    public void testGetLocationNameByLocationCode(){
        siteLocation.setSiteName("test_site");
        siteLocation.setLocationName("test_location");
        siteLocation.setCompanyId("test_company");
        siteLocation.setStoreId("test_store");
        Set<String> bay = new HashSet<>();
        bay.add("test_bay");
        mongoOperations.insert(siteLocation,"test_company_LOCATION_BAY_MAP");
        String location  = siteLocationDAO.getLocationNameByLocationCode("test_company","test_site","test_bay");
        assertNull(location);
    }

    @Test
    public void testGetLocationNameByLocationCode_noSite(){
        siteLocation.setSiteName("test_site");
        siteLocation.setLocationName("test_location");
        siteLocation.setCompanyId("test_company");
        siteLocation.setStoreId("test_store");
        Set<String> bay = new HashSet<>();
        bay.add("test_bay");
        mongoOperations.insert(siteLocation,"test_company_LOCATION_BAY_MAP");
        String location  = siteLocationDAO.getLocationNameByLocationCode("test_company2","test_site2","test_bay");
        assertNull(location);
    }


    @Test
    public void testGetSiteLocationDetail(){
        siteLocation.setSiteName("test_site");
        siteLocation.setLocationName("test_location");
        siteLocation.setCompanyId("test_company");
        siteLocation.setStoreId("test_store");
        Set<String> bay = new HashSet<>();
        bay.add("test_bay");
        mongoOperations.insert(siteLocation,"test_company_LOCATION_BAY_MAP");
        SiteLocationDetails testSiteLocation = siteLocationDAO.getSiteLocationDetail("test_company",
                "test_store","test_site","test_location");
        assertNotNull(testSiteLocation);

    }

    @Test
    public void testGetSiteLocationDetail_noLocation(){
        siteLocation.setSiteName("test_site");
        siteLocation.setLocationName("test_location");
        siteLocation.setCompanyId("test_company");
        siteLocation.setStoreId("test_store");
        Set<String> bay = new HashSet<>();
        bay.add("test_bay");
        mongoOperations.insert(siteLocation,"test_company_LOCATION_BAY_MAP");
        SiteLocationDetails testSiteLocation = siteLocationDAO.getSiteLocationDetail("test_company",
                "test_store","test_site",null);
        assertNotNull(testSiteLocation);

    }
}
