package com.bosch.pai.retail.analytics.dao;

import com.bosch.pai.retail.analytics.model.entryexit.EntryExitDetails;
import com.bosch.pai.retail.analytics.model.entryexit.IntervalDetails;
import com.bosch.pai.retail.analytics.responses.EntryExitResponse;
import com.bosch.pai.retail.db.model.SessionDetail;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;

import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

import static org.junit.Assert.fail;

public class EntryExitDAOTest {

    private EntryExitDAO entryExitDAO;
    private static MongoOperations mongoOperations = null;
    private static MongodExecutable mongodExe;
    private Class<?> classes;
    private static MongodStarter starter = MongodStarter.getDefaultInstance();
    private static String BINDIP = "localhost";
    private static int PORT = 27017;

    private String COMPANY_ID = "PROXIMITY_MARKETING";
    private String STOREID = "20011";
    private String END_POINT = "session_details";

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
        entryExitDAO = new EntryExitDAO(mongoOperations);
        classes = entryExitDAO.getClass();
        mongoOperations.getCollection(COLLECTION_NAME).remove(new BasicDBObject());
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        mongoOperations.dropCollection(COLLECTION_NAME);
    }

    private static final Long startTime = 1502895600000L;

    @Test
    public void getEntryExit_Hourly() {

        SessionDetail userSession = new SessionDetail();
        userSession.setUserId("PROXIMITY");
        userSession.setStartTime(new Date(1502895600000L).getTime());
        userSession.setEndTime(new Date(1502895600000L + 50000).getTime());

        mongoOperations.insert(userSession, COLLECTION_NAME);

        final EntryExitResponse entryExitResponse =
                entryExitDAO.getEntryExit(COMPANY_ID, STOREID, "HOURLY",
                        getServerTimestamp(startTime - 50000), getServerTimestamp(startTime + 100000),"android");

        Assert.assertNotNull(entryExitResponse);

        final EntryExitDetails entryExitDetails = entryExitResponse.getEntryExitDetails();

        Assert.assertNotNull(entryExitDetails);

        Assert.assertNotNull(entryExitDetails.getTotalEntryCount());
        Assert.assertNotNull(entryExitDetails.getTotalExitCount());

        Assert.assertEquals(1L, (long) entryExitDetails.getTotalEntryCount());
        Assert.assertEquals(1L, (long) entryExitDetails.getTotalExitCount());
    }


    @Test
    public void getEntryExit_Daily() {

        SessionDetail userSession = new SessionDetail();
        userSession.setUserId("PROXIMITY");
        userSession.setStartTime(new Date(1502895600000L).getTime());
        userSession.setEndTime(new Date(1502895600000L + 50000).getTime());

        mongoOperations.insert(userSession, COLLECTION_NAME);

        final EntryExitResponse entryExitResponse =
                entryExitDAO.getEntryExit(COMPANY_ID, STOREID, "DAILY",
                        getServerTimestamp(startTime - 50000), getServerTimestamp(startTime + 100000),"android");

        Assert.assertNotNull(entryExitResponse);

        final EntryExitDetails entryExitDetails = entryExitResponse.getEntryExitDetails();

        Assert.assertNotNull(entryExitDetails);

        Assert.assertNotNull(entryExitDetails.getTotalEntryCount());
        Assert.assertNotNull(entryExitDetails.getTotalExitCount());

        Assert.assertEquals(1L, (long) entryExitDetails.getTotalEntryCount());
        Assert.assertEquals(1L, (long) entryExitDetails.getTotalExitCount());
    }

    @Test
    public void getEntryExit_Monthly() {

        SessionDetail userSession = new SessionDetail();
        userSession.setUserId("PROXIMITY");
        userSession.setStartTime(new Date(1502895600000L).getTime());
        userSession.setEndTime(new Date(1502895600000L + 50000).getTime());

        mongoOperations.insert(userSession, COLLECTION_NAME);

        final EntryExitResponse entryExitResponse =
                entryExitDAO.getEntryExit(COMPANY_ID, STOREID, "MONTHLY",
                        getServerTimestamp(startTime - 50000), getServerTimestamp(startTime + 100000),"android");

        Assert.assertNotNull(entryExitResponse);

        final EntryExitDetails entryExitDetails = entryExitResponse.getEntryExitDetails();

        Assert.assertNotNull(entryExitDetails);

        Assert.assertNotNull(entryExitDetails.getTotalEntryCount());
        Assert.assertNotNull(entryExitDetails.getTotalExitCount());

        Assert.assertEquals(1L, (long) entryExitDetails.getTotalEntryCount());
        Assert.assertEquals(1L, (long) entryExitDetails.getTotalExitCount());
    }

    @Test
    public void getEntryExit_Yearly() {

        SessionDetail userSession = new SessionDetail();
        userSession.setUserId("PROXIMITY");
        userSession.setStartTime(new Date(1502895600000L).getTime());
        userSession.setEndTime(new Date(1502895600000L + 50000).getTime());

        mongoOperations.insert(userSession, COLLECTION_NAME);

        final EntryExitResponse entryExitResponse =
                entryExitDAO.getEntryExit(COMPANY_ID, STOREID, "YEARLY",
                        getServerTimestamp(startTime - 50000), getServerTimestamp(startTime + 100000),"android");

        Assert.assertNotNull(entryExitResponse);

        final EntryExitDetails entryExitDetails = entryExitResponse.getEntryExitDetails();

        Assert.assertNotNull(entryExitDetails);

        Assert.assertNotNull(entryExitDetails.getTotalEntryCount());
        Assert.assertNotNull(entryExitDetails.getTotalExitCount());

        Assert.assertEquals(1L, (long) entryExitDetails.getTotalEntryCount());
        Assert.assertEquals(1L, (long) entryExitDetails.getTotalExitCount());
    }

    @Test
    public void getEntryExit_Empty() {

        final EntryExitResponse entryExitResponse =
                entryExitDAO.getEntryExit(COMPANY_ID, STOREID, null,
                        getServerTimestamp(startTime - 50000), getServerTimestamp(startTime + 100000),"android");

        Assert.assertNotNull(entryExitResponse);

        final EntryExitDetails entryExitDetails = entryExitResponse.getEntryExitDetails();

        Assert.assertNull(entryExitDetails);

    }

   /* @Test
    public void getAggregationForYearTest() {

        try {
            final Method method = classes.getDeclaredMethod("getAggregationForYear",
                    FacetOperation.class, ProjectionOperation.class, GroupOperation.class);
            method.setAccessible(true);
            final Aggregation aggregation = Whitebox.invokeMethod(entryExitDAO,
                    "getAggregationForYear", new FacetOperation(),
                    new ProjectionOperation(), new GroupOperation(Fields.fields("year")));

            Assert.assertNotNull(aggregation);
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void getAggregationForMonthTest() {

        try {
            final Method method = classes.getDeclaredMethod("getAggregationForMonth",
                    FacetOperation.class, ProjectionOperation.class, GroupOperation.class);
            method.setAccessible(true);
            final Aggregation aggregation = Whitebox.invokeMethod(entryExitDAO,
                    "getAggregationForMonth", new FacetOperation(),
                    new ProjectionOperation(), new GroupOperation(Fields.fields("year")));

            Assert.assertNotNull(aggregation);
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void getAggregationForDayTest() {

        try {
            final Method method = classes.getDeclaredMethod("getAggregationForDay",
                    FacetOperation.class, ProjectionOperation.class, GroupOperation.class);
            method.setAccessible(true);
            final Aggregation aggregation = Whitebox.invokeMethod(entryExitDAO,
                    "getAggregationForDay", new FacetOperation(),
                    new ProjectionOperation(), new GroupOperation(Fields.fields("year")));

            Assert.assertNotNull(aggregation);
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void getAggregationForHours() {

        try {
            final Method method = classes.getDeclaredMethod("getAggregationForHours",
                    FacetOperation.class, ProjectionOperation.class, GroupOperation.class);
            method.setAccessible(true);
            final Aggregation aggregation = Whitebox.invokeMethod(entryExitDAO,
                    "getAggregationForHours", new FacetOperation(),
                    new ProjectionOperation(), new GroupOperation(Fields.fields("year")));

            Assert.assertNotNull(aggregation);
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void getProjectionBasedOnRequestInterval() {
        try {
            final Method method = classes.getDeclaredMethod("getProjectionBasedOnRequestInterval",
                    IntervalDetails.class);
            method.setAccessible(true);
            final ProjectionOperation requestInterval = Whitebox.invokeMethod(entryExitDAO,
                    "getProjectionBasedOnRequestInterval", IntervalDetails.HOURLY);

            Assert.assertNotNull(requestInterval);
        } catch (Exception e) {
            fail();
        }

    }


    @Test
    public void getProjectionBasedOnRequestIntervalTest() {
        try {
            final Method method = classes.getDeclaredMethod("getProjectionBasedOnRequestInterval",
                    IntervalDetails.class, String.class);
            method.setAccessible(true);
            final ProjectionOperation projectionOperation = Whitebox.invokeMethod(entryExitDAO,
                    "getProjectionBasedOnRequestInterval", IntervalDetails.HOURLY, "START_TIME");

            Assert.assertNotNull(projectionOperation);
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void getGroupBasedOnRequestIntervalTest() {
        try {
            final Method method = classes.getDeclaredMethod("getGroupBasedOnRequestInterval",
                    IntervalDetails.class);
            method.setAccessible(true);
            final GroupOperation groupOperation = Whitebox.invokeMethod(entryExitDAO,
                    "getGroupBasedOnRequestInterval", IntervalDetails.HOURLY);
            Assert.assertNotNull(groupOperation);
        } catch (Exception e) {
            fail();
        }

    }


    @Test
    public void getRequestIntervalTest() {

        try {
            final Method method = classes.getDeclaredMethod("getRequestInterval",
                    String.class, Timestamp.class, Timestamp.class);
            method.setAccessible(true);
            final IntervalDetails intervalDetails = Whitebox.invokeMethod(entryExitDAO,
                    "getRequestInterval", "HOURLY", getServerTimestamp(1502895600000L),
                    getServerTimestamp(1660662000000L));

            Assert.assertEquals(IntervalDetails.HOURLY.name(), intervalDetails.name());
        } catch (Exception e) {
            fail();
        }

    }


    @Test
    public void findRequestIntervalYearsTest() {
        try {
            final Method method = classes.getDeclaredMethod("findRequestInterval", Integer.TYPE,
                    Integer.TYPE, Integer.TYPE);
            method.setAccessible(true);
            final IntervalDetails intervalDetails = Whitebox.invokeMethod(entryExitDAO,
                    "findRequestInterval", 2, 02, 12);

            Assert.assertEquals(IntervalDetails.YEARLY.name(), intervalDetails.name());
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void findRequestIntervalMonthsTest() {
        try {
            final Method method = classes.getDeclaredMethod("findRequestInterval", int.class, int.class, int.class);
            method.setAccessible(true);
            final IntervalDetails intervalDetails = Whitebox.invokeMethod(entryExitDAO,
                    "findRequestInterval", 0, 2, 12);

            Assert.assertEquals(IntervalDetails.MONTHLY.name(), intervalDetails.name());
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void findRequestIntervalDailyTest() {
        try {
            final Method method = classes.getDeclaredMethod("findRequestInterval",
                    Integer.TYPE, Integer.TYPE, Integer.TYPE);
            method.setAccessible(true);
            final IntervalDetails intervalDetails = Whitebox.invokeMethod(entryExitDAO,
                    "findRequestInterval", 0, 0, 12);

            Assert.assertEquals(IntervalDetails.DAILY.name(), intervalDetails.name());
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void findRequestIntervalHourlyTest() {
        try {
            final Method method = classes.getDeclaredMethod("findRequestInterval",
                    Integer.TYPE, Integer.TYPE, Integer.TYPE);
            method.setAccessible(true);
            final IntervalDetails intervalDetails = Whitebox.invokeMethod(entryExitDAO,
                    "findRequestInterval", 0, 0, 0);

            Assert.assertEquals(IntervalDetails.HOURLY.name(), intervalDetails.name());
        } catch (Exception e) {
            fail();
        }

    }*/



    private Timestamp getServerTimestamp(Long timestamp) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("GMT"));
        return Timestamp.from(zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toInstant());
    }

}
