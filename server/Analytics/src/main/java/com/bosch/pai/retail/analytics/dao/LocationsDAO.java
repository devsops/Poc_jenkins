package com.bosch.pai.retail.analytics.dao;

import com.bosch.pai.retail.common.Constants;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.configmodel.SiteLocations;
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
import java.util.Set;

/**
 * Created by sjn8kor on 3/9/2018.
 */

@Repository
public class LocationsDAO {

    private static final String IOS_COLLECTION_END_NAME = "locations_Ios";
    private static final String ANDROID_COLLECTION_END_NAME = "locations";

    private static final String SITE = "siteName";
    private static final String STORE = "storeId";
    private static final String LOCATIONS = "locations";

    private final MongoOperations mongoOperations;

    private final Logger logger = LoggerFactory.getLogger(LocationsDAO.class);

    @Autowired
    public LocationsDAO(MongoOperations mongoOperations) {
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

    public List<SiteLocations> getAllLocations(String companyId, String storeId, String siteName, String platform) {
        final String finalCollectionName = getCollectionName(companyId, platform);

        Query query;
        if (siteName == null) {
            query = Query.query(Criteria.where(STORE).is(storeId));
        } else {
            query = Query.query(Criteria.where(STORE).is(storeId).and(SITE).is(siteName));
        }

        try {
            List<SiteLocations> locationModels = mongoOperations.find(query, SiteLocations.class, finalCollectionName);


            if (locationModels != null && !locationModels.isEmpty()) {
                locationModels.forEach(siteLocation -> siteLocation.setCompanyId(companyId));
            } else {
                locationModels = Collections.emptyList();
            }
            logger.debug("result of getAllLocations operation {}",  locationModels);

            return locationModels;
        } catch (Exception e) {
            logger.error("error while getAllLocations " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

}
