package com.bosch.pai.retail.session.dao;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;
import com.bosch.pai.retail.db.model.SessionDetail;
import com.bosch.pai.retail.db.model.SubSessionDetail;
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

import java.util.ArrayList;
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
import static org.junit.Assert.assertNotNull;

public class SessionDetailDAOTest {

    @InjectMocks
    private SessionDetailDAO sessionDetailDAO;

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
        sessionDetailDAO = new SessionDetailDAO(mongoOperations);
        mongoOperations.getCollection("test_comp_test_store_session_details").remove(new BasicDBObject());
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        mongoOperations.dropCollection("test_comp_test_store_session_details");
    }

    @Test
    public void testStartSession(){
        SessionDetail sessionDetail = new SessionDetail();
        sessionDetail.setEndTime(1550156842305L);
        sessionDetail.setIsValid(true);
        sessionDetail.setSessionId("123");
        sessionDetail.setStartTime(1550136842305L);
        sessionDetail.setStoreId("test_store");
        sessionDetail.setUserId("test_user");
        String result = sessionDetailDAO.startSession("test_comp","test_store",sessionDetail,"android");
        assertNotNull(result);
    }

    @Test
    public void testStartCompleteSession(){
        Map<SessionDetail,List<SubSessionDetail>> sessionDetailListMap = new HashMap<>();
        SessionDetail sessionDetail = new SessionDetail();
        sessionDetail.setEndTime(1550156842305L);
        sessionDetail.setIsValid(true);
        sessionDetail.setSessionId("123");
        sessionDetail.setStartTime(1550136842305L);
        sessionDetail.setStoreId("test_store");
        sessionDetail.setUserId("test_user");

        SubSessionDetail subSessionDetail = new SubSessionDetail();
        subSessionDetail.setHierarchyDetails(new ArrayList<>());
        subSessionDetail.setEndTime(1550156842305L);
        subSessionDetail.setIsValid(true);
        subSessionDetail.setLocationName("test_loc");
        subSessionDetail.setSessionId("123");
        subSessionDetail.setSiteName("test_site");
        subSessionDetail.setStartTime(1550136842305L);
        subSessionDetail.setStoreId("test_store");
        subSessionDetail.setSubSessionId("1234");
        subSessionDetail.setUserId("test_user");
        subSessionDetail.setValid(true);
        List<SubSessionDetail> subSessionDetails = new ArrayList<>();
        subSessionDetails.add(subSessionDetail);
        sessionDetailListMap.put(sessionDetail,subSessionDetails);

        StatusMessage statusMessage = sessionDetailDAO.startCompleteSession("test_comp",sessionDetailListMap);
        assertEquals(StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());
    }

    @Test
    public void testGetNonTerminatedSessions(){
        SessionDetail sessionDetail = new SessionDetail();
        sessionDetail.setIsValid(true);
        sessionDetail.setSessionId("123");
        sessionDetail.setStartTime(1550136842305L);
        sessionDetail.setStoreId("test_store");
        sessionDetail.setUserId("test_user");

        mongoOperations.insert(sessionDetail,"test_comp_test_store_session_details");
        List<String> results = sessionDetailDAO.getNonTerminatedSessions("test_comp","test_store","test_user");
        assertEquals(1,results.size());
    }
}
