package com.bosch.pai.retail.analytics.dao;

import com.bosch.pai.retail.adtuning.model.offer.OfferResponseStatus;
import com.bosch.pai.retail.adtuning.model.offer.UserOfferResponse;
import com.bosch.pai.retail.analytics.responses.OfferAnalyticsResponse;
import com.bosch.pai.retail.analytics.responses.UserOfferAnalyticsResponse;
import com.bosch.pai.retail.encodermodel.EncoderUtil;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class OfferResponseDAOTest {

    private OfferResponseDAO offerResponseDAO;
    private static MongoOperations mongoOperations = null;
    private static MongodExecutable mongodExe;
    private static MongodStarter starter = MongodStarter.getDefaultInstance();
    private static String BINDIP = "localhost";
    private static int PORT = 27017;

    private Class<?> classes;

    private String COMPANY_ID = "PROXIMITY_MARKETING";
    private String STOREID = "20011";
    private String END_POINT = "offer_response_details";

    private String COLLECTION_NAME = COMPANY_ID + "_" + STOREID + "_" + END_POINT;

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
        offerResponseDAO = new OfferResponseDAO(mongoOperations);
        classes = offerResponseDAO.getClass();
        mongoOperations.getCollection(COLLECTION_NAME).remove(new BasicDBObject());
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        mongoOperations.dropCollection(COLLECTION_NAME);
    }

    @Test
    public void goaTest() throws Exception{

        final Long startTime = 1502895600000L;
        final long displayedOfferCount = 1;
        final long acceptedOfferCount = 1;


        final String sampleTestSite = "sampleTestSite";
        final String sampleTestLocation = "sampleTestLocation";

        final UserOfferResponse userOfferResponse = new UserOfferResponse();
        userOfferResponse.setUserId("PROXIMITY");
        userOfferResponse.setOfferActiveDuration(20000L);
        userOfferResponse.setUserResponseTimeStamp(new Timestamp(System.currentTimeMillis()).getTime());
        userOfferResponse.setSiteName(sampleTestSite);
        userOfferResponse.setLocationName(EncoderUtil.encode(sampleTestLocation));
        userOfferResponse.setMessageCode("123456");

        mongoOperations.insert(userOfferResponse, COLLECTION_NAME);

        final Long endTime = new Timestamp(System.currentTimeMillis()).getTime();

        List<OfferAnalyticsResponse> offerAnalyticsResponseList =
                offerResponseDAO.getOfferAnalytics(getServerTimestamp(startTime), getServerTimestamp(endTime),
                        COMPANY_ID, STOREID, sampleTestSite, sampleTestLocation,new HashSet<>(),"android");

        assertTrue(offerAnalyticsResponseList.isEmpty());

    }

    @Test
    public void goaTest_InvalidLoc() throws Exception{

        final Long startTime = 1502895600000L;
        final long displayedOfferCount = 1;
        final long acceptedOfferCount = 1;


        final String sampleTestSite = "sampleTestSite";
        final String sampleTestLocation = "sampleTestLocation";
        Set<String> locations = new HashSet<>();
        locations.add("test");
        final UserOfferResponse userOfferResponse = new UserOfferResponse();
        userOfferResponse.setUserId("PROXIMITY");
        userOfferResponse.setOfferActiveDuration(20000L);
        userOfferResponse.setUserResponseTimeStamp(new Timestamp(System.currentTimeMillis()).getTime());
        userOfferResponse.setSiteName(sampleTestSite);
        userOfferResponse.setLocationName(EncoderUtil.encode(sampleTestLocation));
        userOfferResponse.setMessageCode("123456");

        mongoOperations.insert(userOfferResponse, COLLECTION_NAME);

        final Long endTime = new Timestamp(System.currentTimeMillis()).getTime();

        List<OfferAnalyticsResponse> offerAnalyticsResponseList =
                offerResponseDAO.getOfferAnalytics(getServerTimestamp(startTime), getServerTimestamp(endTime),
                        COMPANY_ID, STOREID, sampleTestSite, null,locations,"android");

        assertTrue(offerAnalyticsResponseList.isEmpty());

    }


    @Test
    public void testGetOfferAnalytics() throws Exception{
        final UserOfferResponse userOfferResponse = new UserOfferResponse();
        userOfferResponse.setUserId("PROXIMITY");
        userOfferResponse.setOfferActiveDuration(20000L);
        userOfferResponse.setUserResponseTimeStamp(new Timestamp(System.currentTimeMillis()).getTime());
        userOfferResponse.setSiteName("test");
        userOfferResponse.setLocationName(EncoderUtil.encode("test"));
        userOfferResponse.setMessageCode("123456");
        final Long endTime = new Timestamp(System.currentTimeMillis()).getTime();
        mongoOperations.insert(userOfferResponse, COLLECTION_NAME);
        List<UserOfferAnalyticsResponse> userOfferAnalyticsResponses = offerResponseDAO.getOfferAnalytics(123L,endTime,"test",COMPANY_ID,STOREID,null,"android");

        assertNotNull(userOfferAnalyticsResponses);

    }

    @Test
    public void testGetOfferAnalytics_Hierarchies() throws Exception{
        final UserOfferResponse userOfferResponse = new UserOfferResponse();
        userOfferResponse.setUserId("PROXIMITY");
        userOfferResponse.setOfferActiveDuration(20000L);
        userOfferResponse.setUserResponseTimeStamp(new Timestamp(System.currentTimeMillis()).getTime());
        userOfferResponse.setSiteName("test");
        userOfferResponse.setLocationName("test");
        userOfferResponse.setLocationName(EncoderUtil.encode("test"));
        userOfferResponse.setMessageCode("123456");
        userOfferResponse.setOfferResponseStatus(OfferResponseStatus.STATUS.ACCEPTED);
        final Long endTime = new Timestamp(System.currentTimeMillis()).getTime();
        mongoOperations.insert(userOfferResponse, COLLECTION_NAME);
        Map<String, List<String>> hierarchyLevelNameMap = new HashMap<>();
        List<String> val = new ArrayList<>();
        val.add("1");
        hierarchyLevelNameMap.put("1",val);
        List<UserOfferAnalyticsResponse> userOfferAnalyticsResponses = offerResponseDAO.getOfferAnalytics(123L,endTime,"test",COMPANY_ID,STOREID,hierarchyLevelNameMap,"android");

        assertNotNull(userOfferAnalyticsResponses);

    }

    /*@Test
    public void getMatchCriteriaTest() {
        try {
            final Method method = classes.getDeclaredMethod("getMatchCriteria", Timestamp.class,
                    Timestamp.class, String.class, String.class);
            method.setAccessible(true);
            final Criteria criteria = Whitebox.invokeMethod(offerResponseDAO, "getMatchCriteria",
                    getServerTimestamp(1502895600000L), getServerTimestamp(1660662000000L),
                    "sampleTestSite", "sampleTestLocation");

            assertNotNull(criteria);
        } catch (Exception e) {
            fail();
        }
    }*/


    private long getServerTimestamp(Long timestamp) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("GMT"));
        return Timestamp.from(zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toInstant()).getTime();
    }
}
