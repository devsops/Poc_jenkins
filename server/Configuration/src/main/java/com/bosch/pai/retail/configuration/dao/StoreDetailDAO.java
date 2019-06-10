package com.bosch.pai.retail.configuration.dao;


import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.config.responses.StoreConfigResponse;
import com.bosch.pai.retail.configmodel.StoreConfig;
import com.bosch.pai.retail.db.model.MapLabelCategories;
import com.bosch.pai.retail.db.util.DBUtil;
import com.bosch.pai.retail.model.MapLabelDetail;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Repository
public class StoreDetailDAO {

    private final MongoOperations mongoOperations;
    private final Logger logger = LoggerFactory
            .getLogger(StoreDetailDAO.class);
    private static final String STORE_ID = "storeId";
    private static final String COLLECTION_NAME = "STORE_CONFIGURATION";
    private static final String SITE_NAME = "siteName";

    @Autowired
    public StoreDetailDAO(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;

    }

    public List getStoreDetails(String companyId, String storeId, String deptName,
                                String categoryName, String brandName) {
        logger.debug("Getting Store Details for User : {}  ", ContextHolder.getContext().getUserId());
        try {
            String collectionName = getCollectionName(companyId);

            if (categoryName == null || categoryName.isEmpty()) {
                return mongoOperations.getCollection(collectionName).distinct("Category_Code");
            } else if (deptName == null || deptName.isEmpty()) {
                DBObject ob = new BasicDBObject("Category_Code", categoryName);
                return mongoOperations.getCollection(collectionName).distinct("Department_Name", ob);
            } else if (brandName == null || brandName.isEmpty()) {
                DBObject ob = new BasicDBObject("Department_Name", deptName);
                return mongoOperations.getCollection(collectionName).distinct("Brand_Code", ob);
            }
        } catch (Exception e) {
            logger.debug("Some Error Occurred while fetching Store Info for User : {}", ContextHolder.getContext().getUserId());
        }
        return Collections.emptyList();
    }

    public StoreConfigResponse getStoreConfiguration(String companyId, String storeId, String siteName) {
        Query query = new Query();
        logger.debug("Feting Store Configuration");
        StoreConfigResponse response = new StoreConfigResponse();
        List<StoreConfig> storeConfigList;
        StatusMessage statusMessage;
        try {
            if (storeId == null) {
                storeConfigList = mongoOperations.findAll(StoreConfig.class, DBUtil.getCollectionName(companyId, COLLECTION_NAME));
                logger.debug("Received Store Configuration for Company : {}", storeConfigList);
            } else if (siteName == null) {
                query.addCriteria(Criteria.where(STORE_ID).is(storeId));
                storeConfigList = mongoOperations.find(query, StoreConfig.class, DBUtil.getCollectionName(companyId, COLLECTION_NAME));
                logger.debug("Received Store Configuration for Store : {}", storeConfigList);
            } else {
                query.addCriteria(Criteria.where(STORE_ID).is(storeId).and(SITE_NAME).is(siteName));
                storeConfigList = mongoOperations.find(query, StoreConfig.class, DBUtil.getCollectionName(companyId, COLLECTION_NAME));
                logger.debug("Received Store Configuration for Site: {}", storeConfigList);
            }
            response.setStoreConfig(storeConfigList);
            if (!storeConfigList.isEmpty()) {
                statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS,
                        "Successfully fetched Store Configurations");
            } else {
                statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS,
                        "No Store Configurations Available");
            }
            response.setStatusMessage(statusMessage);
        } catch (Exception e) {
            logger.debug("Some exception occurred : {}", e);
            response.setStoreConfig(new ArrayList());
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, "Failed to fetch Store Configuration");
            response.setStatusMessage(statusMessage);
        }
        return response;
    }

    public StatusMessage saveStoreConfiguration(String companyId, StoreConfig storeConfig) {
        StatusMessage statusMessage;
        String siteName = storeConfig.getSiteName();
        String storeId = storeConfig.getStoreId();
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where(STORE_ID).is(storeId).and(SITE_NAME).is(siteName));
            StoreConfig sc = mongoOperations.findOne(query, StoreConfig.class, DBUtil.getCollectionName(companyId, COLLECTION_NAME));
            if (sc == null) {
                mongoOperations.insert(storeConfig, DBUtil.getCollectionName(companyId, COLLECTION_NAME));
                statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS, "Successfully Inserted Store Configuration");
                logger.debug("Inserting Store Configuration");
            } else {
                Update update = new Update();
                update.set("storeDescription", storeConfig.getStoreDescription());
                update.set("snapshotThreshold", storeConfig.getSnapshotThreshold());
                mongoOperations.updateFirst(query, update, DBUtil.getCollectionName(companyId, COLLECTION_NAME));
                statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS, "Successfully Updated Store Configuration");
                logger.debug("Updating Store Configuration");
            }
            return statusMessage;
        } catch (Exception e) {
            logger.debug("Some Error Occurred while Inserting Store Configuration : {}", e);
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, "Some Error Occurred while Inserting Store Configuration");
            return statusMessage;
        }
    }

    public List<String> getCategories(String companyId,String storeId){
        List<String> mapLabelCategories = new ArrayList<>();
        String collectionName = getCollectionName(companyId,storeId);
        try{
            List<MapLabelCategories> mapvCategories = mongoOperations.findAll(MapLabelCategories.class,collectionName);
            for(MapLabelCategories mlc : mapvCategories){
                mapLabelCategories.add(mlc.getCategoryName());
            }
            return mapLabelCategories;
        }catch (Exception e){
            logger.debug("Some exception occurred while processing getCategories {}",e);
        }
        return mapLabelCategories;
    }

    private String getCollectionName(String company) {
        return (company + "_" + "store_details").toUpperCase();
    }
    private String getCollectionName(String company,String storeId) {
        return (company +"_"+ storeId+"_" + "map_label_categories").toUpperCase();
    }
}
