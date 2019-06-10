package com.bosch.pai.retail.analytics.dao;

import com.bosch.pai.retail.analytics.exception.AnalyticsServiceException;
import com.bosch.pai.retail.analytics.responses.OfferAnalyticsResponse;
import com.bosch.pai.retail.analytics.responses.UserOfferAnalyticsResponse;
import com.bosch.pai.retail.common.Constants;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.encodermodel.EncoderException;
import com.bosch.pai.retail.encodermodel.EncoderUtil;
import com.mongodb.BasicDBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by SJN8KOR on 1/25/2017.
 */
@Repository("OfferResponseDAO")
public class OfferResponseDAO {


    private final Logger logger = LoggerFactory
            .getLogger(OfferResponseDAO.class);
    private static final String COLLECTION_NAME = "offer_response_details";
    private static final String COLLECTION_NAME_IOS = "offer_response_details_ios";


    private static final String OFFER_RESPONSE_STATUS_TEXT = "offerResponseStatus";
    private static final String ACCEPTED_TEXT = "ACCEPTED";
    private static final String USER_RESPONSE_TIME_STAMP_TEXT = "userResponseTimeStamp";
    private static final String STOREID_TEXT = "storeId";
    private static final String IS_ACCEPTED_TEXT = "isAccepted";
    private static final String SITE_NAME_TEXT = "siteName";
    private static final String LOCATION_NAME_TEXT = "locationName";
    private static final String DISPLAYED_OFFER_COUNT_TEXT = "displayedOfferCount";
    private static final String ACCEPTED_OFFER_COUNT_TEXT = "acceptedOfferCount";
    private static final String ID_TEXT = "_id";
    private static final String COMPANYID_TEXT = "companyId";
    private static final String HIERARCHY_DETAILS_TEXT = "hierarchyDetails";
    private static final String ENTRIES = "entries";
    private static final String HIERARCHY_NAME = "hierarchyName";
    private static final String HIERARCHY_LEVEL = "hierarchyLevel";
    private static final String HIERARCHY_TYPE = "hierarchyType";


    private static final String STARTTIME_TEXT = "startTime";
    private static final String ENDTIME_TEXT = "endTime";

    private static final String FIELD_QUERY_CONSTANT = "$";

    private final MongoOperations mongoOperations;

    @Autowired
    public OfferResponseDAO(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;

    }


    public List<OfferAnalyticsResponse> getOfferAnalytics(long startTime, long endTime, String companyId,
                                                          String storeId, String siteName, String locationName,
                                                          Set<String> locations, String platform) throws AnalyticsServiceException {
        String collection;
        try {
            Criteria criteria = getMatchCriteria(startTime, endTime, storeId, siteName);

            List<ConditionalOperators.Switch.CaseOperator> caseOperatorList = new ArrayList();
            if (locationName != null && !locationName.isEmpty()) {

                List<String> encodedValues = EncoderUtil.getEncodedValues(locationName);
                criteria = criteria.and(LOCATION_NAME_TEXT).in(encodedValues);
                addCondition(locationName, caseOperatorList, encodedValues);
            } else {
                Map<String, List<String>> encodedLocationMap = getEncodedLocationMap(locations);


                logger.debug("Complete map : {}", encodedLocationMap);

                for (Map.Entry<String, List<String>> entry : encodedLocationMap.entrySet()) {
                    addCondition(entry.getKey(), caseOperatorList, entry.getValue());
                }
            }


            ConditionalOperators.Switch switchCases = ConditionalOperators.switchCases(caseOperatorList);
            switchCases = switchCases.defaultTo(FIELD_QUERY_CONSTANT + LOCATION_NAME_TEXT);

            Aggregation agg = prepareOfferAnalyticsAggregation(criteria, switchCases, companyId, storeId, startTime, endTime);

           /* Aggregation agg = Aggregation.newAggregation(
                    Aggregation.match(criteria),
                    Aggregation.project().
                            andExpression(SITE_NAME_TEXT).as(SITE_NAME_TEXT).
                            andExpression(LOCATION_NAME_TEXT).as(LOCATION_NAME_TEXT).
                            and(ConditionalOperators.Cond.when(
                                    Criteria.where(OFFER_RESPONSE_STATUS_TEXT).is(ACCEPTED_TEXT)).then(1).otherwise(0)).as(IS_ACCEPTED_TEXT),
                    Aggregation.group(SITE_NAME_TEXT, LOCATION_NAME_TEXT).count().as(DISPLAYED_OFFER_COUNT_TEXT).sum(IS_ACCEPTED_TEXT).as(ACCEPTED_OFFER_COUNT_TEXT),
                    Aggregation.project(SITE_NAME_TEXT, LOCATION_NAME_TEXT, DISPLAYED_OFFER_COUNT_TEXT, ACCEPTED_OFFER_COUNT_TEXT).andExclude(ID_TEXT)
            );*/

            collection = getCollectionName(companyId, storeId, platform);
            AggregationResults<OfferAnalyticsResponse> results
                    = mongoOperations.aggregate(agg, collection, OfferAnalyticsResponse.class);
            List<OfferAnalyticsResponse> result = results.getMappedResults();


            if (result != null && !result.isEmpty()) {
                result.forEach(offerAnalyticsResponse -> {
                            offerAnalyticsResponse.setCompanyId(companyId);
                            offerAnalyticsResponse.setStoreId(storeId);
                            offerAnalyticsResponse.setStartTime(startTime);
                            offerAnalyticsResponse.setEndTime(endTime);
                        }
                );
                logger.debug("offerAnalyticsResponseList for USER {} : {}", ContextHolder.getContext().getUserId(), result);

                return result;
            } else {
                return Collections.emptyList();
            }

        } catch (Exception ex) {
            logger.error("Exception in querying db for USER {}. {}", ContextHolder.getContext().getUserId(), ex);
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_OFFER_ANALYTICS, "Failed to fetch offer analytics." + ex.getMessage()));
        }

    }

    private Aggregation prepareOfferAnalyticsAggregation(Criteria criteria, ConditionalOperators.Switch switchCases,
                                                         String company, String storeId, long startTime, long endTime) {

        final MatchOperation match = Aggregation.match(criteria);
        final ProjectionOperation projectDuration = Aggregation.project().
                andExpression(SITE_NAME_TEXT).as(SITE_NAME_TEXT).
                andExpression(LOCATION_NAME_TEXT).as(LOCATION_NAME_TEXT).
                and(ConditionalOperators.Cond.when(Criteria.where(OFFER_RESPONSE_STATUS_TEXT).is(ACCEPTED_TEXT)).then(1).otherwise(0)).as(IS_ACCEPTED_TEXT);

        final GroupOperation group = Aggregation.group(SITE_NAME_TEXT, LOCATION_NAME_TEXT).count().as(DISPLAYED_OFFER_COUNT_TEXT).sum(IS_ACCEPTED_TEXT).as(ACCEPTED_OFFER_COUNT_TEXT);

        final ProjectionOperation projectLocation = Aggregation.project().
                andExpression(SITE_NAME_TEXT).as(SITE_NAME_TEXT).
                andExpression(IS_ACCEPTED_TEXT).as(IS_ACCEPTED_TEXT).
                and(switchCases).as(LOCATION_NAME_TEXT);

        final ProjectionOperation projectCount = Aggregation.project().
                andExpression(SITE_NAME_TEXT).as(SITE_NAME_TEXT).
                andExpression(LOCATION_NAME_TEXT).as(LOCATION_NAME_TEXT).
                andExpression(DISPLAYED_OFFER_COUNT_TEXT).as(DISPLAYED_OFFER_COUNT_TEXT).
                andExpression(ACCEPTED_OFFER_COUNT_TEXT).as(ACCEPTED_OFFER_COUNT_TEXT).
                andExclude(ID_TEXT);

        return Aggregation.newAggregation(match, projectDuration, projectLocation, group, projectCount);
    }

    private Map<String, List<String>> getEncodedLocationMap(Set<String> locationList) throws EncoderException {
        Map<String, List<String>> encodedLocationMap = new HashMap<>();
        if (locationList != null && !locationList.isEmpty()) {

            for (String location : locationList) {
                encodedLocationMap.put(location, EncoderUtil.getEncodedValues(location));
            }
        }
        return encodedLocationMap;
    }

    private void addCondition(String actualLocation, List<ConditionalOperators.Switch.CaseOperator> caseOperatorList, final List<String> encodedValues) {
        ConditionalOperators.Switch.CaseOperator caseOperator = ConditionalOperators.Switch.CaseOperator.
                when(dbObject -> new BasicDBObject(new BasicDBObject("$in",
                        Arrays.<Object>asList(FIELD_QUERY_CONSTANT + LOCATION_NAME_TEXT, encodedValues)))).then(actualLocation);
        caseOperatorList.add(caseOperator);
    }


    private Criteria getMatchCriteria(long startTimeInRequest, long endTimeInRequest, String storeId, String siteName/*,String locationName*/) {

        Criteria criteria = Criteria.where(STOREID_TEXT).is(storeId);

        if (siteName != null && !siteName.isEmpty()) {
            criteria = criteria.and("siteName").is(siteName);
        }

        /*if (locationName != null && !locationName.isEmpty()) {
            // dont have site name refernece at client app
            criteria = criteria.and(LOCATION_NAME_TEXT).is(locationName);
        }*/

        return criteria.andOperator(Criteria.where(USER_RESPONSE_TIME_STAMP_TEXT).exists(true),
                Criteria.where(USER_RESPONSE_TIME_STAMP_TEXT).gte(startTimeInRequest),
                Criteria.where(USER_RESPONSE_TIME_STAMP_TEXT).lte(endTimeInRequest));

    }

    private String getCollectionName(String companyId, String storeId, String platform) {
        if (platform.equalsIgnoreCase(Constants.PLATFORM_IOS)) {
            return companyId.toUpperCase() + "_" + storeId + "_" + COLLECTION_NAME_IOS;
        } else {
            return companyId.toUpperCase() + "_" + storeId + "_" + COLLECTION_NAME;
        }
    }

    public List<UserOfferAnalyticsResponse> getOfferAnalytics(Long startTime, Long endTime, String siteName, String companyId, String storeId, Map<String, List<String>> hierarchyLevelNameMap, String platform) {
        try {
            String collection = getCollectionName(companyId, storeId, platform);
            Aggregation offerAnalyticsAggregation;
            if (hierarchyLevelNameMap == null || hierarchyLevelNameMap.isEmpty()) {
                offerAnalyticsAggregation = getStoreLevelAggregation(storeId,siteName, startTime, endTime);
            } else {
                Map<Integer, List<String>> hierarchyRequestList = new HashMap<>();
                for (String hm : hierarchyLevelNameMap.keySet()) {
                    List val = hierarchyLevelNameMap.get(hm);
                    hierarchyRequestList.put(Integer.parseInt(hm), val);
                }

                offerAnalyticsAggregation = getHierarchyLevelAggregation(storeId,siteName, startTime, endTime, hierarchyRequestList);
            }
            AggregationResults<UserOfferAnalyticsResponse> results
                    = mongoOperations.aggregate(offerAnalyticsAggregation, collection, UserOfferAnalyticsResponse.class);
            List<UserOfferAnalyticsResponse> result = results.getMappedResults();
            logger.debug("offerAnalyticsResponseList for USER {} : {}", ContextHolder.getContext().getUserId(), result);
            if (result != null && !result.isEmpty()) {
                return result;

            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.debug("Exception while fetching Offer Analytics : {}", e.getMessage());
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_OFFER_ANALYTICS, "Failed to fetch offer analytics." + e.getMessage()));
        }
    }

    private Aggregation getHierarchyLevelAggregation(String storeId,String siteName, Long startTime, Long endTime, Map<Integer, List<String>> hierarchyLevelNameMap) {
        final MatchOperation match = getTimestampMatchAggregation(storeId,siteName, startTime, endTime);

        final ProjectionOperation projectAccepted = getProjectAcceptedAggregation();

        final UnwindOperation unwindHierarchyDetails = Aggregation.unwind(HIERARCHY_DETAILS_TEXT);

        final UnwindOperation unwindHierarchyEntries = Aggregation.unwind(HIERARCHY_DETAILS_TEXT+"."+ENTRIES);

        final ProjectionOperation projectLocation = getProjectLocationAggregation();

        final MatchOperation matchHierarchyEntries = matchHierarchyEntries(hierarchyLevelNameMap);

        final GroupOperation groupHierarchies = Aggregation.group(SITE_NAME_TEXT,  ENTRIES,
                HIERARCHY_DETAILS_TEXT + "." + HIERARCHY_LEVEL, HIERARCHY_DETAILS_TEXT + "." + HIERARCHY_NAME)
                .count().as(DISPLAYED_OFFER_COUNT_TEXT).sum(IS_ACCEPTED_TEXT).as(ACCEPTED_OFFER_COUNT_TEXT);

        final GroupOperation groupEntries = groupEntryDetailsAggregation();

        final ProjectionOperation projectOfferHierarchies = projectOfferResponseAggregation();

        return Aggregation.newAggregation(match, projectAccepted,  unwindHierarchyDetails, unwindHierarchyEntries, projectLocation, matchHierarchyEntries, groupHierarchies, groupEntries, projectOfferHierarchies);
    }

    private Aggregation getStoreLevelAggregation(String storeId,String siteName, Long startTime, Long endTime) {

        final MatchOperation match = getTimestampMatchAggregation(storeId,siteName, startTime, endTime);

        final ProjectionOperation projectAccepted = getProjectAcceptedAggregation();

        final UnwindOperation unwindHierarchyDetails = Aggregation.unwind(HIERARCHY_DETAILS_TEXT);

        final ProjectionOperation projectHierarchy = Aggregation.project().
                andExpression(SITE_NAME_TEXT).as(SITE_NAME_TEXT).
                andExpression(IS_ACCEPTED_TEXT).as(IS_ACCEPTED_TEXT).
                andExpression(HIERARCHY_DETAILS_TEXT + "." + HIERARCHY_NAME).as(HIERARCHY_NAME).
                andExpression(HIERARCHY_DETAILS_TEXT + "." + HIERARCHY_LEVEL).as(HIERARCHY_LEVEL).
                andExpression(HIERARCHY_DETAILS_TEXT + "." + ENTRIES).as(ENTRIES);

        final UnwindOperation unwindEntries = Aggregation.unwind(ENTRIES);

        final GroupOperation groupHierarchy = Aggregation.group(SITE_NAME_TEXT, ENTRIES, HIERARCHY_LEVEL, HIERARCHY_NAME)
                .count().as(DISPLAYED_OFFER_COUNT_TEXT).sum(IS_ACCEPTED_TEXT).as(ACCEPTED_OFFER_COUNT_TEXT);

        final GroupOperation groupEntryDetails = groupEntryDetailsAggregation();

        final ProjectionOperation projectOfferResponse = projectOfferResponseAggregation();

        return Aggregation.newAggregation(match, projectAccepted, unwindHierarchyDetails, projectHierarchy, unwindEntries, groupHierarchy, groupEntryDetails, projectOfferResponse);
    }

    private MatchOperation getTimestampMatchAggregation(String storeId,String siteName, Long startTime, Long endTime) {
        Criteria criteria = new Criteria();
        if(siteName != null && !siteName.isEmpty()){
            criteria.and(SITE_NAME_TEXT).is(siteName);
        }
        criteria.andOperator(
                Criteria.where(STOREID_TEXT).is(storeId),
                Criteria.where(USER_RESPONSE_TIME_STAMP_TEXT).exists(true),
                Criteria.where(USER_RESPONSE_TIME_STAMP_TEXT).gte(startTime),
                Criteria.where(USER_RESPONSE_TIME_STAMP_TEXT).lte(endTime));
        return Aggregation.match(criteria);
    }

    private ProjectionOperation getProjectAcceptedAggregation() {
        return Aggregation.project().
                andExpression(SITE_NAME_TEXT).as(SITE_NAME_TEXT).
                andExpression(HIERARCHY_DETAILS_TEXT).as(HIERARCHY_DETAILS_TEXT).
                andExpression(LOCATION_NAME_TEXT).as(LOCATION_NAME_TEXT).
                and(ConditionalOperators.Cond.when(Criteria.where(OFFER_RESPONSE_STATUS_TEXT).is(ACCEPTED_TEXT)).then(1).otherwise(0)).as(IS_ACCEPTED_TEXT);
    }

    private ProjectionOperation getProjectLocationAggregation() {
        return Aggregation.project().
                andExpression(SITE_NAME_TEXT).as(SITE_NAME_TEXT).
                andExpression(IS_ACCEPTED_TEXT).as(IS_ACCEPTED_TEXT).
                andExpression(HIERARCHY_DETAILS_TEXT+"."+ENTRIES).toUpper().as(ENTRIES).
                andExpression(HIERARCHY_DETAILS_TEXT+"."+HIERARCHY_LEVEL).as(HIERARCHY_DETAILS_TEXT+"."+HIERARCHY_LEVEL).
                andExpression(HIERARCHY_DETAILS_TEXT+"."+HIERARCHY_NAME).as(HIERARCHY_DETAILS_TEXT+"."+HIERARCHY_NAME)
                ;
    }

    private GroupOperation groupEntryDetailsAggregation() {
        return Aggregation.group(SITE_NAME_TEXT, HIERARCHY_NAME).
                push(new BasicDBObject("name", "$_id.entries").append(DISPLAYED_OFFER_COUNT_TEXT, "$" + DISPLAYED_OFFER_COUNT_TEXT).
                        append(ACCEPTED_OFFER_COUNT_TEXT, "$" + ACCEPTED_OFFER_COUNT_TEXT)).as("entryDetails");
    }

    private ProjectionOperation projectOfferResponseAggregation() {
        return Aggregation.project().
                andExpression(SITE_NAME_TEXT).as(SITE_NAME_TEXT).
                andExpression(HIERARCHY_NAME).as(HIERARCHY_TYPE).
                andExpression("entryDetails").as("details").andExclude(ID_TEXT);
    }

    private MatchOperation matchHierarchyEntries(Map<Integer, List<String>> hierarchyLevelNameMap) {
        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> hierarchyData : hierarchyLevelNameMap.entrySet()) {
            hierarchyData.getValue().replaceAll(String::toUpperCase);
            Criteria andCriteria= new Criteria().andOperator(
                    new Criteria(HIERARCHY_DETAILS_TEXT + "." + HIERARCHY_LEVEL).in(hierarchyData.getKey()),
                    new Criteria(ENTRIES).in(hierarchyData.getValue()));
            criteriaList.add(andCriteria);

        }
        if (!criteriaList.isEmpty() && criteriaList.size() >0) {
            criteria.orOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        } /*else {
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILURE, " "));
        }*/

//        criteria.orOperator(new Criteria().andOperator(
//                new Criteria().and(HIERARCHY_DETAILS_TEXT + "." + HIERARCHY_LEVEL).in(hierarchyLevelNameMap.keySet()),
//                new Criteria().and(HIERARCHY_DETAILS_TEXT + "." + ENTRIES).in(hierarchyLevelNameMap.get(0))
//        ),new Criteria().andOperator(
//                new Criteria().and(HIERARCHY_DETAILS_TEXT + "." + HIERARCHY_LEVEL).in(hierarchyLevelNameMap.keySet()),
//                new Criteria().and(HIERARCHY_DETAILS_TEXT + "." + ENTRIES).in(hierarchyLevelNameMap.get(1))
//        ));
        return Aggregation.match(criteria);
    }
}
