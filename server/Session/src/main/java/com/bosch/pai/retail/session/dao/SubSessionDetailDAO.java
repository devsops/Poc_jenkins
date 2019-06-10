package com.bosch.pai.retail.session.dao;

import com.bosch.pai.retail.common.Constants;
import com.bosch.pai.retail.common.DEVICE_TYPE;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.configmodel.SiteLocationHierarchyDetail;
import com.bosch.pai.retail.db.model.SubSessionDetail;
import com.bosch.pai.retail.db.util.DBUtil;
import com.mongodb.WriteResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class provides API to save/retrieve/update sub session details in
 * database.
 *
 * @author Anju Jacob
 */
@Repository("SubSessionDetailDAO")
public class SubSessionDetailDAO {

    private final MongoOperations mongoOperations;

    private final Logger logger = LoggerFactory
            .getLogger(SubSessionDetailDAO.class);

    private StatusMessage.STATUS status = StatusMessage.STATUS.SUCCESS;
    private static final String SUB_SESSION_COLLECTION = "sub_session_details";
    private static final String SUB_SESSION_COLLECTION_IOS = "sub_session_details_ios";
    private static final String HIERARCHY_COLLECTION = "location_hierarchy_map";
    private static final String HIERARCHY_COLLECTION_IOS = "location_hierarchy_map_ios";


    @Autowired
    public SubSessionDetailDAO(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }


    private String getCollectionName(String company, String platform) {
        String finalCollectionName = "";
        if (platform != null) {
            switch (platform) {
                case Constants.PLATFORM_ANDROID:
                    finalCollectionName = (company + "_"  + HIERARCHY_COLLECTION).toUpperCase();
                    break;
                case Constants.PLATFORM_IOS:
                    finalCollectionName = (company + "_"  + HIERARCHY_COLLECTION_IOS).toUpperCase();
                    break;
                default:
                    finalCollectionName = (company + "_" + HIERARCHY_COLLECTION).toUpperCase();
                    break;
            }
        } else {
            finalCollectionName = (company + "_" + HIERARCHY_COLLECTION).toUpperCase();
        }
        return finalCollectionName;
    }


    /*
     * Saves sub session detail to DB
     *
     * @see
     * com.bosch.pai.bezpar.dao.SubSessionDetailDAO#saveSubSessionDetail(com.bosch.pai
     * .bezpar.test.model.SubSessionDetail)
     */
    public String saveSubSessionDetail(String companyId, String storeId, SubSessionDetail subSessionDetail, String platform) {
        logger.debug("About to save SubSessionDetail for subSessionId for USER  {} : {}",
                ContextHolder.getContext().getUserId(), subSessionDetail.getSubSessionId());
        String subSessionId = null;
        String hierarchyCollection = getCollectionName(companyId,platform);
        String collection = DBUtil.getCollectionName(companyId, storeId, SUB_SESSION_COLLECTION);
        try {
            if (Constants.PLATFORM_IOS.equalsIgnoreCase(platform)) {
                collection = DBUtil.getCollectionName(companyId, storeId, SUB_SESSION_COLLECTION_IOS);
            }
            Query hierarchyQuery = new Query();
            hierarchyQuery.addCriteria(Criteria.
                    where("siteName").is(subSessionDetail.getSiteName())
                    .and("locationName").is(subSessionDetail.getLocationName()));
            SiteLocationHierarchyDetail siteLocationHierarchyDetail = mongoOperations.findOne(hierarchyQuery, SiteLocationHierarchyDetail.class, hierarchyCollection);
            if (siteLocationHierarchyDetail != null && !siteLocationHierarchyDetail.getHierarchies().isEmpty()) {
                subSessionDetail.setHierarchyDetails(siteLocationHierarchyDetail.getHierarchies());
                logger.debug("Hierarchies found for the site : {} and location : {} ", subSessionDetail.getSiteName(), subSessionDetail.getLocationName());
            }
            mongoOperations.insert(subSessionDetail, collection);
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(subSessionDetail.getSubSessionId()));
            SubSessionDetail subSessionDetail1 = mongoOperations.findOne(query,
                    SubSessionDetail.class, DBUtil.getCollectionName(companyId, storeId, SUB_SESSION_COLLECTION));
            subSessionId = subSessionDetail.getSubSessionId();
            logger.debug("Successfully saved SubSessionDetail detail for USER {} : {} ", ContextHolder.getContext().getUserId(), subSessionDetail1);

        } catch (Exception ex) {
            String errorMessage = "Exception in saving data : SubSessionDetail ="
                    + subSessionDetail.getSubSessionId()
                    + " : startTime ="
                    + subSessionDetail.getStartTime()
                    + ": endTime ="
                    + subSessionDetail.getEndTime() + ".";
            logger.error("USER {} : {} , {}", ContextHolder.getContext().getUserId(), errorMessage, ex);
        }
        return subSessionId;
    }

    public List<String> getNonTerminatedSubSessions(String companyId, String storeId, String userId, String sessionId) {
        List<SubSessionDetail> nonTerminatedSubSessionList = null;
        final List<String> subSessionList = new ArrayList<>();
        try {

            Query query = new Query();
            query.addCriteria(Criteria.where("userId").is(userId).and("sessionId").is(sessionId).and("endTime").exists(false));
            nonTerminatedSubSessionList = mongoOperations.find(query, SubSessionDetail.class, DBUtil.getCollectionName(companyId, storeId, SUB_SESSION_COLLECTION));


            if (nonTerminatedSubSessionList != null && !nonTerminatedSubSessionList.isEmpty()) {

                nonTerminatedSubSessionList.forEach(subSessionDetail -> subSessionList.add(subSessionDetail.getSubSessionId()));
            }

        } catch (Exception ex) {
            logger.error("Exception in getNonTerminatedSubSessions : userId {} : {} ", userId, ex);

        }
        return subSessionList;
    }

    public StatusMessage endSubSession(String companyId, String storeId, String userId, String subSessionId, Timestamp endTime, Boolean isValid) {
        logger.debug("About to update subSession for userId : {} ", userId);

        StatusMessage statusMessage;
        String statusDescription;
        try {

            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(subSessionId));

            Update update = new Update();
            update.set("endTime", new Date(endTime.getTime()));
            update.set("isValid", isValid);

            WriteResult writeResult = mongoOperations.updateFirst(query, update,
                    SubSessionDetail.class, DBUtil.getCollectionName(companyId, storeId, SUB_SESSION_COLLECTION));


            statusDescription = "Successfully updated. updateResults : "
                    + writeResult + ".";
            statusMessage = new StatusMessage(status, statusDescription);

            logger.debug("USER {} : {}", ContextHolder.getContext().getUserId(), statusDescription);

        } catch (Exception ex) {
            String errorMessage = "Exception in updating subSession : subSessionId ="
                    + subSessionId + ", endTime =" + endTime
                    + "." + ex.getMessage();
            logger.error("USER {} : {} , {}", ContextHolder.getContext().getUserId(), errorMessage, ex);
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILED_TO_CREATE_OR_UPDATE_SESSION, errorMessage);
        }
        return statusMessage;
    }

    public long getSubSessionCount(String companyId, String storeId, String sessionId) {
        logger.debug("About to getContext sub session count for sessionId for USER {} : {} ", ContextHolder.getContext().getUserId(), sessionId);

        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("sessionId").is(sessionId));
            return mongoOperations.count(query, SubSessionDetail.class, DBUtil.getCollectionName(companyId, storeId, SUB_SESSION_COLLECTION));


        } catch (Exception ex) {
            String errorMessage = "Exception in getting sub session count for sessionId =" +
                    sessionId
                    + "." + ex.getMessage();
            logger.error("USER {} : {} ,{}", ContextHolder.getContext().getUserId(), errorMessage, ex);
        }
        return 0;
    }

}