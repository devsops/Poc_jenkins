package com.bosch.pai.retail.productoffer.dao;

import com.bosch.pai.retail.adtuning.model.offer.OfferResponseStatus;
import com.bosch.pai.retail.adtuning.model.offer.UserOfferResponse;
import com.bosch.pai.retail.adtuning.model.offer.UserPromoOfferResponse;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;
import com.bosch.pai.retail.configmodel.HierarchyDetail;
import com.bosch.pai.retail.configmodel.SiteLocationHierarchyDetail;
import com.bosch.pai.retail.db.model.OfferResponseDetail;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import org.apache.zookeeper.data.Stat;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.sql.Timestamp;
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
import static org.mockito.Matchers.any;


public class OfferResponseDetailDAOTest {
    private OfferResponseDetailDAO offerResponseDetailDAO;

    @InjectMocks
    private OfferResponseDetail offerResponseDetail = null;

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
        offerResponseDetailDAO = new OfferResponseDetailDAO(mongoOperations);
        mongoOperations.getCollection("test_comp_test_store_offer_response_details").remove(new BasicDBObject());
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        mongoOperations.dropCollection("test_comp_test_store_offer_response_details");
    }

    @Test
    public void testAddCustomerAcceptedOfferDetail(){
        offerResponseDetail.setUserId("test_user");
        offerResponseDetail.setLocationName("test_location");
        offerResponseDetail.setMessageCode("100");
        offerResponseDetail.setOfferActiveDuration(2000L);
        offerResponseDetail.setOfferResponseId("response_id");
        offerResponseDetail.setSiteName("test_site");
        offerResponseDetail.setUserResponseTimeStamp(new Timestamp(1000L));
        StatusMessage statusMessage =  offerResponseDetailDAO.addCustomerAcceptedOfferDetail("test_comp","test_store",offerResponseDetail);
        assertEquals(StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());

        Query query = new Query();
        query.addCriteria(Criteria.where("USER_ID").is(offerResponseDetail.getUserId()));
        DBCollection collection = mongoOperations.getCollection("test_comp_test_store_offer_response_details");
        DBObject offerObj =  collection.findOne();
        assertEquals(offerResponseDetail.getUserId(),offerObj.get("USER_ID"));

    }

    @Test
    public void testAddCustomerAcceptedOfferDetail_invalidCompany(){
        offerResponseDetail.setUserId("test_user");
        offerResponseDetail.setLocationName("test_location");
        offerResponseDetail.setMessageCode("100");
        offerResponseDetail.setOfferActiveDuration(2000L);
        offerResponseDetail.setOfferResponseId("response_id");
        offerResponseDetail.setSiteName("test_site");
        offerResponseDetail.setUserResponseTimeStamp(new Timestamp(1000L));
        StatusMessage statusMessage =  offerResponseDetailDAO.addCustomerAcceptedOfferDetail("test_company","test_store",null);
        assertEquals( StatusMessage.STATUS.FAILED_TO_SEND_OFFER_RESPONSE,statusMessage.getStatus());
    }

    @Test
    public void testaddCompleteCustomerAcceptedOfferDetail(){
        UserOfferResponse userOfferResponse = new UserOfferResponse();
        userOfferResponse.setOfferResponseStatus(OfferResponseStatus.STATUS.ACCEPTED);
        userOfferResponse.setLocationName("test_loc");
        userOfferResponse.setMessageCode("1");
        userOfferResponse.setOfferActiveDuration(123L);
        userOfferResponse.setSiteName("test_site");
        userOfferResponse.setStoreId("test_store");
        userOfferResponse.setUserId("test_user");
        userOfferResponse.setUserResponseTimeStamp(123L);
        List<UserOfferResponse> offerResponseDetail = new ArrayList<>();
        offerResponseDetail.add(userOfferResponse);
        StatusMessage statusMessage = offerResponseDetailDAO.addCompleteCustomerAcceptedOfferDetail("test_comp",offerResponseDetail,"android");
        assertEquals(StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());
    }


    @Test
    public void testaddPromoOfferResponse(){
        UserPromoOfferResponse userPromoOfferResponse = new UserPromoOfferResponse();
        userPromoOfferResponse.setHierarchyDetails(new ArrayList<>());
        userPromoOfferResponse.setLocationName("test_loc");
        userPromoOfferResponse.setMessageCode("1");
        userPromoOfferResponse.setOfferActiveDuration(123L);
        userPromoOfferResponse.setSiteName("test_site");
        userPromoOfferResponse.setStoreId("test_store");
        userPromoOfferResponse.setUserId("test_user");
        List<UserPromoOfferResponse> userPromoOfferResponses= new ArrayList<>();
        userPromoOfferResponses.add(userPromoOfferResponse);

        List<HierarchyDetail> hierarchyDetails = new ArrayList<>();
        HierarchyDetail hierarchyDetail = new HierarchyDetail();
        hierarchyDetail.setHierarchyLevel(1);
        hierarchyDetail.setHierarchyName("test");
        hierarchyDetail.setRequired(true);
        hierarchyDetail.setEntries(new ArrayList<>());
        hierarchyDetails.add(hierarchyDetail);

        SiteLocationHierarchyDetail siteLocationHierarchyDetail = new SiteLocationHierarchyDetail();
        siteLocationHierarchyDetail.setCompanyId("test_comp");
        siteLocationHierarchyDetail.setLocationName("test_loc");
        siteLocationHierarchyDetail.setSiteName("test_site");
        siteLocationHierarchyDetail.setStoreId("test_store");
        siteLocationHierarchyDetail.setHierarchies(hierarchyDetails);
        mongoOperations.insert(siteLocationHierarchyDetail,"test_comp".toUpperCase() + "_" + "location_hierarchy_map".toUpperCase());

        StatusMessage statusMessage = offerResponseDetailDAO.addPromoOfferResponse("test_comp",userPromoOfferResponses,"android");
        assertEquals(StatusMessage.STATUS.SUCCESS,statusMessage.getStatus());
    }
}
