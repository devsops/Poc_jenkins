package com.bosch.pai.retail.configuration.dao;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.configmodel.ConfigModel;
import com.bosch.pai.retail.common.Constants;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class SiteConfigDAO {

    private static final String ANDROID_COLLECTION_END_NAME = "site_config";
    private static final String IOS_COLLECTION_END_NAME = "site_config_ios";


    private static final String SITE = "siteName";
    private static final String STORE = "storeId";
    private static final String SITE_CONFIG_MAP = "siteConfigMap";

    private final MongoOperations mongoOperations;

    private final Logger logger = LoggerFactory
            .getLogger(SiteConfigDAO.class);

    @Autowired
    public SiteConfigDAO(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    private String getCollectionName(String company, String platform) {
        String finalCollectionName = "";
        if (platform != null) {
            switch (platform) {
                case Constants.PLATFORM_ANDROID:
                    finalCollectionName = (company + "_" + ANDROID_COLLECTION_END_NAME).toUpperCase();
                    break;
                case Constants.PLATFORM_IOS:
                    finalCollectionName = (company + "_" + IOS_COLLECTION_END_NAME).toUpperCase();
                    break;
                default:
                    finalCollectionName = (company + "_" + ANDROID_COLLECTION_END_NAME).toUpperCase();
                    break;
            }
        } else {
            finalCollectionName = (company + "_" + ANDROID_COLLECTION_END_NAME).toUpperCase();
        }
        return finalCollectionName;

    }


    public StatusMessage saveSiteConfigDetails(String companyName, String storeId, String siteName, Map<String, String> map, String platform) {
        final String collectionName = getCollectionName(companyName, platform);

        final BasicDBObject query = new BasicDBObject();
        query.put(STORE, storeId);
        query.put(SITE, siteName);

        final BasicDBObject update = new BasicDBObject();
        update.put(STORE, storeId);
        update.put(SITE, siteName);
        update.put(SITE_CONFIG_MAP, map);

        try {
            final WriteResult writeResult = mongoOperations.getCollection(collectionName)
                    .update(query, update, true, false);

            logger.debug("result of update operation for USER {} : {}", ContextHolder.getContext().getUserId(), writeResult);

            if (writeResult.getN() < 1) {
                return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
            }
        } catch (Exception e) {
            logger.error("error while updating configuration details for USER {} : {}", ContextHolder.getContext().getUserId(), e.getMessage());
            return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
        }
        return new StatusMessage(StatusMessage.STATUS.SUCCESS, "success");

    }

    public List<ConfigModel> getSiteConfigDetails(String companyName, String storeId, String siteName, String platform) {

        final String finalCollectionName = getCollectionName(companyName, platform);

        try {

            final Query query = Query.query(Criteria.where(STORE).is(storeId));
            if (siteName != null) {
                query.addCriteria(Criteria.where(SITE).is(siteName));
            }

            final List<ConfigModel> configModels = mongoOperations.find(query, ConfigModel.class, finalCollectionName);

            if (configModels != null && !configModels.isEmpty()) {
                configModels.forEach(configModel -> {
                            configModel.setCompanyId(companyName);
                        }
                );
                logger.debug("result of getSiteConfigDetails operation for USER {} : {} ", ContextHolder.getContext().getUserId(), configModels);

                return configModels;
            } else {
                return Collections.emptyList();
            }


        } catch (Exception e) {
            logger.error("error while retrieving configuration details for USER {} : {} ", ContextHolder.getContext().getUserId(), e.getMessage());
            return Collections.emptyList();
        }
    }


    public StatusMessage deleteSiteConfigDetails(String companyName, String storeId, String siteName, String platform) {
        final String finalCollectionName = getCollectionName(companyName, platform);

        try {

            final Query query = Query.query(Criteria.where(STORE).is(storeId));
            if (siteName != null) {
                query.addCriteria(Criteria.where(SITE).is(siteName));
            }

            final WriteResult writeResult = mongoOperations.getCollection(finalCollectionName).
                    remove(query.getQueryObject());

            logger.debug("result of delete Site configutration details operation for USER {} : {}", ContextHolder.getContext().getUserId(), writeResult);

            if (writeResult.getN() < 1) {
                return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
            }
        } catch (Exception e) {
            logger.error("error while deleting configuration details for USER {} : {}", ContextHolder.getContext().getUserId(), e.getMessage());
            return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
        }

        return new StatusMessage(StatusMessage.STATUS.SUCCESS, "success");
    }
}