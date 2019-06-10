package com.bosch.pai.retail.productoffer.dao;

import com.bosch.pai.retail.adtuning.model.offer.UserOfferResponse;
import com.bosch.pai.retail.adtuning.model.offer.UserPromoOfferResponse;
import com.bosch.pai.retail.common.Constants;
import com.bosch.pai.retail.common.DEVICE_TYPE;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.configmodel.SiteLocationHierarchyDetail;
import com.bosch.pai.retail.db.model.OfferResponseDetail;
import com.bosch.pai.retail.db.util.DBUtil;
import com.bosch.pai.retail.encodermodel.EncoderException;
import com.bosch.pai.retail.encodermodel.EncoderUtil;
import com.bosch.pai.retail.productoffer.Exception.InvalidProductException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("OfferResponseDetailDAO")
public class OfferResponseDetailDAO {

    private final Logger logger = LoggerFactory
            .getLogger(OfferResponseDetailDAO.class);

    private static final  String COLLECTION_NAME = "offer_response_details";
    private static final  String COLLECTION_NAME_IOS = "offer_response_details_ios";
    private static final String HIERARCHY_COLLECTION = "location_hierarchy_map";
    private static final String HIERARCHY_COLLECTION_IOS = "location_hierarchy_map_ios";

    private final MongoOperations mongoOperations;


    @Autowired
    public OfferResponseDetailDAO(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public StatusMessage addCustomerAcceptedOfferDetail(
            String companyId, String storeId, OfferResponseDetail offerResponseDetail) {
        logger.debug("About to add customer response for message for USER {} : {} ", ContextHolder.getContext().getUserId(), offerResponseDetail);

        StatusMessage statusMessage = new StatusMessage();
        String statusDescription;
        try {
            String newCollectionName = DBUtil.getCollectionName(companyId,storeId,COLLECTION_NAME);
            mongoOperations.insert(offerResponseDetail,newCollectionName);
            statusDescription = "Successfully saved offerResponse ";
            logger.debug("{} : savedOfferResponseDetail  for USER {} : {} ", statusDescription,ContextHolder.getContext().getUserId(), offerResponseDetail);

            statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS, statusDescription);

        } catch (Exception ex) {
            String errorMessage = "Exception in saving data : offerResponseDetail :"
                    + offerResponseDetail + "." + ex.getMessage();
            logger.error("USER {} : {}, {}",ContextHolder.getContext().getUserId(),errorMessage, ex);
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILED_TO_SEND_OFFER_RESPONSE,
                    errorMessage);
        }
        return statusMessage;
    }

    public StatusMessage addCompleteCustomerAcceptedOfferDetail(
            String companyId, List<UserOfferResponse> offerResponseDetail,String platform) {
        logger.debug("About to add customer response for message for USER {} : {} ",ContextHolder.getContext().getUserId(), offerResponseDetail);

        StatusMessage statusMessage = new StatusMessage();
        String statusDescription;
        try {
            for (UserOfferResponse userOfferResponse :offerResponseDetail){

                String encodedLocationName;
                try {
                    encodedLocationName = EncoderUtil.encode(userOfferResponse.getLocationName());
                } catch (EncoderException e) {
                    logger.error("Unable to encodedLocationName locationName for USER {} : {} ", ContextHolder.getContext().getUserId(), e);
                    encodedLocationName = userOfferResponse.getLocationName();
                }
                userOfferResponse.setLocationName(encodedLocationName);

                String storeId = userOfferResponse.getStoreId();
                String newCollectionName = getCollectionName(companyId,storeId,platform);
                mongoOperations.insert(userOfferResponse,newCollectionName);
            }
            statusDescription = "Successfully saved completeOfferResponse ";
            logger.debug("{} : savedOfferResponseDetail for USER {} : {} ", statusDescription,ContextHolder.getContext().getUserId(), offerResponseDetail);
            statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS, statusDescription);

        } catch (Exception ex) {
            String errorMessage = "Exception in saving data : offerResponseDetail :"
                    + offerResponseDetail + "." + ex.getMessage();
            logger.error(" USER {}:{} , {}",ContextHolder.getContext().getUserId(),errorMessage, ex);
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILED_TO_SEND_OFFER_RESPONSE,
                    errorMessage);
        }
        return statusMessage;
    }

    private String getCollectionName(String companyId,String storeId,String platform){
        if(platform.equalsIgnoreCase(Constants.PLATFORM_IOS)) {
            return DBUtil.getCollectionName(companyId, storeId, COLLECTION_NAME_IOS);
        }else {
            return DBUtil.getCollectionName(companyId, storeId, COLLECTION_NAME);
        }
    }

    private String getCollectionName(String company, String platform) {
        String finalCollectionName = "";
        if (platform != null) {
            switch (platform) {
                case Constants.PLATFORM_ANDROID:
                    finalCollectionName = (company + "_"  + HIERARCHY_COLLECTION).toUpperCase();
                    break;
                case Constants.PLATFORM_IOS:
                    finalCollectionName = (company + "_"  + HIERARCHY_COLLECTION_IOS).toUpperCase();
                    break;
                default:
                    finalCollectionName = (company + "_" + HIERARCHY_COLLECTION).toUpperCase();
                    break;
            }
        } else {
            finalCollectionName = (company + "_" + HIERARCHY_COLLECTION).toUpperCase();
        }
        return finalCollectionName;
    }


    public StatusMessage addPromoOfferResponse(String companyId, List<UserPromoOfferResponse> userPromoOfferResponses, String platform) {
        logger.debug("About to add customer Promo response for message for USER {} : {} ",ContextHolder.getContext().getUserId(), userPromoOfferResponses);
        String hierarchyCollection = getCollectionName(companyId,platform);
        StatusMessage statusMessage;
        String statusDescription;
        try {
            for (UserPromoOfferResponse userOfferResponse :userPromoOfferResponses){
                String storeId = userOfferResponse.getStoreId();
                String offerResponseCollection = getCollectionName(companyId,storeId,platform);
                Query hierarchyQuery = new Query();
                hierarchyQuery.addCriteria(Criteria.
                        where("siteName").is(userOfferResponse.getSiteName())
                        .and("locationName").is(userOfferResponse.getLocationName()));
                SiteLocationHierarchyDetail siteLocationHierarchyDetail = mongoOperations.findOne(hierarchyQuery,SiteLocationHierarchyDetail.class,hierarchyCollection);
                if(siteLocationHierarchyDetail ==null || siteLocationHierarchyDetail.getHierarchies().isEmpty()){
                    logger.debug("Site Location Hierarchy Details not found for Store : {} ",userOfferResponse.getStoreId());
                    throw new InvalidProductException("Site Location Hierarchy Details not found");
                }
                userOfferResponse.setHierarchyDetails(siteLocationHierarchyDetail.getHierarchies());
                mongoOperations.insert(userOfferResponse,offerResponseCollection);
            }
            statusDescription = "Successfully saved completeOfferResponse ";
            logger.debug("{} : savedOfferResponseDetail for USER {} : {} ", statusDescription,ContextHolder.getContext().getUserId(), userPromoOfferResponses);
            statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS, statusDescription);

        } catch (Exception ex) {
            String errorMessage = "Exception in saving data : userPromoOfferResponses :"
                    + userPromoOfferResponses + "." + ex.getMessage();
            logger.error(" USER {}:{} , {}",ContextHolder.getContext().getUserId(),errorMessage, ex);
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILED_TO_SEND_OFFER_RESPONSE,
                    errorMessage);
        }
        return statusMessage;
    }
}
