package com.bosch.pai.retail.configuration.dao;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.configmodel.SiteLocations;
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

public class LocationsDAOTest {

    private LocationsDAO locationsDAO;
    private static MongoOperations mongoOperations = null;
    private static MongodExecutable mongodExe;
    private static MongodStarter starter = MongodStarter.getDefaultInstance();
    private static String BIND_IP = "localhost";
    private static int PORT = 12345;



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
        locationsDAO = new LocationsDAO(mongoOperations);
        mongoOperations.getCollection("TEST_COMP_LOCATIONS").remove(new BasicDBObject());
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        mongoOperations.dropCollection("TEST_COMP_LOCATIONS");
    }

    @Test
    public void testGetAllLocations() {

        Set<String> locations = new HashSet<>();
        locations.add("test_loc");
        SiteLocations siteLocations = new SiteLocations("test_comp","test_store","test_site",locations);
        mongoOperations.insert(siteLocations,"TEST_COMP_LOCATIONS");
        List<SiteLocations> siteLocationsList = locationsDAO.getAllLocations("test_comp","test_store","test_site","android");
        assertEquals(1,siteLocationsList.size());
    }

    @Test
    public void testSaveOrUpdateLocations(){
        Set<String> locations = new HashSet<>();
        locations.add("test_loc");
        StatusMessage statusMessage = locationsDAO.saveOrUpdateLocations("test_comp","test_store","test_site",locations,"android");
        assertEquals(StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());
    }
}
