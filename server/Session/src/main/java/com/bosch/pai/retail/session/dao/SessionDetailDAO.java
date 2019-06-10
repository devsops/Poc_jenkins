package com.bosch.pai.retail.session.dao;

import com.bosch.pai.retail.common.Constants;
import com.bosch.pai.retail.common.DEVICE_TYPE;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.db.model.SessionDetail;
import com.bosch.pai.retail.db.model.SubSessionDetail;
import com.bosch.pai.retail.db.util.DBUtil;
import com.bosch.pai.retail.encodermodel.EncoderException;
import com.bosch.pai.retail.encodermodel.EncoderUtil;
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
import java.util.Map;

/**
 * This class provides API to save/retrieve/update user session in database.
 *
 * @author Anju Jacob
 */

@Repository("SessionDetailDAO")
public class SessionDetailDAO {

    private final Logger logger = LoggerFactory
            .getLogger(SessionDetailDAO.class);

    private StatusMessage statusMessage = null;
    private StatusMessage.STATUS status = StatusMessage.STATUS.SUCCESS;
    private static final String SESSION_COLLECTION = "session_details";
    private static final String SESSION_COLLECTION_IOS = "session_details_ios";
    private static final String SUB_SESSION_COLLECTION = "sub_session_details";
    private String statusDescription;
    private String endTimeField = "endTime";

    private final MongoOperations mongoOperations;

    @Autowired
    public SessionDetailDAO(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;

    }

    /**
     * Saves user session in database
     *
     * @param sessionDetail
     * @return
     */
    public String startSession(String companyId, String storeId, SessionDetail sessionDetail,String platform) {
        logger.debug("About to save user_Session for u_id : {}",
                sessionDetail.getUserId());
        String sessionId = null;
        String storeCollectionName = DBUtil.getCollectionName(companyId, storeId, SESSION_COLLECTION);
        try {
            if(platform.equalsIgnoreCase(Constants.PLATFORM_IOS)) {
                storeCollectionName = DBUtil.getCollectionName(companyId, storeId, SESSION_COLLECTION_IOS);
            }
            mongoOperations.insert(sessionDetail, storeCollectionName);
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(sessionDetail.getSessionId()));
            SessionDetail sessionDetail1 = mongoOperations.findOne(query, SessionDetail.class, storeCollectionName);
            sessionId = sessionDetail1.getSessionId();
            if (sessionId != null && !sessionId.isEmpty()) {
                statusDescription = "Successfully created user_Session";
                statusMessage = new StatusMessage(status, statusDescription);
                logger.debug("{} for USER {} . Generated sessionId : {} "
                        , statusDescription, ContextHolder.getContext().getUserId(), sessionId);
            } else {

                statusDescription = "Unable to create session for userId :" + sessionDetail.getUserId();
            }


        } catch (Exception ex) {
            String errorMessage = "Exception in saving data : userId ="
                    + sessionDetail.getUserId() + " : timestamp ="
                    + sessionDetail.getStartTime() + "." + ex.getMessage();
            logger.error("USER {} :{} , {}",ContextHolder.getContext().getUserId(),errorMessage, ex);
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, errorMessage);
        }
        return sessionId;

    }

    public StatusMessage startCompleteSession(String companyId,Map<SessionDetail,List<SubSessionDetail>> sessionDetail) {
        logger.debug("About to save user_Session for USER {}",ContextHolder.getContext().getUserId());
        String sessionId = null;
        try {
            for(SessionDetail session : sessionDetail.keySet()){
                String storeId =session.getStoreId();
                final String sessionCollectionName = DBUtil.getCollectionName(companyId,storeId, SESSION_COLLECTION);
                final String subSessionCollectionName = DBUtil.getCollectionName(companyId,storeId, SUB_SESSION_COLLECTION);
                mongoOperations.insert(session,sessionCollectionName);
                sessionId = session.getSessionId();
                if(sessionId == null || sessionId.isEmpty()) {
                    statusDescription = "Unable to create session ";
                    statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE,statusDescription);
                }else {
                    for (SubSessionDetail subSession : sessionDetail.get(session)) {

                        String encodedLocationName;
                        try {
                            encodedLocationName = EncoderUtil.encode(subSession.getLocationName());
                        } catch (EncoderException e) {
                            logger.error("Unable to encodedLocationName locationName for USER {} : {} ",ContextHolder.getContext().getUserId(), e);
                            encodedLocationName = subSession.getLocationName();
                        }
                        subSession.setLocationName(encodedLocationName);

                            subSession.setSessionId(sessionId);
                            mongoOperations.insert(subSession,subSessionCollectionName);

                    }
                    statusDescription = "Successfully created user_Session.";
                    statusMessage = new StatusMessage(status, statusDescription);
                    logger.debug("{} . Generated sessionId for USER {} : {} "
                            , statusDescription,ContextHolder.getContext().getUserId(), sessionId);
                }
            }
        } catch (Exception ex) {
            String errorMessage = "Exception in saving data : " + ex.getMessage();
            logger.error("USER {} :{} {}",ContextHolder.getContext().getUserId(),errorMessage, ex);
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, errorMessage);
        }
        return statusMessage;

    }

    public List<String> getNonTerminatedSessions(String companyId, String storeId, String userId) {
        List<SessionDetail> nonTerminatedSessionList = null;
        final List<String> sessionDetailList = new ArrayList<>();
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("userId").is(userId).and(endTimeField).exists(false));
            nonTerminatedSessionList = mongoOperations.find(query, SessionDetail.class, DBUtil.getCollectionName(companyId, storeId, SESSION_COLLECTION));
            if (nonTerminatedSessionList != null && !nonTerminatedSessionList.isEmpty()) {

                for (SessionDetail sessionDetail : nonTerminatedSessionList) {

                    sessionDetailList.add(sessionDetail.getSessionId());

                }
            }

        } catch (Exception ex) {
            logger.error("Exception in getNonTerminatedSessions : userId {} " ,ContextHolder.getContext().getUserId(), ex);
        }
        return sessionDetailList;
    }


    public StatusMessage endSession(String companyId, String storeId, String userId, String sessionId, Timestamp endTime, Boolean isValid) {
        logger.debug("About to update user_Session for u_id : {} "
                , userId);

        try {

            final Criteria criteria = Criteria.where("userId").is(userId);
            Query query = Query.query(criteria.andOperator(Criteria.where("_id").is(sessionId)));

            Update update = new Update();
            update.set(endTimeField, new Date(endTime.getTime()));
            update.set("isValid", isValid);

            WriteResult writeResult = mongoOperations.updateFirst(query, update, SessionDetail.class,
                    DBUtil.getCollectionName(companyId, storeId, SESSION_COLLECTION));

            if (writeResult.getN() <= 0) {
                statusDescription = "No matches found for the userId : " + userId + ", sessionId : "
                        + sessionId + ".";
                statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, statusDescription);
            } else {
                statusDescription = "Successfully updated. updateResults : "
                        + writeResult + ".";

                statusMessage = new StatusMessage(status, statusDescription);
            }

            logger.debug("{} for USER {}",statusDescription,ContextHolder.getContext().getUserId());

        } catch (Exception ex) {
            String errorMessage = "Exception in updating user_Session : sessionId ="
                    + sessionId + ", endTime =" + endTime
                    + "." + ex.getMessage();
            logger.error("USER {} : {} , {}",ContextHolder.getContext().getUserId(),errorMessage, ex);
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILED_TO_CREATE_OR_UPDATE_SESSION, errorMessage);
        }
        return statusMessage;
    }

    public boolean isSessionTerminated(String companyId, String storeId, String sessionId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(sessionId).and(endTimeField).exists(true).
                and("isValid").exists(true));
        List<SessionDetail> sessionDetails = mongoOperations.find(query, SessionDetail.class,
                DBUtil.getCollectionName(companyId, storeId, SESSION_COLLECTION));
        return sessionDetails != null && !sessionDetails.isEmpty();
    }


}

