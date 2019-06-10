package com.bosch.pai.retail.productoffer.dao;

import com.bosch.pai.retail.adtuning.model.offer.PromoDetail;
import com.bosch.pai.retail.adtuning.responses.GetOfferResponse;
import com.bosch.pai.retail.adtuning.responses.PromoMapOfferResponse;
import com.bosch.pai.retail.adtuning.responses.PromoOfferResponse;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;
import com.bosch.pai.retail.productoffer.model.ValidPromoDetail;
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

import java.util.Date;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;



public class ValidPromoDetailDAOTest {
    private ValidPromoDetailDAO validPromoDetailDAO;

    @InjectMocks
    ValidPromoDetail validPromoDetail;

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
        validPromoDetailDAO = new ValidPromoDetailDAO(mongoOperations);
        mongoOperations.getCollection("test_company_test_store_valid_promo_details").remove(new BasicDBObject());
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        mongoOperations.dropCollection("test_company_test_store_valid_promo_details");
    }

    @Test
    public void testGetValidPromosForLocation(){
        validPromoDetail.setLocationName("test_location");
        validPromoDetail.setDisplayMessage("test_message");
        validPromoDetail.setLocationCode("test_code");
        validPromoDetail.setMessageCode("test_msg_code");
        validPromoDetail.setRank(2);
        validPromoDetail.setSiteName("test_site");
        Date date = new Date();
        validPromoDetail.setPromoStartDate(date);
        validPromoDetail.setPromoEndDate(new Date(date.getTime()+(1000*60*60*24)));
        mongoOperations.insert(validPromoDetail,"test_company_test_store_valid_promo_details");
        Set<String> bay = new HashSet<>();
        bay.add("test_code");
        GetOfferResponse getOfferResponse = validPromoDetailDAO.getValidPromosForLocation("test_company","test_store","test_site","test_location");
        assertEquals(StatusMessage.STATUS.SUCCESS,getOfferResponse.getStatusMessage().getStatus());
        }

    @Test
    public void testGetValidPromosForLocation_noBayDetails(){
        validPromoDetail.setLocationName("test_location");
        validPromoDetail.setDisplayMessage("test_message");
        validPromoDetail.setLocationCode("test_code");
        validPromoDetail.setMessageCode("test_msg_code");
        validPromoDetail.setRank(2);
        validPromoDetail.setSiteName("test_site");
        Date date = new Date();
        validPromoDetail.setPromoStartDate(date);
        validPromoDetail.setPromoEndDate(new Date(date.getTime()+(1000*60*60*24)));
        mongoOperations.insert(validPromoDetail,"test_company_test_store_valid_promo_details");
        GetOfferResponse getOfferResponse = validPromoDetailDAO.getValidPromosForLocation("test_company","test_store","test_site","test_location");
        assertEquals(getOfferResponse.getPromoDetailList().size(),0);
        assertEquals(StatusMessage.STATUS.SUCCESS,getOfferResponse.getStatusMessage().getStatus());
    }

    @Test
    public void testGetValidPromosForLocation_emptyPromo(){
        validPromoDetail.setLocationName("test_location");
        validPromoDetail.setDisplayMessage("test_message");
        validPromoDetail.setLocationCode("test_code");
        validPromoDetail.setMessageCode("test_msg_code");
        validPromoDetail.setRank(2);
        validPromoDetail.setSiteName("test_site");
        Date date = new Date();
        validPromoDetail.setPromoStartDate(date);
        validPromoDetail.setPromoEndDate(new Date(date.getTime()+(1000*60*60*24)));
        mongoOperations.insert(validPromoDetail,"test_company_test_store_valid_promo_details");
        GetOfferResponse getOfferResponse = validPromoDetailDAO.getValidPromosForLocation("test_company","test_store","test_site2","test_location2");
        assertEquals(getOfferResponse.getPromoDetailList().size(),0);
        assertEquals(StatusMessage.STATUS.SUCCESS,getOfferResponse.getStatusMessage().getStatus());
    }


    @Test
    public void testgetAllValidPromosForStore(){
        validPromoDetail.setLocationName("test_location");
        validPromoDetail.setDisplayMessage("test_message");
        validPromoDetail.setLocationCode("test_code");
        validPromoDetail.setMessageCode("test_msg_code");
        validPromoDetail.setRank(2);
        validPromoDetail.setSiteName("test_site");
        Date date = new Date();
        validPromoDetail.setPromoStartDate(date);
        validPromoDetail.setPromoEndDate(new Date(date.getTime()+(1000*60*60*24)));
        mongoOperations.insert(validPromoDetail,"test_company_test_store_valid_promo_details");
        PromoOfferResponse promoMapOfferResponse = validPromoDetailDAO.getAllValidPromosForStore("test_comp","test_store","android");
        assertEquals(StatusMessage.STATUS.SUCCESS,promoMapOfferResponse.getStatusMessage().getStatus());
    }

    @Test
    public void testgetValidPromosForSite(){
        validPromoDetail.setLocationName("test_location");
        validPromoDetail.setDisplayMessage("test_message");
        validPromoDetail.setLocationCode("test_code");
        validPromoDetail.setMessageCode("test_msg_code");
        validPromoDetail.setRank(2);
        validPromoDetail.setSiteName("test_site");
        Date date = new Date();
        validPromoDetail.setPromoStartDate(date);
        validPromoDetail.setPromoEndDate(new Date(date.getTime()+(1000*60*60*24)));
        mongoOperations.insert(validPromoDetail,"test_company_test_store_valid_promo_details");
        PromoMapOfferResponse promoMapOfferResponse = validPromoDetailDAO.getValidPromosForSite("test_comp","test_store","android");
        assertEquals(StatusMessage.STATUS.SUCCESS,promoMapOfferResponse.getStatusMessage().getStatus());
    }


    @Test
    public void testgetValidPromosForStore() {
        validPromoDetail.setLocationName("test_location");
        validPromoDetail.setDisplayMessage("test_message");
        validPromoDetail.setLocationCode("test_code");
        validPromoDetail.setMessageCode("test_msg_code");
        validPromoDetail.setRank(2);
        validPromoDetail.setSiteName("test_site");
        Date date = new Date();
        validPromoDetail.setPromoStartDate(date);
        validPromoDetail.setPromoEndDate(new Date(date.getTime()+(1000*60*60*24)));
        mongoOperations.insert(validPromoDetail,"test_company_test_store_valid_promo_details");
        PromoMapOfferResponse promoMapOfferResponse = validPromoDetailDAO.getValidPromosForStore("test_comp", "test_store");
        assertNotNull(promoMapOfferResponse);
    }


}

