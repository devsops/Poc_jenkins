/*
package com.bosch.pai.retail.analytics.dao;

import com.bosch.pai.retail.common.Constants;
import com.bosch.pai.retail.common.DEVICE_TYPE;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.configmodel.SiteLocationDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class SiteLocationDAO {

    private final Logger logger = LoggerFactory
            .getLogger(SiteLocationDAO.class);

    private final MongoOperations mongoOperations;

    private static final String COLLECTION_END_NAME = "location_bay_map";
    private static final String COLLECTION_END_NAME_IOS = "location_bay_map_ios";
    private static final String STORE = "storeId";

    @Autowired
    public SiteLocationDAO(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    private String getCollectionName(String company,String platform) {
        if(platform.equalsIgnoreCase(Constants.PLATFORM_IOS)) {
            return (company + "_" + COLLECTION_END_NAME_IOS).toUpperCase();
        }else {
            return (company + "_" + COLLECTION_END_NAME).toUpperCase();
        }
    }

    public List<SiteLocationDetails> getSiteLocationList(String company, String storeId,String platform) {

        final String finalCollectionName = getCollectionName(company,platform);
        Query query = Query.query(Criteria.where(STORE).is(storeId));

        try {
            final List<SiteLocationDetails> siteLocations = mongoOperations.find(query, SiteLocationDetails.class, finalCollectionName);

            logger.debug("result of getSiteLocationDetails operation for USER {} : {} ", ContextHolder.getContext().getUserId(), siteLocations);

            return siteLocations;
        } catch (Exception e) {
            logger.error("error while retrieving store locations for USER {} :  " + e.getMessage(),ContextHolder.getContext().getUserId(), e);
            return Collections.emptyList();
        }
    }


}

*/
