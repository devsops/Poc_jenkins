package com.bosch.pai.retail.productoffer.dao;

import com.bosch.pai.retail.adtuning.model.offer.LocationPromoDetail;
import com.bosch.pai.retail.adtuning.model.offer.PromoDetail;
import com.bosch.pai.retail.adtuning.model.offer.PromoLocation;
import com.bosch.pai.retail.adtuning.responses.GetOfferResponse;
import com.bosch.pai.retail.adtuning.responses.PromoMapOfferResponse;
import com.bosch.pai.retail.adtuning.responses.PromoOfferResponse;
import com.bosch.pai.retail.common.Constants;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.db.util.DBUtil;
import com.mongodb.BasicDBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;


@Repository("ValidPromoDetailDAO")
public class ValidPromoDetailDAO{

    private final Logger logger = LoggerFactory
            .getLogger(ValidPromoDetailDAO.class);

    private static final String COLLECTION_NAME = "valid_promo_details";
    private static final String COLLECTION_NAME_IOS = "valid_promo_details_ios";
    private static final String DB_KEY_PROMO_START_DATE = "PROMO_START_DATE";
    private static final String DB_KEY_PROMO_END_DATE = "PROMO_END_DATE";
    private static final String DB_KEY_STORE_ID = "STORE_ID";
    private static final String DB_KEY_PROMO_CODE = "PROMO_CODE";
    private static final String DB_KEY_RANK = "RANK";
    private static final String DB_KEY_SITE_NAME = "SITE_NAME";
    private static final String DB_KEY_LOCATIONS = "LOCATIONS";
    private static final String DB_KEY_ITEM_CODE = "ITEM_CODE";
    private static final String DB_KEY_IMAGE_URL = "IMAGE_URL";
    private static final String DB_KEY_ITEM_DESC = "ITEM_DESC";
    private static final String DB_KEY_CUSTOM_DETAIL_MAP = "CUSTOM_DETAIL_MAP";
    private static final String DB_KEY_OFFER_DESCRIPTION = "OFFER_DESCRIPTION";
    private static final String DISPLAY_MESSAGE = "displayMessage";
    private static final String IMAGE_URL = "imageUrl";
    private static final String MESSAGE_CODE = "messageCode";
    private static final String DEVICE_TYPE_IOS = "device_type_ios";
    private final MongoOperations mongoOperations;
    @Autowired
    public ValidPromoDetailDAO(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public GetOfferResponse getValidPromosForLocation(String companyId, String storeId, String siteName, String locationName) {
        StatusMessage statusMessage;
        GetOfferResponse getOfferResponse;

        try {
            logger.debug("User : {} , siteName : {}, locationName : {} ", ContextHolder.getContext().getUserId(), siteName, locationName);
            final Set<PromoDetail> promoDetails = new HashSet<>();
            final Date currentDate = new Date();
            String collectionName = DBUtil.getCollectionName(companyId,COLLECTION_NAME);
            MatchOperation matchOperation;
            matchOperation = Aggregation.match(Criteria.where(DB_KEY_PROMO_START_DATE).lte(currentDate).
                        and(DB_KEY_PROMO_END_DATE).gte(currentDate).
                        and(DB_KEY_LOCATIONS).in(locationName));
            ProjectionOperation projectionOperation = Aggregation.project()
                    .andExpression(DB_KEY_OFFER_DESCRIPTION).as(DISPLAY_MESSAGE)
                    .andExpression(DB_KEY_PROMO_CODE).as(MESSAGE_CODE)
                    .andExpression(DB_KEY_RANK).as("rank")
                    .andExpression(DB_KEY_ITEM_CODE).as("itemCode")
                    .andExpression(DB_KEY_ITEM_DESC).as("itemDescription")
                    .andExpression(DB_KEY_CUSTOM_DETAIL_MAP).as("customDetailMap");
            Aggregation aggregation = newAggregation(matchOperation,projectionOperation);
            final AggregationResults<PromoDetail> aggregateResult= mongoOperations.aggregate(aggregation,collectionName,PromoDetail.class);
            final List<PromoDetail> result = aggregateResult.getMappedResults();
            promoDetails.addAll(result);
            logger.debug("getValidPromosForLocation for USER {} : {} ",ContextHolder.getContext().getUserId(), promoDetails);
            if (!promoDetails.isEmpty()) {

                statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS,
                        "Successfully fetched promos for locationName. " + locationName);
            } else {
                statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS,
                        "No valid promos found for locationName. " + locationName);
            }
            getOfferResponse = new GetOfferResponse();
            getOfferResponse.setPromoDetailList(promoDetails);
            getOfferResponse.setStatusMessage(statusMessage);
            return getOfferResponse;
        } catch (Exception e) {
            logger.debug("Exception in fetching offers for location for USER {} : {} ",ContextHolder.getContext().getUserId(), e);
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_OFFERS,
                    "Exception in fetching promos  for Location. " + e.getMessage());
            getOfferResponse = new GetOfferResponse();
            getOfferResponse.setStatusMessage(statusMessage);
            return getOfferResponse;
        }

    }


    public PromoOfferResponse getAllValidPromosForStore(String companyId, String storeId,String platform) {
        StatusMessage statusMessage;
        PromoOfferResponse promoOfferResponse;
        try {
            logger.debug("fetching Store Promos as LIST for USER : {}",ContextHolder.getContext().getUserId());
            final List<PromoDetail> promoDetails = new ArrayList<>();
            final Map<PromoLocation,List<PromoDetail>> promoOfferList =new HashMap<>();
            String collectionName = getCollection(companyId,platform);
              Aggregation aggregation = getAggregation(storeId,null);
          final AggregationResults<LocationPromoDetail> aggregateResult= mongoOperations.aggregate(aggregation,collectionName,LocationPromoDetail.class);

            final List<LocationPromoDetail> result = aggregateResult.getMappedResults();
            if (result != null && !result.isEmpty()) {
                result.forEach(locationPromoDetail -> {
                    if(locationPromoDetail!=null) {
                        PromoLocation promoLocation = new PromoLocation(locationPromoDetail.getSiteName(),locationPromoDetail.getLocationName());
                        promoOfferList.put(promoLocation, locationPromoDetail.getPromoDetail());
                    }
                });
            }else{
                Query query = new Query();
                query.addCriteria(Criteria.where(DB_KEY_PROMO_START_DATE).gte(new Date(0))
                        .and(DB_KEY_STORE_ID).is(storeId));
                List<PromoDetail> promos = mongoOperations.find(query,PromoDetail.class,collectionName);
                if(!promos.isEmpty()){
                    statusMessage= new StatusMessage(StatusMessage.STATUS.SUCCESS,
                            "Promos present but maybe expired or not activated."
                            );
                    logger.debug("getAllValidPromosForStore :{} {} ",statusMessage, promos);
                    PromoOfferResponse response = new PromoOfferResponse();
                    response.setPromoDetailList(new ArrayList<>());
                    response.setStatusMessage(statusMessage);
                    return response;
                }
                logger.debug("getAllValidPromosForStore for USER {} : {}  ",ContextHolder.getContext().getUserId(), promos);
            }
            logger.debug("getAllValidPromosForStore for USER {} : {} ",ContextHolder.getContext().getUserId(), promoDetails);
            if (!promoOfferList.isEmpty()) {
                statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS,
                        "Successfully fetched promos for Store. " );
            } else {
                statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS,
                        "No valid promos found for Store. " );
            }
            promoOfferResponse = new PromoOfferResponse();
            promoOfferResponse.setPromoDetailList(result);
            promoOfferResponse.setStatusMessage(statusMessage);
            return promoOfferResponse;
        } catch (Exception e) {
            logger.debug("Exception in fetching offers for Store for USER {} : {} ",ContextHolder.getContext().getUserId(), e);
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_OFFERS,
                    "Exception in fetching promos  for Store. " + e.getMessage());
            promoOfferResponse = new PromoOfferResponse();
            promoOfferResponse.setStatusMessage(statusMessage);
            return promoOfferResponse;
        }
    }

    public PromoMapOfferResponse getValidPromosForStore(String companyId, String storeId) {
        StatusMessage statusMessage;
        PromoMapOfferResponse promoMapOfferResponse;
        try {
            logger.debug("fetching Store Promos for USER {}",ContextHolder.getContext().getUserId());
            final List<PromoDetail> promoDetails = new ArrayList<>();
            final Map<PromoLocation,List<PromoDetail>> promoOfferList =new HashMap<>();
            String collectionName = getCollectionName(companyId,COLLECTION_NAME);

            Aggregation aggregation = getAggregation(storeId,null);
            final AggregationResults<LocationPromoDetail> aggregateResult= mongoOperations.aggregate(aggregation,collectionName,LocationPromoDetail.class);
            final List<LocationPromoDetail> result = aggregateResult.getMappedResults();

            if (result != null && !result.isEmpty()) {
                result.forEach(locationPromoDetail -> {
                    PromoLocation promoLocation = new PromoLocation(locationPromoDetail.getSiteName(),locationPromoDetail.getLocationName());
                    if(locationPromoDetail!=null) {
                        promoOfferList.put(promoLocation, locationPromoDetail.getPromoDetail());
                    }
                });
            }

            logger.debug("getValidPromosForStore for USER {} : {} ",ContextHolder.getContext().getUserId(), promoDetails);
            if (!promoOfferList.isEmpty()) {
                statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS,
                        "Successfully fetched promos for Store. " );
            } else {
                statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE,
                        "No valid promos found for Store. " );
            }
            promoMapOfferResponse = new PromoMapOfferResponse();
            promoMapOfferResponse.setPromoDetailMap(promoOfferList);
            promoMapOfferResponse.setStatusMessage(statusMessage);
            return promoMapOfferResponse;
        } catch (Exception e) {
            logger.debug("Exception in fetching offers for USER {} : {} ",ContextHolder.getContext().getUserId(), e);
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_OFFERS,
                    "Exception in fetching promos  for Store. " + e.getMessage());
            promoMapOfferResponse = new PromoMapOfferResponse();
            promoMapOfferResponse.setStatusMessage(statusMessage);
            return promoMapOfferResponse;
        }

    }


    public PromoMapOfferResponse getValidPromosForSite(String companyId, String storeId,String siteId) {
        StatusMessage statusMessage;
        PromoMapOfferResponse promoMapOfferResponse;
        try {
            logger.debug("fetching Site Promos for USER : {}",ContextHolder.getContext().getUserId());
            final List<PromoDetail> promoDetails = new ArrayList<>();
            final Map<PromoLocation,List<PromoDetail>> promoOfferList =new HashMap<>();
            String collectionName = getCollectionName(companyId,COLLECTION_NAME);

            Aggregation aggregation = getAggregation(storeId,siteId);
            final AggregationResults<LocationPromoDetail> aggregateResult= mongoOperations.aggregate(aggregation,collectionName,LocationPromoDetail.class);
            final List<LocationPromoDetail> result = aggregateResult.getMappedResults();
            if (result != null && !result.isEmpty()) {
                result.forEach(locationPromoDetail -> {
                    PromoLocation promoLocation = new PromoLocation(locationPromoDetail.getSiteName(),locationPromoDetail.getLocationName());
                    if(locationPromoDetail!=null) {
                        promoOfferList.put(promoLocation, locationPromoDetail.getPromoDetail());
                    }
                });
            }
            logger.debug("getValidPromosForSite for USER {} : {} ",ContextHolder.getContext().getUserId(), promoDetails);
            if (!promoDetails.isEmpty()) {
                statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS,
                        "Successfully fetched promos for Site. " );
            } else {
                statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS,
                        "No valid promos found for Site. " );
            }
            promoMapOfferResponse = new PromoMapOfferResponse();
            promoMapOfferResponse.setPromoDetailMap(promoOfferList);
            promoMapOfferResponse.setStatusMessage(statusMessage);
            return promoMapOfferResponse;
        } catch (Exception e) {
            logger.debug("Exception in fetching offers for USER {} : {} ",ContextHolder.getContext().getUserId(), e);
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_OFFERS,
                    "Exception in fetching promos  for Site. " + e.getMessage());
            promoMapOfferResponse = new PromoMapOfferResponse();
            promoMapOfferResponse.setStatusMessage(statusMessage);
            return promoMapOfferResponse;
        }
    }

    private String getCollectionName(String companyId,String collectionName){
        return  companyId +"_"+collectionName;
    }

    private String getCollection(String companyId,String platform){
        if(platform.equalsIgnoreCase(Constants.PLATFORM_IOS)) {
            return DBUtil.getCollectionName(companyId, COLLECTION_NAME_IOS);
        }else {
            return DBUtil.getCollectionName(companyId, COLLECTION_NAME);
        }
    }

    private Aggregation getAggregation(String storeId,String siteId){
        final Date currentDate = new Date();
        MatchOperation matchOperation;
        if(siteId !=null) {
            matchOperation = Aggregation.match(Criteria.where(DB_KEY_PROMO_START_DATE).lte(currentDate)
                    .and(DB_KEY_PROMO_END_DATE).gte(currentDate).and(DB_KEY_STORE_ID).is(storeId)
                    .and(DB_KEY_SITE_NAME).is(siteId));
        }else{
            matchOperation = Aggregation.match(Criteria.where(DB_KEY_PROMO_START_DATE).lte(currentDate)
                    .and(DB_KEY_PROMO_END_DATE).gte(currentDate).and(DB_KEY_STORE_ID).is(storeId));
        }
        UnwindOperation unwindOperation = Aggregation.unwind(DB_KEY_LOCATIONS);
        GroupOperation groupOperation = Aggregation.group(DB_KEY_LOCATIONS,DB_KEY_SITE_NAME)
                .push(new BasicDBObject
                        (DISPLAY_MESSAGE, "$"+DB_KEY_OFFER_DESCRIPTION).append
                        (IMAGE_URL, "$"+DB_KEY_IMAGE_URL).append
                        (MESSAGE_CODE, "$"+DB_KEY_PROMO_CODE).append
                        ("rank","$"+DB_KEY_RANK).append
                        ("customDetailMap","$"+DB_KEY_CUSTOM_DETAIL_MAP)).as("promoInfo");

        ProjectionOperation projectionOperation = Aggregation.project()
                .andExpression("_id.LOCATIONS").as("locationName")
                .andExpression("_id.SITE_NAME").as("siteName")
                .andExpression("promoInfo").as("promoDetail");
        return newAggregation(matchOperation,unwindOperation,groupOperation,projectionOperation);

    }

}
