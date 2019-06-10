package com.bosch.pai.retail.analytics.dao;

import com.bosch.pai.retail.analytics.model.dwelltime.LocationDwellTime;
import com.bosch.pai.retail.analytics.model.heatmap.HeatMapDetail;
import com.bosch.pai.retail.analytics.responses.UserDwellTimeAnalyticsResponse;
import com.bosch.pai.retail.analytics.responses.UserHeatMapAnalyticsResponse;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;
import com.bosch.pai.retail.db.model.SubSessionDetail;
import com.bosch.pai.retail.encodermodel.EncoderException;
import com.bosch.pai.retail.encodermodel.EncoderUtil;
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
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class SubSessionDetailDAOTest {

    private SubSessionDetailDAO subSessionDetailDAO;

    private static MongoOperations mongoOperations = null;
    private static MongodExecutable mongodExe;
    private static MongodStarter starter = MongodStarter.getDefaultInstance();
    private static String BINDIP = "localhost";
    private static int PORT = 27017;

    private Class<?> classes;

    private String COMPANY_ID = "PROXIMITY_MARKETING";
    private String STOREID = "20011";
    private String END_POINT = "sub_session_details";

    private String COLLECTION_NAME = COMPANY_ID + "_" + STOREID + "_" + END_POINT;

    @BeforeClass
    public static void setUpBeforeClass() {
        try {
            final IMongodConfig mongodConfig = new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(BINDIP, PORT, Network.localhostIsIPv6()))
                    .build();
            mongodExe = starter.prepare(mongodConfig);
            final MongodProcess mongod = mongodExe.start();
            final MongoClient mongo = new MongoClient(BINDIP, PORT);
            final MongoDbFactory factory = new SimpleMongoDbFactory(mongo, "PROXIMITY_MARKETING");
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
        RequestContext requestContext = new RequestContext();
        requestContext.setUserId("test");
        ContextHolder.setContext(requestContext);
        subSessionDetailDAO = new SubSessionDetailDAO(mongoOperations);
        mongoOperations.getCollection(COLLECTION_NAME).remove(new BasicDBObject());
        classes = subSessionDetailDAO.getClass();
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        mongoOperations.dropCollection(COLLECTION_NAME);
    }

    @Test
    public void getHeatMapDetailsTest() throws EncoderException, NoSuchFieldException, IllegalAccessException {


        final Field validDurationField = classes.getDeclaredField("validDuration");
        validDurationField.setAccessible(true);
        validDurationField.set(subSessionDetailDAO, new Integer(30));
        final Integer validDuration = (Integer) validDurationField.get(subSessionDetailDAO);

        Assert.assertEquals(new Integer(30), validDuration);

        final String sampleTestSite = "sampleTestSite";
        final String sampleTestLocation = "sampleTestLocation";
        String encodedLocation = EncoderUtil.encode(sampleTestLocation);

        final Set<String> locations = new HashSet<>();
        locations.add(sampleTestLocation);

        Long startTime = 1511790268000L;
        Long endTime = 1511797468000L;

        final SubSessionDetail subsession = new SubSessionDetail();
        subsession.setIsValid(true);
        subsession.setUserId("PROXIMITY");
        subsession.setLocationName(encodedLocation);
        subsession.setSiteName(sampleTestSite);
        subsession.setSessionId("1234567");
        subsession.setStartTime(new Date(startTime).getTime());
        subsession.setEndTime(new Date(endTime).getTime());

        mongoOperations.insert(subsession, COLLECTION_NAME);

        Long queryStartTime = 1509111868000L;
        Long queryEndTime = 1514382268000L;

        List<HeatMapDetail> heatMapDetailList = subSessionDetailDAO.getHeatMaps(getServerTimestamp(queryStartTime),
                getServerTimestamp(queryEndTime), COMPANY_ID, STOREID, sampleTestSite, sampleTestLocation, locations,"android");

        Assert.assertFalse(heatMapDetailList.isEmpty());

        final HeatMapDetail heatMapDetail = heatMapDetailList.get(0);

        Assert.assertEquals(COMPANY_ID, heatMapDetail.getCompanyName());
        Assert.assertEquals(STOREID, heatMapDetail.getStoreId());
        Assert.assertEquals(sampleTestSite, heatMapDetail.getSiteName());
        Assert.assertEquals(new Integer(1), heatMapDetail.getUserCount());
        Assert.assertEquals(sampleTestLocation, heatMapDetail.getLocationName());
    }

    @Test
    public void getHeatMapDetailsTest_EmptyLoc() throws EncoderException, NoSuchFieldException, IllegalAccessException {


        final Field validDurationField = classes.getDeclaredField("validDuration");
        validDurationField.setAccessible(true);
        validDurationField.set(subSessionDetailDAO, new Integer(30));
        final Integer validDuration = (Integer) validDurationField.get(subSessionDetailDAO);

        Assert.assertEquals(new Integer(30), validDuration);

        final String sampleTestSite = "sampleTestSite";
        final String sampleTestLocation = "sampleTestLocation";
        String encodedLocation = EncoderUtil.encode(sampleTestLocation);

        final Set<String> locations = new HashSet<>();
        locations.add(sampleTestLocation);

        Long startTime = 1511790268000L;
        Long endTime = 1511797468000L;

        final SubSessionDetail subsession = new SubSessionDetail();
        subsession.setIsValid(true);
        subsession.setUserId("PROXIMITY");
        subsession.setLocationName(encodedLocation);
        subsession.setSiteName(sampleTestSite);
        subsession.setSessionId("1234567");
        subsession.setStartTime(new Date(startTime).getTime());
        subsession.setEndTime(new Date(endTime).getTime());

        mongoOperations.insert(subsession, COLLECTION_NAME);

        Long queryStartTime = 1509111868000L;
        Long queryEndTime = 1514382268000L;

        List<HeatMapDetail> heatMapDetailList = subSessionDetailDAO.getHeatMaps(getServerTimestamp(queryStartTime),
                getServerTimestamp(queryEndTime), COMPANY_ID, STOREID, sampleTestSite, null, locations,"android");

        Assert.assertFalse(heatMapDetailList.isEmpty());

        final HeatMapDetail heatMapDetail = heatMapDetailList.get(0);

        Assert.assertEquals(COMPANY_ID, heatMapDetail.getCompanyName());
        Assert.assertEquals(STOREID, heatMapDetail.getStoreId());
        Assert.assertEquals(sampleTestSite, heatMapDetail.getSiteName());
        Assert.assertEquals(new Integer(1), heatMapDetail.getUserCount());
        Assert.assertEquals(sampleTestLocation, heatMapDetail.getLocationName());
    }

    @Test
    public void getDwellTimeDetailsTest() throws EncoderException, NoSuchFieldException, IllegalAccessException {

        final Field dwellTimeUnitField = classes.getDeclaredField("dwellTimeUnit");
        dwellTimeUnitField.setAccessible(true);
        dwellTimeUnitField.set(subSessionDetailDAO, "minutes");
        final String dwellTimeUnit = (String) dwellTimeUnitField.get(subSessionDetailDAO);

        Assert.assertEquals("minutes", dwellTimeUnit);

        final Field validDurationField = classes.getDeclaredField("validDuration");
        validDurationField.setAccessible(true);
        validDurationField.set(subSessionDetailDAO, new Integer(30));
        final Integer validDuration = (Integer) validDurationField.get(subSessionDetailDAO);

        Assert.assertEquals(new Integer(30), validDuration);

        final String sampleTestSite = "sampleTestSite";
        final String sampleTestLocation = "sampleTestLocation";
        String encodedLocation = EncoderUtil.encode(sampleTestLocation);

        final Set<String> locations = new HashSet<>();
        locations.add(sampleTestLocation);

        Long startTime = 1511790268000L;
        Long endTime = 1511797468000L;

        final SubSessionDetail subsession = new SubSessionDetail();
        subsession.setIsValid(true);
        subsession.setUserId("PROXIMITY");
        subsession.setLocationName(encodedLocation);
        subsession.setSiteName(sampleTestSite);
        subsession.setSessionId("1234567");
        subsession.setStartTime(new Date(startTime).getTime());
        subsession.setEndTime(new Date(endTime).getTime());

        mongoOperations.insert(subsession, COLLECTION_NAME);

        Long queryStartTime = 1509111868000L;
        Long queryEndTime = 1514382268000L;

        List<LocationDwellTime> locationDwellTimes = subSessionDetailDAO.getDwellTimeDetails(getServerTimestamp(queryStartTime),
                getServerTimestamp(queryEndTime), COMPANY_ID, STOREID, sampleTestSite, sampleTestLocation, locations,"android");

        Assert.assertFalse(locationDwellTimes.isEmpty());

        final LocationDwellTime locationDwellTime = locationDwellTimes.get(0);

        Assert.assertEquals(COMPANY_ID, locationDwellTime.getCompanyId());
        Assert.assertEquals(STOREID, locationDwellTime.getStoreId());
        Assert.assertEquals(sampleTestSite, locationDwellTime.getSiteName());

        Assert.assertEquals(new Integer(1), locationDwellTime.getUserCount());
        Assert.assertNotNull(locationDwellTime.getAverageDuration());

        Assert.assertEquals(sampleTestLocation, locationDwellTime.getLocationName());
    }


    @Test
    public void getDwellTimeDetailsTest_InvalidLoc() throws EncoderException, NoSuchFieldException, IllegalAccessException {

        final Field dwellTimeUnitField = classes.getDeclaredField("dwellTimeUnit");
        dwellTimeUnitField.setAccessible(true);
        dwellTimeUnitField.set(subSessionDetailDAO, "minutes");
        final String dwellTimeUnit = (String) dwellTimeUnitField.get(subSessionDetailDAO);

        Assert.assertEquals("minutes", dwellTimeUnit);

        final Field validDurationField = classes.getDeclaredField("validDuration");
        validDurationField.setAccessible(true);
        validDurationField.set(subSessionDetailDAO, new Integer(30));
        final Integer validDuration = (Integer) validDurationField.get(subSessionDetailDAO);

        Assert.assertEquals(new Integer(30), validDuration);

        final String sampleTestSite = "sampleTestSite";
        final String sampleTestLocation = "sampleTestLocation";
        String encodedLocation = EncoderUtil.encode(sampleTestLocation);

        final Set<String> locations = new HashSet<>();
        locations.add(sampleTestLocation);

        Long startTime = 1511790268000L;
        Long endTime = 1511797468000L;

        final SubSessionDetail subsession = new SubSessionDetail();
        subsession.setIsValid(true);
        subsession.setUserId("PROXIMITY");
        subsession.setLocationName(encodedLocation);
        subsession.setSiteName(sampleTestSite);
        subsession.setSessionId("1234567");
        subsession.setStartTime(new Date(startTime).getTime());
        subsession.setEndTime(new Date(endTime).getTime());

        mongoOperations.insert(subsession, COLLECTION_NAME);

        Long queryStartTime = 1509111868000L;
        Long queryEndTime = 1514382268000L;

        List<LocationDwellTime> locationDwellTimes = subSessionDetailDAO.getDwellTimeDetails(getServerTimestamp(queryStartTime),
                getServerTimestamp(queryEndTime), COMPANY_ID, STOREID, sampleTestSite, null, locations,"android");

        Assert.assertFalse(locationDwellTimes.isEmpty());

        final LocationDwellTime locationDwellTime = locationDwellTimes.get(0);

        Assert.assertEquals(COMPANY_ID, locationDwellTime.getCompanyId());
        Assert.assertEquals(STOREID, locationDwellTime.getStoreId());
        Assert.assertEquals(sampleTestSite, locationDwellTime.getSiteName());

        Assert.assertEquals(new Integer(1), locationDwellTime.getUserCount());
        Assert.assertNotNull(locationDwellTime.getAverageDuration());

        Assert.assertEquals(sampleTestLocation, locationDwellTime.getLocationName());
    }


    @Test
    public void testGetHierarchyDwellTime() throws Exception{
        final Field dwellTimeUnitField = classes.getDeclaredField("dwellTimeUnit");
        dwellTimeUnitField.setAccessible(true);
        dwellTimeUnitField.set(subSessionDetailDAO, "minutes");
        final String dwellTimeUnit = (String) dwellTimeUnitField.get(subSessionDetailDAO);

        Assert.assertEquals("minutes", dwellTimeUnit);

        final Field validDurationField = classes.getDeclaredField("validDuration");
        validDurationField.setAccessible(true);
        validDurationField.set(subSessionDetailDAO, new Integer(30));
        final Integer validDuration = (Integer) validDurationField.get(subSessionDetailDAO);

        Assert.assertEquals(new Integer(30), validDuration);

        final String sampleTestSite = "sampleTestSite";
        final String sampleTestLocation = "sampleTestLocation";
        String encodedLocation = EncoderUtil.encode(sampleTestLocation);

        final Set<String> locations = new HashSet<>();
        locations.add(sampleTestLocation);

        Long startTime = 1511790268000L;
        Long endTime = 1511797468000L;

        final SubSessionDetail subsession = new SubSessionDetail();
        subsession.setIsValid(true);
        subsession.setUserId("PROXIMITY");
        subsession.setLocationName(encodedLocation);
        subsession.setSiteName(sampleTestSite);
        subsession.setSessionId("1234567");
        subsession.setStartTime(new Date(startTime).getTime());
        subsession.setEndTime(new Date(endTime).getTime());

        mongoOperations.insert(subsession, COLLECTION_NAME);

        Long queryStartTime = 1509111868000L;
        Long queryEndTime = 1514382268000L;

        List<UserDwellTimeAnalyticsResponse> userDwellTimeAnalyticsResponses = subSessionDetailDAO.getHierarchyDwellTime(COMPANY_ID,STOREID,sampleTestSite,startTime,endTime,"android",null);

        assertNotNull(userDwellTimeAnalyticsResponses);

    }


    @Test
    public void testGetHierarchyHeatMap() throws Exception{
        final Field dwellTimeUnitField = classes.getDeclaredField("dwellTimeUnit");
        dwellTimeUnitField.setAccessible(true);
        dwellTimeUnitField.set(subSessionDetailDAO, "minutes");
        final String dwellTimeUnit = (String) dwellTimeUnitField.get(subSessionDetailDAO);

        Assert.assertEquals("minutes", dwellTimeUnit);

        final Field validDurationField = classes.getDeclaredField("validDuration");
        validDurationField.setAccessible(true);
        validDurationField.set(subSessionDetailDAO, new Integer(30));
        final Integer validDuration = (Integer) validDurationField.get(subSessionDetailDAO);

        Assert.assertEquals(new Integer(30), validDuration);

        final String sampleTestSite = "sampleTestSite";
        final String sampleTestLocation = "sampleTestLocation";
        String encodedLocation = EncoderUtil.encode(sampleTestLocation);

        Long startTime = 1511790268000L;
        Long endTime = 1511797468000L;

        final SubSessionDetail subsession = new SubSessionDetail();
        subsession.setIsValid(true);
        subsession.setUserId("PROXIMITY");
        subsession.setLocationName(encodedLocation);
        subsession.setSiteName(sampleTestSite);
        subsession.setSessionId("1234567");
        subsession.setStartTime(new Date(startTime).getTime());
        subsession.setEndTime(new Date(endTime).getTime());

        mongoOperations.insert(subsession, COLLECTION_NAME);

        List<UserHeatMapAnalyticsResponse> userHeatMapAnalyticsResponses = subSessionDetailDAO.getHierarchyHeatMap(COMPANY_ID,STOREID,sampleTestSite,startTime,endTime,"android",null);

        assertNotNull(userHeatMapAnalyticsResponses);

    }

    @Test
    public void testGetHierarchyHeatMap_Hierarchy() throws Exception{
        final Field dwellTimeUnitField = classes.getDeclaredField("dwellTimeUnit");
        dwellTimeUnitField.setAccessible(true);
        dwellTimeUnitField.set(subSessionDetailDAO, "minutes");
        final String dwellTimeUnit = (String) dwellTimeUnitField.get(subSessionDetailDAO);

        Assert.assertEquals("minutes", dwellTimeUnit);

        final Field validDurationField = classes.getDeclaredField("validDuration");
        validDurationField.setAccessible(true);
        validDurationField.set(subSessionDetailDAO, new Integer(30));
        final Integer validDuration = (Integer) validDurationField.get(subSessionDetailDAO);

        Assert.assertEquals(new Integer(30), validDuration);

        final String sampleTestSite = "sampleTestSite";
        final String sampleTestLocation = "sampleTestLocation";
        String encodedLocation = EncoderUtil.encode(sampleTestLocation);


        Long startTime = 1511790268000L;
        Long endTime = 1511797468000L;

        final SubSessionDetail subsession = new SubSessionDetail();
        subsession.setIsValid(true);
        subsession.setUserId("PROXIMITY");
        subsession.setLocationName(encodedLocation);
        subsession.setSiteName(sampleTestSite);
        subsession.setSessionId("1234567");
        subsession.setStartTime(new Date(startTime).getTime());
        subsession.setEndTime(new Date(endTime).getTime());

        mongoOperations.insert(subsession, COLLECTION_NAME);

        Map<String, List<String>> hierarchyLevelNameMap = new HashMap<>();
        List<String> val = new ArrayList<>();
        val.add("1");
        hierarchyLevelNameMap.put("1",val);

        List<UserHeatMapAnalyticsResponse> userHeatMapAnalyticsResponses = subSessionDetailDAO.getHierarchyHeatMap(COMPANY_ID,STOREID,sampleTestSite,startTime,endTime,"android",hierarchyLevelNameMap);

        assertNotNull(userHeatMapAnalyticsResponses);

    }

    @Test
    public void testGetHierarchyDwellTime_Hierarchy() throws Exception{
        final Field dwellTimeUnitField = classes.getDeclaredField("dwellTimeUnit");
        dwellTimeUnitField.setAccessible(true);
        dwellTimeUnitField.set(subSessionDetailDAO, "minutes");
        final String dwellTimeUnit = (String) dwellTimeUnitField.get(subSessionDetailDAO);

        Assert.assertEquals("minutes", dwellTimeUnit);

        final Field validDurationField = classes.getDeclaredField("validDuration");
        validDurationField.setAccessible(true);
        validDurationField.set(subSessionDetailDAO, new Integer(30));
        final Integer validDuration = (Integer) validDurationField.get(subSessionDetailDAO);

        Assert.assertEquals(new Integer(30), validDuration);

        final String sampleTestSite = "sampleTestSite";
        final String sampleTestLocation = "sampleTestLocation";
        String encodedLocation = EncoderUtil.encode(sampleTestLocation);


        Long startTime = 1511790268000L;
        Long endTime = 1511797468000L;

        final SubSessionDetail subsession = new SubSessionDetail();
        subsession.setIsValid(true);
        subsession.setUserId("PROXIMITY");
        subsession.setLocationName(encodedLocation);
        subsession.setSiteName(sampleTestSite);
        subsession.setSessionId("1234567");
        subsession.setStartTime(new Date(startTime).getTime());
        subsession.setEndTime(new Date(endTime).getTime());

        mongoOperations.insert(subsession, COLLECTION_NAME);

        Map<String, List<String>> hierarchyLevelNameMap = new HashMap<>();
        List<String> val = new ArrayList<>();
        val.add("1");
        hierarchyLevelNameMap.put("1",val);

        List<UserDwellTimeAnalyticsResponse> userDwellTimeAnalyticsResponses = subSessionDetailDAO.getHierarchyDwellTime(COMPANY_ID,STOREID,sampleTestSite,startTime,endTime,"android",hierarchyLevelNameMap);

        assertNotNull(userDwellTimeAnalyticsResponses);

    }





/*
    @Test
    public void prepareDwellTimeAggregationTest() {
        try {
            final Method method = classes.getDeclaredMethod("prepareDwellTimeAggregation", Criteria.class, ConditionalOperators.Switch.class);
            method.setAccessible(true);
            final Aggregation aggregation = Whitebox.invokeMethod(subSessionDetailDAO, "prepareDwellTimeAggregation",
                    new Criteria(), ConditionalOperators.switchCases());

            assertNotNull(aggregation);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void prepareHeatMapAggregationTest() {
        try {
            final Method method = classes.getDeclaredMethod("prepareHeatMapAggregation", Criteria.class, ConditionalOperators.Switch.class);
            method.setAccessible(true);
            final Aggregation aggregation = Whitebox.invokeMethod(subSessionDetailDAO, "prepareHeatMapAggregation",
                    new Criteria(), ConditionalOperators.switchCases());

            assertNotNull(aggregation);
        } catch (Exception e) {
            fail();
        }
    }


    @Test
    public void getMatchCriteriaTest() {
        try {
            final Method method = classes.getDeclaredMethod("getMatchCriteria", Timestamp.class, Timestamp.class, String.class);
            method.setAccessible(true);
            final Criteria criteria = Whitebox.invokeMethod(subSessionDetailDAO, "getMatchCriteria",
                    getServerTimestamp(1502895600000L), getServerTimestamp(1660662000000L),
                    "sampleTestSite");

            assertNotNull(criteria);
        } catch (Exception e) {
            fail();
        }
    }


    @Test
    public void getEncodedLocationMapTest() {
        try {
            final Set<String> set = new HashSet<>();
            set.add("abc");
            set.add("def");
            set.add("ghi");

            final Method method = classes.getDeclaredMethod("getEncodedLocationMap", Set.class);
            method.setAccessible(true);
            final Map<String, List<String>> map = Whitebox.invokeMethod(subSessionDetailDAO, "getEncodedLocationMap", set);

            assertNotNull(map);
            assertEquals(set.size(), map.size());
        } catch (Exception e) {
            fail();
        }
    }
*/

    private long getServerTimestamp(Long timestamp) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("GMT"));
        return Timestamp.from(zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toInstant()).getTime();
    }

}

