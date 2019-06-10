package com.bosch.pai.retail.analytics.dao;

import com.bosch.pai.retail.analytics.model.entryexit.EntryExitDetails;
import com.bosch.pai.retail.analytics.model.entryexit.IntervalDetails;
import com.bosch.pai.retail.analytics.responses.EntryExitResponse;
import com.bosch.pai.retail.common.Constants;
import com.bosch.pai.retail.common.DEVICE_TYPE;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.aggregation.FacetOperation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository("EntryExitDAO")
public class EntryExitDAO {

    private final Logger logger = LoggerFactory
            .getLogger(EntryExitDAO.class);

    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";

    private static final String ENTRY_COUNT = "entryCount";
    private static final String EXIT_COUNT = "exitCount";

    private static final String DOL_ENTRY_COUNT = "$entryCount";
    private static final String DOL_EXIT_COUNT = "$exitCount";

    private static final String CATEGORIZED_BY_STARTTIME = "categorizedByStartTime";
    private static final String CATEGORIZED_BY_ENDTIME = "categorizedByEndTime";

    private static final String COMBINE = "combine";

    private static final String COMBINE_DOT_ENTRYCOUNT = "combine.entryCount";
    private static final String COMBINE_DOT_EXITCOUNT = "combine.exitCount";

    private static final String COMBINE_ID_YEAR = "combine._id.year";
    private static final String COMBINE_ID_MONTH = "combine._id.month";
    private static final String COMBINE_ID_DAY = "combine._id.day";
    private static final String COMBINE_ID_HOUR = "combine._id.hour";

    private static final String ID = "_id";

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final String HOUR = "hour";

    private static final String YEARS = "years";
    private static final String MONTHS = "months";
    private static final String DAYS = "days";
    private static final String HOURS = "hours";

    private static final String ID_YEAR = "_id.year";
    private static final String ID_MONTH = "_id.month";
    private static final String ID_DAY = "_id.day";
    private static final String ID_HOUR = "_id.hour";

    private static final String DOL_ID_YEAR = "$_id.year";
    private static final String DOL_ID_MONTH = "$_id.month";
    private static final String DOL_ID_DAY = "$_id.day";
    private static final String DOL_ID_HOUR = "$_id.hour";

    private static final String DOL_MONTHS = "$months";
    private static final String DOL_DAYS = "$days";
    private static final String DOL_HOURS = "$hours";

    private static final String COLLECTION_NAME = "session_details";
    private static final String COLLECTION_NAME_IOS = "session_details_ios";

    private final MongoOperations mongoOperations;

    @Autowired
    public EntryExitDAO(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }


    private String getCollectionName(String companyId, String storeId, String platform) {
        if (platform.equalsIgnoreCase(Constants.PLATFORM_IOS)) {
            return companyId.toUpperCase() + "_" + storeId.toUpperCase() + "_" + COLLECTION_NAME_IOS;
        } else {
            return companyId.toUpperCase() + "_" + storeId.toUpperCase() + "_" + COLLECTION_NAME;
        }
    }

    /*
    *  IsValid parameter from session collection is not considered in this query (As discussed).
    *  This method used for getting both entry and exit counts using single aggregation query
    * */
    public EntryExitResponse getEntryExit(final String companyId,
                                          final String storeId,
                                          final String requestInterval,
                                          final Timestamp startTimeInServerTimeZone,
                                          final Timestamp endTimeInServerTimeZone,
                                          String platform) {

        try {

            final String collection = getCollectionName(companyId, storeId, platform);

            final IntervalDetails intervalDetails = getRequestInterval(requestInterval, startTimeInServerTimeZone, endTimeInServerTimeZone);

            AggregationExpression startTimestamp = new AggregationExpression() {
                @Override
                public DBObject toDbObject(AggregationOperationContext context) {
                    return new BasicDBObject("$add", Arrays.asList(new Date(0), "$startTime"));

                }
            };

            AggregationExpression endTimestamp = new AggregationExpression() {
                @Override
                public DBObject toDbObject(AggregationOperationContext context) {
                    return new BasicDBObject("$add", Arrays.asList(new Date(0), "$endTime"));


                }
            };

            ProjectionOperation projectionOperation = Aggregation.project().and(startTimestamp).as(START_TIME).and(endTimestamp).as(END_TIME);

            // match documents which are greater than startTimeInServerTimeZone and lessthan endTimeInServerTimeZone for getting EntryCount
            final MatchOperation startMatchOperation = Aggregation.match(Criteria.where(START_TIME).gte(startTimeInServerTimeZone).lte(endTimeInServerTimeZone));

            // match documents which are greater than startTimeInServerTimeZone and lessthan endTimeInServerTimeZone for getting ExitCount
            final MatchOperation endMatchOperation = Aggregation.match(Criteria.where(END_TIME).gte(startTimeInServerTimeZone).lte(endTimeInServerTimeZone));


            final ProjectionOperation startProjectionOperation = getProjectionBasedOnRequestInterval(intervalDetails, START_TIME);
            final ProjectionOperation endProjectionOperation = getProjectionBasedOnRequestInterval(intervalDetails, END_TIME);

            final GroupOperation groupBasedOnRequestInterval = getGroupBasedOnRequestInterval(intervalDetails);

            final ProjectionOperation projectionBasedOnRequestInterval = getProjectionBasedOnRequestInterval(intervalDetails);

            // using facet to create multi-facet aggregation.
            final FacetOperation facetOperation = new FacetOperation().
                    and(startMatchOperation, startProjectionOperation, groupBasedOnRequestInterval.count().as(ENTRY_COUNT)).as(CATEGORIZED_BY_STARTTIME).
                    and(endMatchOperation, endProjectionOperation, groupBasedOnRequestInterval.count().as(EXIT_COUNT)).as(CATEGORIZED_BY_ENDTIME);

            Aggregation aggregation;

            switch (intervalDetails) {
                case YEARLY:
                    aggregation = getAggregationForYear(projectionOperation, facetOperation, projectionBasedOnRequestInterval, groupBasedOnRequestInterval);
                    break;
                case MONTHLY:
                    aggregation = getAggregationForMonth(projectionOperation, facetOperation, projectionBasedOnRequestInterval, groupBasedOnRequestInterval);
                    break;
                case DAILY:
                    aggregation = getAggregationForDay(projectionOperation, facetOperation, projectionBasedOnRequestInterval, groupBasedOnRequestInterval);
                    break;
                case HOURLY:
                    aggregation = getAggregationForHours(projectionOperation, facetOperation, projectionBasedOnRequestInterval, groupBasedOnRequestInterval);
                    break;
                default:
                    return null;
            }

            final AggregationResults<EntryExitDetails> aggregationResults =
                    mongoOperations.aggregate(aggregation, collection, EntryExitDetails.class);

            List<EntryExitDetails> mappedResults = aggregationResults.getMappedResults();
            if (mappedResults != null && !mappedResults.isEmpty()) {
                final EntryExitDetails entryExitDetails = mappedResults.get(0);

                final EntryExitResponse entryExitResponse = new EntryExitResponse();
                if (entryExitDetails != null) {
                    entryExitResponse.setIntervalDetails(intervalDetails);
                    entryExitResponse.setEntryExitDetails(entryExitDetails);
                    entryExitResponse.setStatusMessage(new StatusMessage(StatusMessage.STATUS.SUCCESS, "Entry exit details got successfully"));
                } else {
                    entryExitResponse.setIntervalDetails(null);
                    entryExitResponse.setEntryExitDetails(null);
                    entryExitResponse.setStatusMessage(new StatusMessage(StatusMessage.STATUS.FAILURE, "Failed to getContext Entry exit details"));
                }
                return entryExitResponse;
            }

            final EntryExitResponse entryExitResponse = new EntryExitResponse();
            entryExitResponse.setIntervalDetails(intervalDetails);
            entryExitResponse.setEntryExitDetails(null);
            entryExitResponse.setStatusMessage(new StatusMessage(StatusMessage.STATUS.SUCCESS, "Entry exit details got successfully"));

            return entryExitResponse;


        } catch (Exception e) {
            logger.error("exception while quering entry exit for USER {} : {} ", ContextHolder.getContext().getUserId(), e.getMessage());

            final EntryExitResponse entryExitResponse = new EntryExitResponse();
            entryExitResponse.setIntervalDetails(null);
            entryExitResponse.setEntryExitDetails(null);
            entryExitResponse.setStatusMessage(new StatusMessage(StatusMessage.STATUS.FAILURE, "Failed to getContext Entry exit details"));


            return entryExitResponse;
        }
    }

    /*
    *  returns aggregation query for Yearly request interval
    * */
    private Aggregation getAggregationForYear(ProjectionOperation projectionOperation, FacetOperation facetOperation, ProjectionOperation projectionBasedOnRequestInterval, GroupOperation groupBasedOnRequestInterval) {

        final DBObject pushYears = new BasicDBObject
                (YEAR, DOL_ID_YEAR).append
                (ENTRY_COUNT, DOL_ENTRY_COUNT).append
                (EXIT_COUNT, DOL_EXIT_COUNT);

        return Aggregation.newAggregation(
                projectionOperation,
                facetOperation,
                Aggregation.project().and(ArrayOperators.ConcatArrays.arrayOf(CATEGORIZED_BY_STARTTIME).concat(CATEGORIZED_BY_ENDTIME)).as(COMBINE),
                Aggregation.unwind(COMBINE),
                projectionBasedOnRequestInterval.
                        and(COMBINE_DOT_ENTRYCOUNT).as(ENTRY_COUNT).
                        and(COMBINE_DOT_EXITCOUNT).as(EXIT_COUNT),
                groupBasedOnRequestInterval.sum(ENTRY_COUNT).as(ENTRY_COUNT).sum(EXIT_COUNT).as(EXIT_COUNT),


                Aggregation.sort(Sort.Direction.ASC, ID_YEAR),

                Aggregation.group().push(pushYears).as(YEARS),
                Aggregation.project().andExclude(ID).andInclude(YEARS)
        );

    }


    /*
    *  returns aggregation query for Monthly request interval
    * */
    private Aggregation getAggregationForMonth(ProjectionOperation projectionOperation, FacetOperation facetOperation, ProjectionOperation projectionBasedOnRequestInterval, GroupOperation groupBasedOnRequestInterval) {


        final DBObject pushMonths = new BasicDBObject
                (MONTH, DOL_ID_MONTH).append
                (ENTRY_COUNT, DOL_ENTRY_COUNT).append
                (EXIT_COUNT, DOL_EXIT_COUNT);

        final DBObject pushYears = new BasicDBObject
                (YEAR, DOL_ID_YEAR).append
                (MONTHS, DOL_MONTHS);


        return Aggregation.newAggregation(
                projectionOperation,
                facetOperation,
                Aggregation.project().and(ArrayOperators.ConcatArrays.arrayOf(CATEGORIZED_BY_STARTTIME).concat(CATEGORIZED_BY_ENDTIME)).as(COMBINE),
                Aggregation.unwind(COMBINE),
                projectionBasedOnRequestInterval.
                        and(COMBINE_DOT_ENTRYCOUNT).as(ENTRY_COUNT).
                        and(COMBINE_DOT_EXITCOUNT).as(EXIT_COUNT),
                groupBasedOnRequestInterval.sum(ENTRY_COUNT).as(ENTRY_COUNT).sum(EXIT_COUNT).as(EXIT_COUNT),

                Aggregation.sort(Sort.Direction.ASC, ID_MONTH),

                new GroupOperation(Fields.fields(YEAR).and(YEAR)).push(pushMonths).as(MONTHS),

                Aggregation.sort(Sort.Direction.ASC, ID_YEAR),

                Aggregation.group().push(pushYears).as(YEARS),

                Aggregation.project().andExclude(ID).andInclude(YEARS)

        );

    }


    /*
    *  returns aggregation query for Daily request interval
    * */
    private Aggregation getAggregationForDay(ProjectionOperation projectionOperation, FacetOperation facetOperation, ProjectionOperation projectionBasedOnRequestInterval, GroupOperation groupBasedOnRequestInterval) {


        final DBObject pushDays = new BasicDBObject
                (DAY, DOL_ID_DAY).append
                (ENTRY_COUNT, DOL_ENTRY_COUNT).append
                (EXIT_COUNT, DOL_EXIT_COUNT);

        final DBObject pushMonths = new BasicDBObject
                (MONTH, DOL_ID_MONTH).append
                (DAYS, DOL_DAYS);

        final DBObject pushYears = new BasicDBObject
                (YEAR, DOL_ID_YEAR).append
                (MONTHS, DOL_MONTHS);

        return Aggregation.newAggregation(projectionOperation,
                facetOperation,
                Aggregation.project().and(ArrayOperators.ConcatArrays.arrayOf(CATEGORIZED_BY_STARTTIME).concat(CATEGORIZED_BY_ENDTIME)).as(COMBINE),
                Aggregation.unwind(COMBINE),
                projectionBasedOnRequestInterval.
                        and(COMBINE_DOT_ENTRYCOUNT).as(ENTRY_COUNT).
                        and(COMBINE_DOT_EXITCOUNT).as(EXIT_COUNT),
                groupBasedOnRequestInterval.sum(ENTRY_COUNT).as(ENTRY_COUNT).sum(EXIT_COUNT).as(EXIT_COUNT),

                Aggregation.sort(Sort.Direction.ASC, ID_DAY),

                Aggregation.group(Fields.fields().and(YEAR).and(MONTH)).push(pushDays).as(DAYS),
                Aggregation.sort(Sort.Direction.ASC, ID_MONTH),

                new GroupOperation(Fields.fields(YEAR).and(YEAR)).push(pushMonths).as(MONTHS),

                Aggregation.sort(Sort.Direction.ASC, ID_YEAR),

                Aggregation.group().push(pushYears).as(YEARS),

                Aggregation.project().andExclude(ID).andInclude(YEARS)

        );

    }


    /*
    *  returns aggregation query for Hourly request interval
    * */
    private Aggregation getAggregationForHours(ProjectionOperation projectionOperation, FacetOperation facetOperation, ProjectionOperation projectionBasedOnRequestInterval, GroupOperation groupBasedOnRequestInterval) {

        final DBObject pushHours = new BasicDBObject
                (HOUR, DOL_ID_HOUR).append
                (ENTRY_COUNT, DOL_ENTRY_COUNT).append
                (EXIT_COUNT, DOL_EXIT_COUNT);

        final DBObject pushDays = new BasicDBObject
                (DAY, DOL_ID_DAY).append
                (HOURS, DOL_HOURS);

        final DBObject pushMonths = new BasicDBObject
                (MONTH, DOL_ID_MONTH).append
                (DAYS, DOL_DAYS);

        final DBObject pushYears = new BasicDBObject
                (YEAR, DOL_ID_YEAR).append
                (MONTHS, DOL_MONTHS);


        return Aggregation.newAggregation(projectionOperation,
                facetOperation,
                Aggregation.project().and(ArrayOperators.ConcatArrays.arrayOf(CATEGORIZED_BY_STARTTIME).concat(CATEGORIZED_BY_ENDTIME)).as(COMBINE),
                Aggregation.unwind(COMBINE),
                projectionBasedOnRequestInterval.
                        and(COMBINE_DOT_ENTRYCOUNT).as(ENTRY_COUNT).
                        and(COMBINE_DOT_EXITCOUNT).as(EXIT_COUNT),
                groupBasedOnRequestInterval.sum(ENTRY_COUNT).as(ENTRY_COUNT).sum(EXIT_COUNT).as(EXIT_COUNT),

                Aggregation.sort(Sort.Direction.ASC, ID_HOUR),

                Aggregation.group(Fields.fields().and(YEAR).and(MONTH).and(DAY)).push(pushHours).as(HOURS),

                Aggregation.sort(Sort.Direction.ASC, ID_DAY),

                Aggregation.group(Fields.fields().and(YEAR).and(MONTH)).push(pushDays).as(DAYS),

                Aggregation.sort(Sort.Direction.ASC, ID_MONTH),

                new GroupOperation(Fields.fields(YEAR).and(YEAR)).push(pushMonths).as(MONTHS),

                Aggregation.sort(Sort.Direction.ASC, YEAR),

                Aggregation.group().push(pushYears).as(YEARS),

                Aggregation.project().andExclude(ID).andInclude(YEARS)

        );

    }

    /*
    *   grouping required information based on IntervalDetails after combining both entry and exit related documents
    * */
    private ProjectionOperation getProjectionBasedOnRequestInterval(final IntervalDetails intervalDetails) {
        ProjectionOperation projectionOperation;
        switch (intervalDetails) {
            case YEARLY:
                projectionOperation = Aggregation.project().and(COMBINE_ID_YEAR).as(YEAR);
                break;
            case MONTHLY:
                projectionOperation = Aggregation.project().and(COMBINE_ID_YEAR).as(YEAR).
                        and(COMBINE_ID_MONTH).as(MONTH);
                break;
            case DAILY:
                projectionOperation = Aggregation.project().and(COMBINE_ID_YEAR).as(YEAR).
                        and(COMBINE_ID_MONTH).as(MONTH).
                        and(COMBINE_ID_DAY).as(DAY);
                break;
            case HOURLY:
            default:
                projectionOperation = Aggregation.project().and(COMBINE_ID_YEAR).as(YEAR).
                        and(COMBINE_ID_MONTH).as(MONTH).
                        and(COMBINE_ID_DAY).as(DAY).
                        and(COMBINE_ID_HOUR).as(HOUR);

        }

        return projectionOperation;
    }


    /*
    *   Project information required based on intervalDetails for grouping at the initial level
    * */
    private ProjectionOperation getProjectionBasedOnRequestInterval(final IntervalDetails intervalDetails,
                                                                    final String fieldName) {
        ProjectionOperation projectionOperation;
        switch (intervalDetails) {
            case YEARLY:
                projectionOperation = Aggregation.project()
                        .and(DateOperators.Year.yearOf(fieldName)).as(YEAR);
                break;
            case MONTHLY:
                projectionOperation = Aggregation.project().
                        and(DateOperators.Year.yearOf(fieldName)).as(YEAR).
                        and(DateOperators.Month.monthOf(fieldName)).as(MONTH);
                break;
            case DAILY:
                projectionOperation = Aggregation.project().
                        and(DateOperators.Year.yearOf(fieldName)).as(YEAR).
                        and(DateOperators.Month.monthOf(fieldName)).as(MONTH).
                        and(DateOperators.DayOfMonth.dayOfMonth(fieldName)).as(DAY);
                break;
            case HOURLY:
            default:
                projectionOperation = Aggregation.project().
                        and(DateOperators.Year.yearOf(fieldName)).as(YEAR).
                        and(DateOperators.Month.monthOf(fieldName)).as(MONTH).
                        and(DateOperators.DayOfMonth.dayOfMonth(fieldName)).as(DAY).
                        and(DateOperators.Hour.hourOf(fieldName)).as(HOUR);
        }

        return projectionOperation;
    }

    /*
    * Group required information based on intervalDetails
    * */
    private GroupOperation getGroupBasedOnRequestInterval(final IntervalDetails intervalDetails) {
        GroupOperation groupOperation;

        switch (intervalDetails) {
            case YEARLY:
                groupOperation = Aggregation.group(Fields.fields(YEAR).and(YEAR));
                break;
            case MONTHLY:
                groupOperation = Aggregation.group(Fields.fields().and(YEAR).and(MONTH));

                break;
            case DAILY:
                groupOperation = Aggregation.group(Fields.fields().and(YEAR).and(MONTH).and(DAY));
                break;
            case HOURLY:
            default:
                groupOperation = Aggregation.group(Fields.fields().and(YEAR).and(MONTH).and(DAY).and(HOUR));
        }

        return groupOperation;
    }

    /*
    *   returns request interval details if not provided
    * */
    private IntervalDetails getRequestInterval(String requestInterval,
                                               Timestamp startTimeInServerTimeZone,
                                               Timestamp endTimeInServerTimeZone) {

        if (requestInterval != null && !requestInterval.isEmpty()) {
            try {
                return IntervalDetails.valueOf(requestInterval);
            } catch (Exception e) {
                logger.debug("invalid request interval for USER {} : {} ", ContextHolder.getContext().getUserId(), e.getMessage(), e);
            }
        }

        final LocalDate startDate = startTimeInServerTimeZone.toLocalDateTime().toLocalDate();
        final LocalDate endDate = endTimeInServerTimeZone.toLocalDateTime().toLocalDate();

        final Period age = Period.between(startDate, endDate);

        final int years = age.getYears();
        final int months = age.getMonths();
        final int days = age.getDays();


        return findRequestInterval(years, months, days);
    }

    private IntervalDetails findRequestInterval(int years, int months, int days) {

        if (years > 0) {
            return IntervalDetails.YEARLY;
        } else if (months > 0) {
            return IntervalDetails.MONTHLY;
        } else if (days > 0) {
            return IntervalDetails.DAILY;
        }

        return IntervalDetails.HOURLY;
    }

}
