/*
package com.bosch.pai.retail.configuration.dao;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.configmodel.LocationCateDeptBrand;
import com.bosch.pai.retail.configmodel.SiteLocationDetails;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SiteLocationIosDAO {

    private final Logger logger = LoggerFactory
            .getLogger(SiteLocationIosDAO.class);

    private final MongoOperations mongoOperations;

    private static final String COLLECTION_END_NAME = "location_bay_map_Ios";
    private static final String SITE = "siteName";
    private static final String STORE = "storeId";
    private static final String LOCATION = "locationName";
    private static final String LOCATION_TYPE = "locationType";
    private static final String LOCATION_DEPT = "locationDepartments";
    private static final String LOCATION_CATE = "locationCategorys";
    private static final String LOCATION_BRAND = "locationBrands";
    private static final String LOCATION_CATE_DEPT_BRANDS = "locationCateDeptBrands";

    @Autowired
    public SiteLocationIosDAO(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    private String getCollectionName(String company) {
        return (company + "_" + COLLECTION_END_NAME).toUpperCase();
    }

    public StatusMessage saveSiteLocationsDetails(String companyId, String storeId, String siteName,
                                                  String locationName, LocationCateDeptBrand locationCateDeptBrand) {
        final String finalCollectionName = getCollectionName(companyId);

        List<String> locCateDeptBrands = new ArrayList<>();
        locCateDeptBrands.addAll(locationCateDeptBrand.getLocCateDeptBrands());

        final List<SiteLocationDetails> siteLocationList =
                getSiteLocationDetails(companyId, storeId, siteName, null);

        if (siteLocationList != null && !siteLocationList.isEmpty()) {
            for (SiteLocationDetails siteLocation : siteLocationList) {

                List<String> locationCateDeptBrands = new ArrayList<>();
                Set<String> ldcbSet = siteLocation.getLocationCateDeptBrands();
                if (ldcbSet != null) {
                    locationCateDeptBrands.addAll(ldcbSet);
                }

                if (!locationCateDeptBrands.isEmpty()) {

                    for (int i = 0; i < locCateDeptBrands.size(); i++) {
                        String locCateDeptBrand = locCateDeptBrands.get(i);
                        if (locationCateDeptBrands.contains(locCateDeptBrand)) {

                            if (locationCateDeptBrands.remove(locCateDeptBrand)) {
                             return  getSavedStatusMessage(siteLocation, locCateDeptBrand, locationCateDeptBrands, storeId, finalCollectionName);

                            } else {
                                logger.error("error while updating store location details for USER {} ", ContextHolder.getContext().getUserId());
                                return new StatusMessage(StatusMessage.STATUS.FAILURE, "Error while ");
                            }

                        }
                    }
                }
            }
        }

        return updateSiteLocationDetails(storeId, siteName, locationName, locationCateDeptBrand, finalCollectionName);
    }

    private StatusMessage getSavedStatusMessage(SiteLocationDetails siteLocation, String locCateDeptBrand, List<String> locationCateDeptBrands, String storeId, String finalCollectionName) {
        {
            Set<String> locationCategorys = siteLocation.getLocationCategorys();
            Set<String> locationDepartments = siteLocation.getLocationDepartments();
            Set<String> locationBrands = siteLocation.getLocationBrands();

            LocationCateDeptBrand locaCateDeptBrand = new LocationCateDeptBrand();

            String[] array = locCateDeptBrand.split("_");
            if (array != null && array.length > 0) {
                switch (array.length) {
                    case 1:
                        final String category1 = array[0];
                        if (locationCategorys != null && !locationCateDeptBrands.isEmpty()) {
                            locationCategorys.remove(category1);
                        }
                        break;
                    case 2:
                        final String category2 = array[0];
                        final String department2 = array[1];

                        if (locationCategorys != null && !locationCategorys.isEmpty()) {
                            locationCategorys.remove(category2);
                        }

                        if (locationCategorys != null && locationDepartments != null && locationDepartments.isEmpty()) {
                            locationDepartments.remove(department2);
                        }

                        break;
                    case 3:
                        final String category3 = array[0];
                        final String department3 = array[1];
                        final String brand3 = array[2];

                        if (locationBrands != null && !locationBrands.isEmpty()) {
                            locationBrands.remove(brand3);
                        }

                        if (locationDepartments != null && locationCategorys != null && locationBrands != null && locationBrands.isEmpty()) {
                            locationCategorys.remove(category3);
                            locationDepartments.remove(department3);
                        }

                        break;
                    default:
                        break;
                }

            }

            locaCateDeptBrand.setLocationType(siteLocation.getLocationType());

            locaCateDeptBrand.setLocationDepartments(locationDepartments);
            locaCateDeptBrand.setLocationCategorys(locationCategorys);
            locaCateDeptBrand.setLocationBrands(locationBrands);

            final Set<String> ldcbs = new HashSet<>();
            ldcbs.addAll(locationCateDeptBrands);

            locaCateDeptBrand.setLocCateDeptBrands(ldcbs);


            final StatusMessage statusMessage =
                    updateSiteLocationDetails(storeId, siteLocation.getSiteName(),
                            siteLocation.getLocationName(), locaCateDeptBrand, finalCollectionName);
            if (!statusMessage.getStatus().equals(StatusMessage.STATUS.SUCCESS)) {
                return statusMessage;
            }else{
                return null;
            }
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
        update.put(LOCATION_DEPT, locationDepartments);
        update.put(LOCATION_CATE, locationCategorys);
        update.put(LOCATION_BRAND, locationBrands);

        update.put(LOCATION_CATE_DEPT_BRANDS, locationDeptCateBrand.getLocCateDeptBrands());

        try {

            final WriteResult writeResult = mongoOperations.getCollection(finalCollectionName)
                    .update(query, update, true, false);

            logger.debug("result of saveSiteLocationsDetails operation for USER {} : {}",ContextHolder.getContext().getUserId(),  writeResult);

            if (writeResult.getN() < 1) {
                return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
            }
        } catch (Exception e) {
            logger.error("error while updating store location details for USER {} : {} {}" ,ContextHolder.getContext().getUserId(), e.getMessage(), e);
            return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
        }

        return new StatusMessage(StatusMessage.STATUS.SUCCESS, "success");
    }

    public List<SiteLocationDetails> getSiteLocationDetails(String company, String storeId, String siteName, String locationName) {

        final String finalCollectionName = getCollectionName(company);

        Query query;
        if (siteName == null) {
            query = Query.query(Criteria.where(STORE).is(storeId));
        } else if (locationName == null) {
            query = Query.query(Criteria.where(STORE).is(storeId).and(SITE).is(siteName));
        } else {
            query = Query.query(Criteria.where(STORE).is(storeId).and(SITE).is(siteName).and(LOCATION).is(locationName));
        }


        try {
            final List<SiteLocationDetails> siteLocations = mongoOperations.find(query, SiteLocationDetails.class, finalCollectionName);

            if (siteLocations != null && !siteLocations.isEmpty()) {
                siteLocations.forEach(siteLocation -> {
                    siteLocation.setCompanyId(company);
                });
            }

            logger.debug("result of getSiteLocationDetails operation for USER {} : {}",ContextHolder.getContext().getUserId() , siteLocations);

            return siteLocations;
        } catch (Exception e) {
            logger.error("error while retrieving store locations for USER {} :{} {}",ContextHolder.getContext().getUserId(),  e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public StatusMessage deleteSiteLocationDetails(String companyId, String storeId, String siteName, String locationName) {
        final String finalCollectionName = getCollectionName(companyId);

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

            logger.debug("result of delete Site configutration details operation for USER {} : {}",ContextHolder.getContext().getUserId(), writeResult);

            if (writeResult.getN() < 1) {
                return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
            }
        } catch (Exception e) {
            logger.error("error while deleteSiteLocationDetails details for USER {} : {} {}" ,ContextHolder.getContext().getUserId(), e.getMessage(), e);
            return new StatusMessage(StatusMessage.STATUS.FAILURE, "failure");
        }

        return new StatusMessage(StatusMessage.STATUS.SUCCESS, "success");
    }
}

*/
