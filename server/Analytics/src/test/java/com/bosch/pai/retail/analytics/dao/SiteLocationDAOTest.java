/*
package com.bosch.pai.retail.analytics.dao;

import com.bosch.pai.retail.configmodel.SiteLocation;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class SiteLocationDAOTest {

    private SiteLocationDAO siteLocationDAO;
    private static MongoOperations mongoOperations = null;
    private static MongodExecutable mongodExe;
    private Class<?> classes;
    private static MongodStarter starter = MongodStarter.getDefaultInstance();
    private static String BINDIP = "localhost";
    private static int PORT = 27017;

    private String COMPANY_ID = "PROXIMITY_MARKETING";
    private String STOREID = "20011";
    private String END_POINT = "LOCATION_BAY_MAP";

    private String COLLECTION_NAME = COMPANY_ID + "_" + END_POINT;

    @BeforeClass
    public static void setUpBeforeClass() {
        try {
            IMongodConfig mongodConfig = new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(BINDIP, PORT, Network.localhostIsIPv6()))
                    .build();
            mongodExe = starter.prepare(mongodConfig);
            MongodProcess mongod = mongodExe.start();
            MongoClient mongo = new MongoClient(BINDIP, PORT);
            MongoDbFactory factory = new SimpleMongoDbFactory(mongo, "PROXIMITY_MARKETING");
            mongoOperations = new MongoTemplate(factory);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDownAfterClass() {
        // drop all created collections, indexes and sequence in test db
        if (mongodExe != null)
            mongodExe.stop();
    }

    @Before
    public void setUp() {
        siteLocationDAO = new SiteLocationDAO(mongoOperations);
        classes = siteLocationDAO.getClass();
        mongoOperations.getCollection(COLLECTION_NAME).remove(new BasicDBObject());
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        mongoOperations.dropCollection(COLLECTION_NAME);
    }


    @Test
    public void getSiteLocationListTest() {
        // insert a document and retrieve and check whether that document is present

        final Set<String> bays = new HashSet<>();
        bays.add("11");
        bays.add("12");
        bays.add("13");

        final SiteLocation expectedSiteLocation = new SiteLocation();
        expectedSiteLocation.setCompanyId(COMPANY_ID);
        expectedSiteLocation.setStoreId(STOREID);
        expectedSiteLocation.setSiteName("sampleTestSite");
        expectedSiteLocation.setLocationName("sampleTestLocation");
        expectedSiteLocation.setBays(bays);

        mongoOperations.insert(expectedSiteLocation, COLLECTION_NAME);

        final List<SiteLocation> siteLocationList =
                siteLocationDAO.getSiteLocationList(COMPANY_ID, STOREID);

        assertFalse(siteLocationList.isEmpty());

        final SiteLocation siteLocation = siteLocationList.get(0);

        assertEquals("sampleTestSite", siteLocation.getSiteName());
        assertEquals("sampleTestLocation", siteLocation.getLocationName());
        assertEquals(COMPANY_ID, siteLocation.getCompanyId());
        assertEquals(STOREID, siteLocation.getStoreId());
        assertEquals(bays.size(), siteLocation.getBays().size());

        assertEquals(bays, siteLocation.getBays());

    }

}

*/
