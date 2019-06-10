package com.bosch.pai.retail.configuration.dao;

import com.bosch.pai.retail.common.Constants;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.configmodel.HierarchyDetail;
import com.bosch.pai.retail.configuration.Exception.InvalidUrlException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class HierarchyConfigDAO {

    private static final String ANDROID_COLLECTION_END_NAME = "hierarchy_details";
    private static final String IOS_COLLECTION_END_NAME = "hierarchy_details_ios";
    private final Logger logger = LoggerFactory
            .getLogger(HierarchyConfigDAO.class);
    private final MongoOperations mongoOperations;


    @Autowired
    public HierarchyConfigDAO(MongoOperations mongoOperations){
        this.mongoOperations = mongoOperations;
    }


    public List<HierarchyDetail> getHierarchies(String companyId,String storeId,String platform){
        List<HierarchyDetail> hierarchyDetails = new ArrayList<>();
        String collectionName = getCollectionName(companyId,storeId,platform);
        try {
            hierarchyDetails = mongoOperations.findAll(HierarchyDetail.class, collectionName);
        }catch (Exception e){
            logger.debug("Error while fetching Hierarchies for {}",e.getLocalizedMessage());
        }
        return hierarchyDetails;

    }


    public StatusMessage saveHierarchies(String companyId,String storeId,List<HierarchyDetail> hierarchyDetails, String platform){
        StatusMessage statusMessage = null;
        StatusMessage.STATUS status = StatusMessage.STATUS.SUCCESS;
        String statusDescription;
        String collectionName = getCollectionName(companyId,storeId,platform);
        try{
            for(HierarchyDetail hierarchyDetail : hierarchyDetails){
                Query query = new Query();
                query.addCriteria(Criteria.where("hierarchyName").is(hierarchyDetail.getHierarchyName()));
                logger.debug("Inserting Hierarchy data for Company : {} and Store : {} ", companyId,storeId);
                HierarchyDetail mongoHierarchyDetail1 = mongoOperations.findOne(query,HierarchyDetail.class,collectionName);
                if(mongoHierarchyDetail1 !=null){
                    Update update = new Update();
                    update.set("hierarchyLevel",mongoHierarchyDetail1.getHierarchyLevel());
                    update.set("required",mongoHierarchyDetail1.getRequired());
                    mongoOperations.updateFirst(query,update,collectionName);
                }else{
                    mongoOperations.insert(hierarchyDetail,collectionName);
                }
            }
            statusDescription = "Successfully inserted data";
            statusMessage = new StatusMessage(status,statusDescription);
        }catch (Exception e){
            status = StatusMessage.STATUS.FAILURE;
            statusDescription = "Failed in saving Hierarchy data";
            statusMessage = new StatusMessage(status,statusDescription);
            logger.debug("Exception in saving Hierarchy data");
        }
        return statusMessage;
    }


    private String getCollectionName(String company, String storeId, String platform) {
        String finalCollectionName = "";
        if (platform != null) {
            switch (platform) {
                case Constants.PLATFORM_ANDROID:
                    finalCollectionName = (company + "_" + storeId.toUpperCase() + "_" + ANDROID_COLLECTION_END_NAME).toUpperCase();
                    break;
                case Constants.PLATFORM_IOS:
                    finalCollectionName = (company + "_" + storeId.toUpperCase() + "_" + IOS_COLLECTION_END_NAME).toUpperCase();
                    break;
                default:
                    finalCollectionName = (company + "_" + storeId.toUpperCase() + "_"  +ANDROID_COLLECTION_END_NAME).toUpperCase();
                    break;
            }
        } else {
            finalCollectionName = (company + "_" + storeId + "_"+ ANDROID_COLLECTION_END_NAME).toUpperCase();
        }
        return finalCollectionName;
    }


}
