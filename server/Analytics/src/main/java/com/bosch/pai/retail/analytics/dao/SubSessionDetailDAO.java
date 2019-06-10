package com.bosch.pai.retail.analytics.dao;

import com.bosch.pai.retail.analytics.exception.AnalyticsServiceException;
import com.bosch.pai.retail.analytics.model.dwelltime.LocationDwellTime;
import com.bosch.pai.retail.analytics.model.heatmap.HeatMapDetail;
import com.bosch.pai.retail.analytics.responses.UserDwellTimeAnalyticsResponse;
import com.bosch.pai.retail.analytics.responses.UserHeatMapAnalyticsResponse;
import com.bosch.pai.retail.common.Constants;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.db.util.DBUtil;
import com.bosch.pai.retail.encodermodel.EncoderException;
import com.bosch.pai.retail.encodermodel.EncoderUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Switch.CaseOperator;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;


/*
 *This class provides APIS to getContext dwell time and heatmap analytics.
 *
 * @author Anju Jacob
 * */


@Repository("SubSessionDetailDAO")
public class SubSessionDetailDAO {


    private final Logger logger = LoggerFactory
            .getLogger(SubSessionDetailDAO.class);
    private static final String COLLECTION_NAME = "sub_session_details";
    private static final String COLLECTION_NAME_IOS = "sub_session_details_ios";
    private final MongoOperations mongoOperations;

    @Value("${dwellTime.response.time.unit}")
    private String dwellTimeUnit;
    @Value("${valid.session.duration.seconds}")
    private Integer validDuration;

    private Integer dwellTimeConversionValue = 1;

    private static final String duration = "duration";
    private static final  String AVERAGE_DURATION = "averageDuration";
    private static final String userId = "userId";
    private static final String SITE_NAME = "siteName";
    private static final  String locationConstant = "locationName";
    private static final  String userCount = "userCount";
    private static final  String fieldQueryConstant = "$";
    private static final String START_TIME_FIELD ="startTime";
    private static final String END_TIME_FIELD ="endTime";
    private static final String IS_VALID ="isValid";
    private static final String HIERARCHY_DETAILS_TEXT = "hierarchyDetails";
    private static final String ENTRIES = "entries";
    private static final String HIERARCHY_NAME = "hierarchyName";
    private static final String HIERARCHY_LEVEL = "hierarchyLevel";
    private static final String HIERARCHY_TYPE = "hierarchyType";
    private static final String USER_ID_TEXT = "userId";
    private static final String USER_COUNT = "userCount";
    private static final String HIERARCHY_DWELL_TIME_DETAILS_TEXT = "hierarchyDwellTimeDetails";
    private static final String HIERARCHY_HEAT_MAP_DETAILS = "hierarchyHeatMapDetails";

    @Autowired
    public SubSessionDetailDAO(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @PostConstruct
    private void setConfigs() {
        if ("MINUTES".equalsIgnoreCase(dwellTimeUnit)) {
            dwellTimeConversionValue = 60;
        } else if ("HOURS".equalsIgnoreCase(dwellTimeUnit)) {
            dwellTimeConversionValue = 60 * 60;
        }
    }

    /**
     * DwellTime is the average time spent by user in one location during the time range requested.
     * Each user sub session is counted as one entry during this calculation.
     * duration = (endTime -startTime)/1000
     * if(duration >30seconds)
     * AverageDuration = Avg(duration/dwellTimeConversionValue)
     *
     * @param startTime         - Time from which analytics to be fetched
     * @param endTime           - Time till which analytics to be fetched
     * @param companyId         - company ID
     * @param storeId           - store ID
     * @param requestedSite     - site for which analytics is to be fetched
     * @param requestedLocation - location for which analytics is to be fetched
     * @return
     * @throws AnalyticsServiceException
     */
    public List<LocationDwellTime> getDwellTimeDetails(long startTime, long endTime, String companyId, String storeId,
                                                       String requestedSite, String requestedLocation, Set<String> locationList,
                                                        String platform)
            throws AnalyticsServiceException {

        try {
            String collection = getCollectionName(companyId,storeId,platform);
            Criteria criteria = getValidityMatchCriteria(startTime, endTime, requestedSite);
            List<CaseOperator> caseOperatorList = new ArrayList();
            if (requestedLocation != null && !requestedLocation.isEmpty()) {
                List<String> encodedValues = EncoderUtil.getEncodedValues(requestedLocation);
                criteria = criteria.and(locationConstant).in(encodedValues);
                addCondition(requestedLocation, caseOperatorList, encodedValues);
            } else {

                Map<String, List<String>> encodedLocationMap = getEncodedLocationMap(locationList);

                for (Map.Entry<String, List<String>> entry : encodedLocationMap.entrySet()) {
                    addCondition(entry.getKey(), caseOperatorList, entry.getValue());
                }
            }

            ConditionalOperators.Switch switchCases = ConditionalOperators.switchCases(caseOperatorList);
            switchCases = switchCases.defaultTo(fieldQueryConstant + locationConstant);

            Aggregation agg = prepareDwellTimeAggregation(criteria, switchCases);
            AggregationResults<LocationDwellTime> results
                    = mongoOperations.aggregate(agg, collection, LocationDwellTime.class);
            List<LocationDwellTime> result = results.getMappedResults();

            if (result != null && !result.isEmpty()) {
                result.forEach(locationDwellTime -> {
                            locationDwellTime.setCompanyId(companyId);
                            locationDwellTime.setStoreId(storeId);
                            locationDwellTime.setStartTime(startTime);
                            locationDwellTime.setEndTime(endTime);
                        }
                );
                logger.debug("locationDwellTimeList for USER {} : {}", ContextHolder.getContext().getUserId(), result);
                return result;
            } else {
                return Collections.emptyList();
            }

        } catch (EncoderException e) {
            logger.error("Unable to decode locations for USER {} .",ContextHolder.getContext().getUserId(), e);
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_DWELL_TIME_ANALYTICS, e.getMessage()), e);
        } catch (Exception e) {
            logger.error("Error in fetching dwell time details for USER {}. ",ContextHolder.getContext().getUserId(), e);
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_DWELL_TIME_ANALYTICS, e.getMessage()), e);
        }


    }

    private Aggregation prepareDwellTimeAggregation(Criteria criteria, ConditionalOperators.Switch switchCases) {
        final ProjectionOperation projectSiteLocationUserId = Aggregation.project().
                andExpression(SITE_NAME).as(SITE_NAME).
                andExpression(locationConstant).as(locationConstant).
                andExpression(userId).as(userId);
        return Aggregation.newAggregation(
                Aggregation.match(criteria),
                projectSiteLocationUserId.
                        andExpression("($endTime-$startTime)/1000").as(duration),
                Aggregation.match(Criteria.where(duration).gte(validDuration)),
                projectSiteLocationUserId.andExpression(duration).divide(dwellTimeConversionValue).as(duration),
                Aggregation.project().
                        andExpression(SITE_NAME).as(SITE_NAME).
                        andExpression(userId).as(userId).
                        andExpression(duration).as(duration).
                        and(switchCases).as(locationConstant),
                Aggregation.group(SITE_NAME, locationConstant, userId).count().as(userCount).avg(duration).as(AVERAGE_DURATION),
                Aggregation.group(SITE_NAME, locationConstant).sum(userCount).as(userCount).avg(AVERAGE_DURATION).as(AVERAGE_DURATION),
                Aggregation.project().
                        andExpression(SITE_NAME).as(SITE_NAME).
                        andExpression(locationConstant).as(locationConstant).
                        andExpression(userCount).as(userCount).
                        andExpression(AVERAGE_DURATION).as(AVERAGE_DURATION).
                        andExclude("_id")
        );
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

    private void addCondition(String actualLocation, List<CaseOperator> caseOperatorList, final List<String> encodedValues) {
       /* AggregationExpression aggregationExpression = new AggregationExpression() {
            @Override
            public DBObject toDbObject(AggregationOperationContext context) {
                DBObject dbObject = new BasicDBObject(new BasicDBObject("$in",
                        Arrays.<Object>asList(fieldQueryConstant+locationConstant, encodedValues)));
                return dbObject;
            }
        };
        CaseOperator caseOperator= CaseOperator.
                when(aggregationExpression).then(actualLocation);*/
        CaseOperator caseOperator = CaseOperator.
                when(dbObject -> new BasicDBObject(new BasicDBObject("$in",
                        Arrays.<Object>asList(fieldQueryConstant + locationConstant, encodedValues)))).then(actualLocation);
        caseOperatorList.add(caseOperator);
    }

    private Criteria getValidityMatchCriteria(long startTimeInRequest, long endTimeInRequest, String siteName) {

        Criteria criteria = new Criteria();
        if (siteName != null && !siteName.isEmpty()) {
            criteria = criteria.where(SITE_NAME).is(siteName);
        }
        return criteria.andOperator(
                Criteria.where(IS_VALID).is(true),
                Criteria.where(START_TIME_FIELD).gte(startTimeInRequest),
                Criteria.where(END_TIME_FIELD).lte(endTimeInRequest));

    }

    /*
     * Clears all sub session data
     *
     * @return status of deletion
    */

    public StatusMessage clearSubSessions(String companyId, String storeId) {


        final String storeCollectionName = DBUtil.getCollectionName(companyId, storeId, COLLECTION_NAME);
        final long count = mongoOperations.getCollection(storeCollectionName).count();
        final WriteResult writeResult = mongoOperations.remove(new Query(), storeCollectionName);
        if (writeResult.getN() == count) {
            return new StatusMessage(StatusMessage.STATUS.SUCCESS, "Cleared SubSessions successfully");
        } else {
            return new StatusMessage(StatusMessage.STATUS.FAILURE, "Failed to clear SubSessions.");
        }
    }

    /**
     * HeatMap is the number of users present in one location during the time range requested.
     * Only subsessions having duration > 30 seconds is considered for this calculation
     * Different Subsessions by same user visiting same location is counted as multiple
     *
     * @param startTime         - Time from which analytics to be fetched
     * @param endTime           - Time till which analytics to be fetched
     * @param companyId         - company ID
     * @param storeId           - store ID
     * @param requestedSite     - site for which analytics is to be fetched
     * @param requestedLocation - location for which analytics is to be fetched
     * @return
     * @throws AnalyticsServiceException
     */
    public List<HeatMapDetail> getHeatMaps(long startTime, long endTime, String companyId, String storeId,
                                           String requestedSite, String requestedLocation, Set<String> locationList,
                                           String platform)
            throws AnalyticsServiceException {
        String collection = getCollectionName(companyId,storeId,platform);
        try {



            Criteria criteria = getValidityMatchCriteria(startTime, endTime, requestedSite);
            List<CaseOperator> caseOperatorList = new ArrayList();
            if (requestedLocation != null && !requestedLocation.isEmpty()) {

                List<String> encodedValues = EncoderUtil.getEncodedValues(requestedLocation);
                criteria = criteria.and(locationConstant).in(encodedValues);
                addCondition(requestedLocation, caseOperatorList, encodedValues);
            } else {
                Map<String, List<String>> encodedLocationMap = getEncodedLocationMap(locationList);

                for (Map.Entry<String, List<String>> entry : encodedLocationMap.entrySet()) {
                    addCondition(entry.getKey(), caseOperatorList, entry.getValue());
                }

            }

            ConditionalOperators.Switch switchCases = ConditionalOperators.switchCases(caseOperatorList);
            switchCases = switchCases.defaultTo(fieldQueryConstant + locationConstant);

            Aggregation agg = prepareHeatMapAggregation(criteria, switchCases);

            AggregationResults<HeatMapDetail> results
                    = mongoOperations.aggregate(agg, collection, HeatMapDetail.class);
            List<HeatMapDetail> result = results.getMappedResults();

            if (result != null && !result.isEmpty()) {
                result.forEach(heatMapDetail -> {
                            heatMapDetail.setCompanyName(companyId);
                            heatMapDetail.setStoreId(storeId);
                            heatMapDetail.setStartTime(startTime);
                            heatMapDetail.setEndTime(endTime);
                        }
                );
                logger.debug("HeatMapList for USER {} : {}",ContextHolder.getContext().getUserId(), result);

                return result;
            } else {
                return Collections.emptyList();
            }
        } catch (EncoderException e) {
            logger.error("Unable to decode locations for USER {}. ",ContextHolder.getContext().getUserId(), e);
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_HEATMAP, e.getMessage()), e);
        }catch (Exception e) {
            logger.error("Unable to fetch heatmap details for USER {}. ",ContextHolder.getContext().getUserId(), e);
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_HEATMAP, e.getMessage()), e);
        }
    }

    private Aggregation prepareHeatMapAggregation(Criteria criteria, ConditionalOperators.Switch switchCases) {
        final MatchOperation match = Aggregation.match(criteria);
        final ProjectionOperation projectDuration = Aggregation.project().
                andExpression(SITE_NAME).as(SITE_NAME).
                andExpression(locationConstant).as(locationConstant).
                andExpression(userId).as(userId).
                andExpression("($endTime-$startTime)/1000").as(duration);
        final ProjectionOperation projectLocation = Aggregation.project().
                andExpression(SITE_NAME).as(SITE_NAME).
                andExpression(userId).as(userId).
                andExpression(duration).as(duration).
                and(switchCases).as(locationConstant);
        final MatchOperation matchValidDuration = Aggregation.match(Criteria.where(duration).gte(validDuration));
        final GroupOperation group = Aggregation.group(SITE_NAME, locationConstant).count().as(userCount);
        final ProjectionOperation projectCount = Aggregation.project().
                andExpression(SITE_NAME).as(SITE_NAME).
                andExpression(locationConstant).as(locationConstant).
                andExpression(userCount).as(userCount).
                andExclude("_id");

        return Aggregation.newAggregation(
                match,
                projectDuration,
                projectLocation,
                matchValidDuration,
                group,
                projectCount);
    }

    private String getCollectionName(String companyId,String storeId,String platform){
        if(platform.equalsIgnoreCase(Constants.PLATFORM_IOS)) {
            return DBUtil.getCollectionName(companyId, storeId, COLLECTION_NAME_IOS);
        }else {
            return DBUtil.getCollectionName(companyId, storeId, COLLECTION_NAME);
        }
    }

    public List<UserDwellTimeAnalyticsResponse> getHierarchyDwellTime(String companyId, String storeId, String siteName, Long startTime, Long endTime, String platform, Map<String, List<String>> hierarchyLevelNameMap) {
        try {
            String collectionName = getCollectionName(companyId, storeId, platform);
            Aggregation dwellTimeAggregation;
            if(hierarchyLevelNameMap == null || hierarchyLevelNameMap.size() ==0){
             dwellTimeAggregation = getStoreDwellTimeAggregation(siteName,startTime, endTime);
            }else {
                Map<Integer, List<String>> hierarchyRequestList = new HashMap<>();
                for (String hm : hierarchyLevelNameMap.keySet()) {
                    List val = hierarchyLevelNameMap.get(hm);
                    hierarchyRequestList.put(Integer.parseInt(hm), val);
                }

                dwellTimeAggregation = getHierarchyDwellTimeAggregation(siteName,startTime,endTime,hierarchyRequestList);
            }
            AggregationResults<UserDwellTimeAnalyticsResponse> results
                    = mongoOperations.aggregate(dwellTimeAggregation, collectionName, UserDwellTimeAnalyticsResponse.class);
            List<UserDwellTimeAnalyticsResponse> result = results.getMappedResults();
            logger.debug("offerAnalyticsResponseList for USER {} : {}", ContextHolder.getContext().getUserId(), result);
            if (result != null && !result.isEmpty()) {
                return result;

            } else {
                return Collections.emptyList();
            }

        } catch (Exception e) {
            logger.debug("Exception while fetching Dwell Time : {}", e.getMessage());
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_OFFER_ANALYTICS, "Failed to fetch Dwell Time." + e.getMessage()));
        }
    }


    public List<UserHeatMapAnalyticsResponse> getHierarchyHeatMap(String companyId, String storeId,String siteName, Long startTime, Long endTime, String platform, Map<String,List<String>> hierarchyLevelNameMap) {
        try{
            String collection = getCollectionName(companyId,storeId,platform);
            Aggregation heatMapAggregation;
            if(hierarchyLevelNameMap == null || hierarchyLevelNameMap.size() ==0){
                heatMapAggregation = getStoreHierarchyHeatMap(siteName,startTime,endTime);
            }else{
                Map<Integer, List<String>> hierarchyRequestList = new HashMap<>();
                for (String hm : hierarchyLevelNameMap.keySet()) {
                    List val = hierarchyLevelNameMap.get(hm);
                    hierarchyRequestList.put(Integer.parseInt(hm), val);
                }
                heatMapAggregation = getHierarchyHeatMapAggregation(siteName,startTime,endTime,hierarchyRequestList);
            }

            AggregationResults<UserHeatMapAnalyticsResponse> results
                    = mongoOperations.aggregate(heatMapAggregation, collection, UserHeatMapAnalyticsResponse.class);
            List<UserHeatMapAnalyticsResponse> result = results.getMappedResults();
            logger.debug("offerAnalyticsResponseList for USER {} : {}", ContextHolder.getContext().getUserId(), result);
            if (result != null && !result.isEmpty()) {
                return result;

            } else {
                return Collections.emptyList();
            }

        }catch (Exception ex){
            logger.debug("Exception while fetching Dwell Time : {}", ex.getMessage());
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_OFFER_ANALYTICS, "Failed to fetch Dwell Time." + ex.getMessage()));
        }
    }



    private Aggregation getStoreHierarchyHeatMap(String siteName, Long startTime, Long endTime) {

        final Criteria matchCriteria =  getValidityMatchCriteria(startTime,endTime,siteName);

        final MatchOperation matchValidity = Aggregation.match(matchCriteria);

        final UnwindOperation unwindHierarchyDetails = Aggregation.unwind(HIERARCHY_DETAILS_TEXT);

        final UnwindOperation unwindEntries = Aggregation.unwind(HIERARCHY_DETAILS_TEXT+"."+ENTRIES);

        final ProjectionOperation projectDuration = getHeatmapDuration();

        final MatchOperation matchHeatMapDuration = Aggregation.match(new Criteria().and(duration).gte(30));

        final GroupOperation groupHeatMapCount = groupHeatMapUserCount();

        final GroupOperation groupHieararchyMap = groupHierarchyMapDetails();

        final ProjectionOperation projectStoreHeatMap = projectStoreHeatMapDetails();

        return Aggregation.newAggregation(matchValidity,unwindHierarchyDetails,unwindEntries,projectDuration,matchHeatMapDuration,groupHeatMapCount,groupHieararchyMap,projectStoreHeatMap);

    }

    private Aggregation getHierarchyHeatMapAggregation(String siteName,Long startTime, Long endTime, Map<Integer, List<String>> hierarchyRequestList) {
        final Criteria matchCriteria =getValidityMatchCriteria(startTime,endTime,siteName);

        final MatchOperation matchValidity =Aggregation.match(matchCriteria);

        final UnwindOperation unwindHierarchyDetails = Aggregation.unwind(HIERARCHY_DETAILS_TEXT);

        final UnwindOperation unwindEntries = Aggregation.unwind(HIERARCHY_DETAILS_TEXT+"."+ENTRIES);

        final ProjectionOperation projectDuration = getHeatmapDuration();

        final MatchOperation matchHierarchyHeatMap = getHierarchyHeatMapDetails(hierarchyRequestList);

        final MatchOperation matchHeatMapDuration = Aggregation.match(new Criteria().and(duration).gte(30));

        final GroupOperation groupHeatMapCount = groupHeatMapUserCount();

        final GroupOperation groupHieararchyMap = groupHierarchyMapDetails();

        final ProjectionOperation projectStoreHeatMap = projectStoreHeatMapDetails();

        return Aggregation.newAggregation(matchValidity,unwindHierarchyDetails,unwindEntries,projectDuration,
                matchHierarchyHeatMap,matchHeatMapDuration,groupHeatMapCount,groupHieararchyMap,projectStoreHeatMap);

    }




    private Aggregation getHierarchyDwellTimeAggregation(String siteName, Long startTime, Long endTime, Map<Integer,List<String>> hierarchyRequestList) {
        final Criteria matchCriteria =  getValidityMatchCriteria(startTime,endTime,siteName);

        final MatchOperation matchValidity = Aggregation.match(matchCriteria);

        final UnwindOperation unwindHierarchy = Aggregation.unwind(HIERARCHY_DETAILS_TEXT);

        final UnwindOperation unwindEntries = Aggregation.unwind(HIERARCHY_DETAILS_TEXT+"."+ENTRIES);

        final ProjectionOperation projectDwellDuration = getDwellDuration();

        final MatchOperation matchRequestHierachyDwellTime = getMatchForRequestHierarchy(hierarchyRequestList);

        final MatchOperation matchDwellTimeCondition = Aggregation.match(new Criteria().and(duration).gte(30));

        final ProjectionOperation projectDwellTimeDuration = getDwellTimeDuration();

        final GroupOperation groupDwellTimeEntries = groupDwellTimeEntriesAggregation();

        final GroupOperation groupDwellTimeCount = groupDwellTimeCounts();

        final GroupOperation groupHierarchyDwellTime = groupHierarchyDwellTimeDetails();

        final ProjectionOperation projectDwellTime = projectDwellTimeResponse();

        return Aggregation.newAggregation(matchValidity,unwindHierarchy,unwindEntries,projectDwellDuration,matchRequestHierachyDwellTime,matchDwellTimeCondition,projectDwellTimeDuration,
                groupDwellTimeEntries,groupDwellTimeCount,groupHierarchyDwellTime,projectDwellTime);
    }

    private Aggregation getStoreDwellTimeAggregation(String siteName,Long startTime, Long endTime) {

        final Criteria matchCriteria =  getValidityMatchCriteria(startTime,endTime,siteName);

        final MatchOperation matchValidity = Aggregation.match(matchCriteria);

        final UnwindOperation unwindHierarchy = Aggregation.unwind(HIERARCHY_DETAILS_TEXT);

        final UnwindOperation unwindEntries = Aggregation.unwind(HIERARCHY_DETAILS_TEXT+"."+ENTRIES);

        final ProjectionOperation projectDwellDuration = getDwellDuration();

        final MatchOperation matchDwellTimeCondition = Aggregation.match(new Criteria().and(duration).gte(30));

        final ProjectionOperation projectDwellTimeDuration = getDwellTimeDuration();

        final GroupOperation groupDwellTimeEntries = groupDwellTimeEntriesAggregation();

        final GroupOperation groupDwellTimeCount = groupDwellTimeCounts();

        final GroupOperation groupHierarchyDwellTime = groupHierarchyDwellTimeDetails();

        final ProjectionOperation projectDwellTime = projectDwellTimeResponse();

        return Aggregation.newAggregation(matchValidity,unwindHierarchy,unwindEntries,projectDwellDuration,matchDwellTimeCondition,projectDwellTimeDuration,
                groupDwellTimeEntries,groupDwellTimeCount,groupHierarchyDwellTime,projectDwellTime);

    }

    /*private MatchOperation getAggregationMatchCriteria(String siteName, Long startTime, Long endTime) {
        Criteria criteria = new Criteria();


        return Aggregation.match(new Criteria().andOperator(
                Criteria.where(IS_VALID).is(true),
                Criteria.where(START_TIME_FIELD).gte(startTime),
                Criteria.where(END_TIME_FIELD).lte(endTime)));
    }*/

    private ProjectionOperation getDwellDuration() {
        return Aggregation.project().andExpression(SITE_NAME).as(SITE_NAME).
                andExpression(USER_ID_TEXT).as(USER_ID_TEXT).
                andExpression("($endTime-$startTime)/1000").as(duration).
                andExpression(HIERARCHY_DETAILS_TEXT+"."+ENTRIES).toUpper().as(ENTRIES).
                andExpression(HIERARCHY_DETAILS_TEXT+"."+HIERARCHY_LEVEL).as(HIERARCHY_LEVEL).
                andExpression(HIERARCHY_DETAILS_TEXT+"."+HIERARCHY_NAME).as(HIERARCHY_NAME);

    }

    private ProjectionOperation getDwellTimeDuration() {
        return Aggregation.project().andExpression(SITE_NAME).as(SITE_NAME).
                andExpression(USER_ID_TEXT).as(USER_ID_TEXT).
                andExpression(duration).divide(60).as(duration).
                andExpression(ENTRIES).as(ENTRIES).
                andExpression(HIERARCHY_LEVEL).as(HIERARCHY_LEVEL).
                andExpression(HIERARCHY_NAME).as(HIERARCHY_NAME);
    }

    private GroupOperation groupDwellTimeEntriesAggregation() {
        return Aggregation.group(SITE_NAME,  ENTRIES,
                HIERARCHY_LEVEL,  HIERARCHY_NAME)
                .count().as(USER_COUNT).avg(duration).as(AVERAGE_DURATION);
    }

    private GroupOperation groupDwellTimeCounts() {
        return Aggregation.group(SITE_NAME,HIERARCHY_LEVEL,HIERARCHY_NAME,ENTRIES).sum(USER_COUNT).as(USER_COUNT).avg(AVERAGE_DURATION).as(AVERAGE_DURATION);
    }

    private GroupOperation groupHierarchyDwellTimeDetails() {
        return Aggregation.group(SITE_NAME,HIERARCHY_LEVEL,HIERARCHY_NAME).
                push(new BasicDBObject(ENTRIES, "$_id.entries").append(USER_COUNT, "$" + USER_COUNT).
                        append(AVERAGE_DURATION, "$" + AVERAGE_DURATION)).as(HIERARCHY_DWELL_TIME_DETAILS_TEXT);
    }

    private ProjectionOperation projectDwellTimeResponse() {
        return Aggregation.project().
                andExpression(SITE_NAME).as(SITE_NAME).
                andExpression(HIERARCHY_NAME).as(HIERARCHY_TYPE).
                andExpression(HIERARCHY_DWELL_TIME_DETAILS_TEXT).as(HIERARCHY_DWELL_TIME_DETAILS_TEXT).andExclude("_id");
    }

    private MatchOperation getMatchForRequestHierarchy(Map<Integer, List<String>> hierarchyRequestList) {

        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> hierarchyData : hierarchyRequestList.entrySet()) {
            hierarchyData.getValue().replaceAll(String::toUpperCase);
            Criteria andCriteria= new Criteria().andOperator(
                    new Criteria(HIERARCHY_LEVEL).in(hierarchyData.getKey()),
                    new Criteria(ENTRIES).in(hierarchyData.getValue()));
            criteriaList.add(andCriteria);

        }
        if (!criteriaList.isEmpty() && criteriaList.size() >0) {
            criteria.orOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        }
        return Aggregation.match(criteria);
    }

    private ProjectionOperation getHeatmapDuration() {
        return Aggregation.project().andExpression(SITE_NAME).as(SITE_NAME).
                andExpression(USER_ID_TEXT).as(USER_ID_TEXT).
                andExpression("($endTime-$startTime)/1000").as(duration).
                andExpression(HIERARCHY_DETAILS_TEXT+"."+ENTRIES).toUpper().as(ENTRIES).
                andExpression(HIERARCHY_DETAILS_TEXT+"."+HIERARCHY_LEVEL).as(HIERARCHY_LEVEL).
                andExpression(HIERARCHY_DETAILS_TEXT+"."+HIERARCHY_NAME).as(HIERARCHY_NAME);
    }

    private GroupOperation groupHeatMapUserCount() {
        return Aggregation.group(SITE_NAME,HIERARCHY_LEVEL,HIERARCHY_NAME,ENTRIES).count().as(USER_COUNT);
    }

    private GroupOperation groupHierarchyMapDetails() {
        return Aggregation.group(SITE_NAME,HIERARCHY_LEVEL,HIERARCHY_NAME).
                push(new BasicDBObject(ENTRIES, "$_id.entries").append(USER_COUNT, "$" + USER_COUNT)).as(HIERARCHY_HEAT_MAP_DETAILS);
    }

    private ProjectionOperation projectStoreHeatMapDetails() {
        return Aggregation.project().
                andExpression(SITE_NAME).as(SITE_NAME).
                andExpression(HIERARCHY_NAME).as(HIERARCHY_TYPE).
                andExpression(HIERARCHY_HEAT_MAP_DETAILS).as(HIERARCHY_HEAT_MAP_DETAILS).andExclude("_id");
    }
    private MatchOperation getHierarchyHeatMapDetails(Map<Integer, List<String>> hierarchyRequestList) {
        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> hierarchyData : hierarchyRequestList.entrySet()) {
            hierarchyData.getValue().replaceAll(String::toUpperCase);
            Criteria andCriteria= new Criteria().andOperator(
                    new Criteria(HIERARCHY_LEVEL).in(hierarchyData.getKey()),
                    new Criteria(ENTRIES).in(hierarchyData.getValue()));
            criteriaList.add(andCriteria);

        }
        if (!criteriaList.isEmpty() && criteriaList.size() >0) {
            criteria.orOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        }
        return Aggregation.match(criteria);

    }
}

