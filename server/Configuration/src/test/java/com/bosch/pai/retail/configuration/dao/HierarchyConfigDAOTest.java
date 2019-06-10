package com.bosch.pai.retail.configuration.dao;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.configmodel.HierarchyDetail;
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

public class HierarchyConfigDAOTest {

    private HierarchyConfigDAO hierarchyConfigDAO;
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
        hierarchyConfigDAO = new HierarchyConfigDAO(mongoOperations);
        mongoOperations.getCollection("TEST_COMP_TEST_STORE_HIERARCHY_DETAILS").remove(new BasicDBObject());
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        mongoOperations.dropCollection("TEST_COMP_TEST_STORE_HIERARCHY_DETAILS");
    }


    @Test
    public void testSaveHierarchies(){

        HierarchyDetail hierarchyDetail = new HierarchyDetail();
        hierarchyDetail.setEntries(new ArrayList<>());
        hierarchyDetail.setRequired(true);
        hierarchyDetail.setHierarchyName("test_hierarchy");
        hierarchyDetail.setHierarchyLevel(1);
        List<HierarchyDetail> hierarchyDetails = new ArrayList<>();
        hierarchyDetails.add(hierarchyDetail);
        StatusMessage statusMessage = hierarchyConfigDAO.saveHierarchies("test_comp","test_store",hierarchyDetails,"android");

        assertEquals(StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());
    }


    @Test
    public void testGetHierarchies(){
        HierarchyDetail hierarchyDetail = new HierarchyDetail();
        hierarchyDetail.setEntries(new ArrayList<>());
        hierarchyDetail.setRequired(true);
        hierarchyDetail.setHierarchyName("test_hierarchy");
        hierarchyDetail.setHierarchyLevel(1);
        List<HierarchyDetail> hierarchyDetails = new ArrayList<>();
        hierarchyDetails.add(hierarchyDetail);
        hierarchyConfigDAO.saveHierarchies("test_comp","test_store",hierarchyDetails,"android");
        List<HierarchyDetail> hierarchyDetailsList = hierarchyConfigDAO.getHierarchies("test_comp","test_store","android");
        assertEquals(1,hierarchyDetailsList.size());
    }
}
