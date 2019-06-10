package com.bosch.pai.retail.configuration.dao;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.configmodel.BaymapDetail;
import com.bosch.pai.retail.configmodel.HierarchyDetail;
import com.bosch.pai.retail.configmodel.LocationCateDeptBrand;
import com.bosch.pai.retail.configmodel.SiteLocationDetails;
import com.bosch.pai.retail.common.Constants;
import com.bosch.pai.retail.configmodel.SiteLocationHierarchyDetail;
import com.bosch.pai.retail.configuration.Exception.InvalidUrlException;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
public class SiteLocationDAO {

    private final Logger logger = LoggerFactory
            .getLogger(SiteLocationDAO.class);

    private final MongoOperations mongoOperations;

    //private static final String IOS_COLLECTION_END_NAME = "location_bay_map_Ios";
    //private static final String ANDROID_COLLECTION_END_NAME = "location_bay_map";
    private static final String ANDROID_COLLECTION_END_NAME = "location_hierarchy_map";
    private static final String IOS_COLLECTION_END_NAME = "location_hierarchy_map_Ios";

    private static final String SITE = "siteName";
    private static final String STORE = "storeId";
    private static final String LOCATION = "locationName";
    private static final String LOCATIONCATEDEPTBRAND = "locationCateDeptBrand.";
    private static final String LOCATION_TYPE = "locationType";
    private static final String LOCATION_DEPARTMENTS = "locationDepartments";
    private static final String LOCATION_CATEGORYS = "locationCategorys";
    private static final String LOCATION_BRANDS = "locationBrands";
    private static final String LOCATION_CATE_DEPT_BRANDS = "locationCateDeptBrands";
    private static final String BAYS = "bays";

    @Autowired
    public SiteLocationDAO(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    private String getCollectionName(String company, String platform) {
        if(company ==null || company.isEmpty()){
            InvalidUrlException exception = new InvalidUrlException("Company cannot be null or Empty");
            exception.setStatusCode(404);
            throw exception;
        }
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

    public StatusMessage saveSiteLocationsDetails(String companyId,
                                                  String storeId, String siteName,
                                                  String locationName, LocationCateDeptBrand locationCateDeptBrand, String platform, boolean overrideRequired) {
        final String finalCollectionName = getCollectionName(companyId, platform);

        final List<SiteLocationDetails> siteLocationList =
                getSiteLocationDetails(companyId, storeId, siteName, null, platform);

        final Set<String> cateDeptBrandSet = locationCateDeptBrand.getLocationCateDeptBrands();

        if (siteLocationList != null && !siteLocationList.isEmpty()) {
            for (SiteLocationDetails siteLocationDetails : siteLocationList) {
                final Set<String> locationCateDeptBrands = siteLocationDetails.getLocationCateDeptBrand().getLocationCateDeptBrands();
                if (!locationName.equals(siteLocationDetails.getLocationName()) && cateDeptBrandSet.equals(locationCateDeptBrands)) {
                    if (overrideRequired) {
                        final StatusMessage statusMessage = deleteSiteLocationDetails(companyId, siteLocationDetails.getStoreId(),
                                siteLocationDetails.getSiteName(), siteLocationDetails.getLocationName(), platform);
                        if (statusMessage.getStatus().equals(StatusMessage.STATUS.SUCCESS)) {
                            return updateSiteLocationDetails(storeId, siteName, locationName, locationCateDeptBrand, finalCollectionName);
                        } else {
                            return statusMessage;
                        }
                    } else {
                        return new StatusMessage(StatusMessage.STATUS.BAYMAP_ALREADY_EXIST, "Baymap already exist");
                    }
                }
            }
            return updateSiteLocationDetails(storeId, siteName, locationName, locationCateDeptBrand, finalCollectionName);
        } else {
            return updateSiteLocationDetails(storeId, siteName, locationName, locationCateDeptBrand, finalCollectionName);
        }

    }


    public StatusMessage updateSiteLocationDetails(String storeId, String siteName, String locationName,
                                                   LocationCateDeptBrand locationDeptCateBrand, final String finalCollectionName) {

        Set<String> locationCategorys = locationDeptCateBrand.getLocationCategorys();
        if (locationCategorys == null) {
            locationCategorys = new HashSet<>();
        }

        Set<String> locationDepartments = locationDeptCateBrand.getLocationDepartments();
        if (locationDepartments == null) {
            locationDepartments = new HashSet<>();
        }

        Set<String> locationBrands = locationDeptCateBrand.getLocationBrands();
        if (locationBrands == null) {
            locationBrands = new HashSet<>();
        }


        final BasicDBObject query = new BasicDBObject();
        query.put(STORE, storeId);
        query.put(SITE, siteName);
        query.put(LOCATION, locationName);

        final BasicDBObject update = new BasicDBObject();
        update.put(STORE, storeId);
        update.put(SITE, siteName);
        update.put(LOCATION, locationName);

        update.put(LOCATION_TYPE, locationDeptCateBrand.getLocationType());
        update.put(LOCATION_DEPARTMENTS, locationDepartments);
        update.put(LOCATION_CATEGORYS, locationCategorys);
        update.put(LOCATION_BRANDS, locationBrands);

        update.put(LOCATION_CATE_DEPT_BRANDS, locationDeptCateBrand.getLocationCateDeptBrands());

        try {

            final WriteResult writeResult = mongoOperations.getCollection(finalCollectionName)
                    .update(query, update, true, false);

            logger.debug("result of updateSiteLocationDetails operation for USER {} :{}", ContextHolder.getContext().getUserId(), writeResult);

            if (writeResult.getN() < 1) {
                return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
            }
        } catch (Exception e) {
            logger.error("error while updating store location details for USER {} : {} {}", ContextHolder.getContext().getUserId(), e.getMessage(), e);
            return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
        }

        return new StatusMessage(StatusMessage.STATUS.SUCCESS, "success");
    }

    public List<SiteLocationDetails> getSiteLocationDetails(String company, String storeId,
                                                             String siteName, String locationName,
                                                             String platform) {

        final String finalCollectionName = getCollectionName(company, platform);

        try {

            MatchOperation matchOperation;
            if (siteName == null) {
                matchOperation = Aggregation.match(Criteria.where(STORE).is(storeId));
            } else if (locationName == null) {
                matchOperation = Aggregation.match(Criteria.where(STORE).is(storeId).
                        and(SITE).is(siteName));
            } else {
                matchOperation = Aggregation.match(Criteria.where(STORE).is(storeId).
                        and(SITE).is(siteName).
                        and(LOCATION).is(locationName));
            }

            ProjectionOperation projectionOperation = Aggregation.project().
                    andExpression(STORE).as(STORE)
                    .andExpression(SITE).as(SITE)
                    .andExpression(LOCATION).as(LOCATION)
                    .andExpression(LOCATION_TYPE).as(LOCATIONCATEDEPTBRAND + LOCATION_TYPE)
                    .andExpression(LOCATION_CATEGORYS).as(LOCATIONCATEDEPTBRAND + LOCATION_CATEGORYS)
                    .andExpression(LOCATION_DEPARTMENTS).as(LOCATIONCATEDEPTBRAND + LOCATION_DEPARTMENTS)
                    .andExpression(LOCATION_BRANDS).as(LOCATIONCATEDEPTBRAND + LOCATION_BRANDS)
                    .andExpression(LOCATION_CATE_DEPT_BRANDS).as(LOCATIONCATEDEPTBRAND + LOCATION_CATE_DEPT_BRANDS);

            Aggregation aggregation = newAggregation(matchOperation, projectionOperation);

            final AggregationResults<SiteLocationDetails> aggregateResult = mongoOperations.aggregate(aggregation, finalCollectionName, SiteLocationDetails.class);
            final List<SiteLocationDetails> siteLocationDetails = aggregateResult.getMappedResults();

            if (siteLocationDetails != null) {
                siteLocationDetails.forEach(siteLocation -> siteLocation.setCompanyId(company));
            }

            logger.debug("result of getSiteLocationDetails operation for USER {} : {}", ContextHolder.getContext().getUserId(), siteLocationDetails);

            return siteLocationDetails;

        } catch (Exception e) {
            logger.error("error while retrieving store locations for USER {} : {} {}", ContextHolder.getContext().getUserId(), e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    public ResponseEntity getSiteLocationDetail(String company, String storeId, String siteName, String locationName,String platform) {

        final String finalCollectionName = getCollectionName(company, platform);
        try {
            MatchOperation matchOperation;
            if (locationName != null) {
                matchOperation = Aggregation.match(Criteria.where(STORE).is(storeId).
                        and(SITE).is(siteName).
                        and(LOCATION).is(locationName));
            } else {
                matchOperation = Aggregation.match(Criteria.where(STORE).is(storeId).
                        and(SITE).is(siteName));
            }

            ProjectionOperation projectionOperation = Aggregation.project().
                    andExpression(STORE).as(STORE)
                    .andExpression(SITE).as(SITE)
                    .andExpression(LOCATION).as(LOCATION)
                    .andExpression(LOCATION_TYPE).as(LOCATIONCATEDEPTBRAND + LOCATION_TYPE)
                    .andExpression(LOCATION_CATEGORYS).as(LOCATIONCATEDEPTBRAND + LOCATION_CATEGORYS)
                    .andExpression(LOCATION_DEPARTMENTS).as(LOCATIONCATEDEPTBRAND + LOCATION_DEPARTMENTS)
                    .andExpression(LOCATION_BRANDS).as(LOCATIONCATEDEPTBRAND + LOCATION_BRANDS)
                    .andExpression(LOCATION_CATE_DEPT_BRANDS).as(LOCATIONCATEDEPTBRAND + LOCATION_CATE_DEPT_BRANDS);

            Aggregation aggregation = newAggregation(matchOperation, projectionOperation);

            final AggregationResults<SiteLocationDetails> aggregateResult = mongoOperations.aggregate(aggregation, finalCollectionName, SiteLocationDetails.class);
            final List<SiteLocationDetails> siteLocationList = aggregateResult.getMappedResults();

            if (siteLocationList != null) {
                for (SiteLocationDetails siteLocation : siteLocationList) {
                    siteLocation.setCompanyId(company);
                    LocationCateDeptBrand locationCateDeptBrand = siteLocation.getLocationCateDeptBrand();
                    if (locationCateDeptBrand != null) {
                        locationCateDeptBrand.setLocationCateDeptBrands(new HashSet<>());
                    }
                    logger.debug("result of getSiteLocationDetail operation for USER {} : {}", ContextHolder.getContext().getUserId(), siteLocation);
                }
                return new ResponseEntity<>(siteLocationList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No location Details Found", HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("error while retrieving store locations for USER {} : {} {}", ContextHolder.getContext().getUserId(), e.getMessage(), e);
            return new ResponseEntity<>("Some Error Occurred while fetching location details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public StatusMessage deleteSiteLocationDetails(String companyId, String storeId, String siteName, String locationName, String platform) {
        final String finalCollectionName = getCollectionName(companyId, platform);

        try {

            Query query;
            if (siteName == null) {
                query = Query.query(Criteria.where(STORE).is(storeId));
            } else if (locationName == null) {
                query = Query.query(Criteria.where(STORE).is(storeId).and(SITE).is(siteName));
            } else {
                query = Query.query(Criteria.where(STORE).is(storeId).and(SITE).is(siteName).and(LOCATION).is(locationName));
            }

            final WriteResult writeResult = mongoOperations.getCollection(finalCollectionName).
                    remove(query.getQueryObject());

            logger.debug("result of delete Site configutration details operation for USER {} :{}", ContextHolder.getContext().getUserId(), writeResult);

            if (writeResult.getN() < 1) {
                return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
            }
        } catch (Exception e) {
            logger.error("error while deleteSiteLocationDetails details for USER {} : {} {}", ContextHolder.getContext().getUserId(), e.getMessage(), e);
            return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
        }

        return new StatusMessage(StatusMessage.STATUS.SUCCESS, "success");
    }


    public StatusMessage saveLocationBayMap(String companyId, String storeId, String siteName, String locationName, Set<String> bays, String platform, boolean overrideRequired) {
        final String finalCollectionName = getCollectionName(companyId, platform);

        List<BaymapDetail> baymapDetailsList = getLocationBayMap(companyId, storeId, siteName, null, platform);

        if (baymapDetailsList != null && !baymapDetailsList.isEmpty()) {
            for (BaymapDetail baymapDetail : baymapDetailsList) {

                if (!locationName.equals(baymapDetail.getLocationName()) && bays.equals(baymapDetail.getBays())) {
                    if (overrideRequired) {
                        final StatusMessage statusMessage = deleteLocationBayMap(companyId, baymapDetail.getStoreId(),
                                baymapDetail.getSiteName(), baymapDetail.getLocationName(), platform);
                        if (statusMessage.getStatus().equals(StatusMessage.STATUS.SUCCESS)) {
                            return updateLocationBayMap(storeId, siteName, locationName, bays, finalCollectionName);
                        } else {
                            return statusMessage;
                        }
                    } else {
                        return new StatusMessage(StatusMessage.STATUS.BAYMAP_ALREADY_EXIST, "Baymap already exist");
                    }

                }
            }
            return updateLocationBayMap(storeId, siteName, locationName, bays, finalCollectionName);
        } else {
            return updateLocationBayMap(storeId, siteName, locationName, bays, finalCollectionName);
        }

    }


    public StatusMessage updateLocationBayMap(String storeId, String siteName, String locationName, Set<String> bayList, final String finalCollectionName) {
        final BasicDBObject query = new BasicDBObject();
        query.put(STORE, storeId);
        query.put(SITE, siteName);
        query.put(LOCATION, locationName);

        final BasicDBObject update = new BasicDBObject();
        update.put(STORE, storeId);
        update.put(SITE, siteName);
        update.put(LOCATION, locationName);
        update.put(BAYS, bayList);

        try {

            final WriteResult writeResult = mongoOperations.getCollection(finalCollectionName)
                    .update(query, update, true, false);

            logger.debug("result of updateLocationBayMap operation " + writeResult);

            if (writeResult.getN() < 1) {
                return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
            }
        } catch (Exception e) {
            logger.error("error while updating store location details " + e.getMessage(), e);
            return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
        }

        return new StatusMessage(StatusMessage.STATUS.SUCCESS, "success");
    }

    public List<BaymapDetail> getLocationBayMap(String company, String storeId, String siteName, String locationName, String platform) {

        final String finalCollectionName = getCollectionName(company, platform);

        Query query;
        if (siteName == null) {
            query = Query.query(Criteria.where(STORE).is(storeId));
        } else if (locationName == null) {
            query = Query.query(Criteria.where(STORE).is(storeId).and(SITE).is(siteName));
        } else {
            query = Query.query(Criteria.where(STORE).is(storeId).and(SITE).is(siteName).and(LOCATION).is(locationName));
        }

        try {
            List<BaymapDetail> siteLocations = mongoOperations.find(query, BaymapDetail.class, finalCollectionName);

            if (siteLocations != null && !siteLocations.isEmpty()) {
                siteLocations.forEach(siteLocation ->
                        siteLocation.setCompanyId(company)
                );
            } else {
                siteLocations = new ArrayList<>();
            }

            logger.debug("result of getLocationBayMap operation {}" , siteLocations);

            return siteLocations;
        } catch (Exception e) {
            logger.error("error while retrieving store locations " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public StatusMessage deleteLocationBayMap(String companyId, String storeId, String siteName, String locationName, String platform) {
        final String finalCollectionName = getCollectionName(companyId, platform);

        try {
            Query query;
            if (siteName == null) {
                query = Query.query(Criteria.where(STORE).is(storeId));
            } else if (locationName == null) {
                query = Query.query(Criteria.where(STORE).is(storeId).and(SITE).is(siteName));
            } else {
                query = Query.query(Criteria.where(STORE).is(storeId).and(SITE).is(siteName).and(LOCATION).is(locationName));
            }

            final WriteResult writeResult = mongoOperations.getCollection(finalCollectionName).
                    remove(query.getQueryObject());

            logger.debug("result of deleteLocationBayMap details operation {}" , writeResult);

            if (writeResult.getN() < 1) {
                return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
            }
        } catch (Exception e) {
            logger.error("error while deleteLocationBayMap details " + e.getMessage(), e);
            return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
        }

        return new StatusMessage(StatusMessage.STATUS.SUCCESS, "success");
    }

    public StatusMessage saveSiteLocationsHierarchyMappingDetails(String companyId, String storeId, String siteName, String locationName, List<HierarchyDetail> hierarchies, String platform) {
        final String finalCollectionName = getCollectionName(companyId, platform);
        if(storeId == null || storeId.isEmpty()){
            InvalidUrlException exception = new InvalidUrlException("StoreId cannot be null or empty");
            exception.setStatusCode(500);
            throw exception;
        }
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("storeId").is(storeId).and("siteName").is(siteName).and("locationName").is(locationName));
            Update updateQuery = new Update();
            updateQuery.set("hierarchies",hierarchies);

                WriteResult writeResult = mongoOperations.upsert(query,updateQuery,SiteLocationHierarchyDetail.class,finalCollectionName);

                if(writeResult.getN()==1){

                    logger.debug("error while saving site location hierarchy mapping  details {}" , writeResult);
                    return new StatusMessage(StatusMessage.STATUS.SUCCESS, "success");
                }else {

                    logger.error("Failed to save site location hierarchy mapping  details {}", writeResult);
                    return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
                }

        } catch (Exception e) {
            logger.error("error while saving site location hierarchy mapping  details " + e.getMessage(), e);
            return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
        }

    }

    public List<SiteLocationHierarchyDetail> getSiteLocationHierarchyMapping(String companyId, String storeId, String siteName, String locationName, String platform) {
        final String finalCollectionName = getCollectionName(companyId, platform);
        if(storeId == null || storeId.isEmpty()){
            InvalidUrlException exception = new InvalidUrlException("Storeid cannot be null or empty");
            exception.setStatusCode(500);
            throw exception;
        }
        try {
            Query query;
            if (siteName == null || siteName.isEmpty()) {
                query = Query.query(Criteria.where(STORE).is(storeId));
            } else if (locationName == null || locationName.isEmpty()) {
                query = Query.query(Criteria.where(STORE).is(storeId).and(SITE).is(siteName));
            } else {
                query = Query.query(Criteria.where(STORE).is(storeId).and(SITE).is(siteName).and(LOCATION).is(locationName));
            }

            List<SiteLocationHierarchyDetail> siteLocations = mongoOperations.find(query, SiteLocationHierarchyDetail.class, finalCollectionName);

            if (siteLocations != null && !siteLocations.isEmpty()) {
                siteLocations.forEach(siteLocation ->
                        siteLocation.setCompanyId(companyId)
                );
            } else {
                siteLocations = new ArrayList<>();
            }

            logger.debug("result of getSiteLocationHierarchyMapping operation {}",  siteLocations);

            return siteLocations;
        } catch (Exception e) {
            logger.error("error while retrieving store locations " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}

