package com.bosch.pai.retail.productoffer.dao;

import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.configmodel.SiteLocationDetails;
import com.bosch.pai.retail.db.util.DBUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by SJN8KOR on 1/25/2017.
 */
@Repository("SiteLocationDAO")
public class SiteLocationDAO{

    private final Logger logger = LoggerFactory
            .getLogger(SiteLocationDAO.class);

    private static final String COLLECTION_NAME = "LOCATION_BAY_MAP";
    private final MongoOperations mongoOperations;

    @Autowired
    public SiteLocationDAO(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public SiteLocationDetails getSiteLocationDetail(String companyId, String storeId, String siteName, String locationName) {
        try{
            String collectionName = DBUtil.getCollectionName(companyId,COLLECTION_NAME);
            if(locationName!=null) {
                Query query = new Query();
                query.addCriteria(Criteria.where("storeId").is(storeId).where("siteName").is(siteName).and("locationName").is(locationName));
                return mongoOperations.findOne(query, SiteLocationDetails.class, collectionName);
            }
            else {
                Query query = new Query();
                query.addCriteria(Criteria.where("siteName").is(siteName));
                return mongoOperations.findOne(query, SiteLocationDetails.class, collectionName);
            }
        }catch (Exception ex){
            logger.error("Exception in fetching site location details from db for USER {} : {}", ContextHolder.getContext().getUserId(),ex);
        }

        return null;

    }

    public  String getLocationNameByLocationCode(String companyId, String siteName, String bay) {
        try{
            String collectionName = DBUtil.getCollectionName(companyId,COLLECTION_NAME);
                Query query = new Query();
                query.addCriteria(Criteria.where("siteName").is(siteName).where("locationDeptCateBrands").all(bay));
                final SiteLocationDetails siteLocation =
                        mongoOperations.findOne(query, SiteLocationDetails.class, collectionName);
                if(siteLocation !=null){
                    return siteLocation.getLocationName();
                }
        }catch (Exception ex){
            logger.error("Exception in fetching site location details from db for USER {} : {} ",ContextHolder.getContext().getUserId(),ex);
        }

        return null;
    }
}
