package com.bosch.pai.retail.session.service;

import org.slf4j.Logger;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.session.dao.SessionDetailDAO;
import com.bosch.pai.retail.session.dao.SubSessionDetailDAO;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.db.model.SessionDetail;
import com.bosch.pai.retail.db.model.SubSessionDetail;
import com.bosch.pai.retail.encodermodel.EncoderException;
import com.bosch.pai.retail.encodermodel.EncoderUtil;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Service
public class RetailSessionService {

    private static final int STORE_OPENING_HOUR = 8;
    private static final int STORE_CLOSING_HOUR = 23;
    private static final int MAX_TRACE_COUNT = 200;
    private static String[] exLoc = new String[]{"CHECKOUT", "EXIT", "BILLING"};
    private final Logger logger = LoggerFactory
            .getLogger(RetailSessionService.class);

    @Autowired
    private SessionDetailDAO sessionDetailDao;

    @Autowired
    private SubSessionDetailDAO subSessionDetailDao;

    @Autowired
    public RetailSessionService() {
        //default constructor
    }

    public String startUserSession(String companyId,
                                   String storeId, String userId, String site, String location, String platform) {


        final LocalDateTime localDateTime = LocalDateTime.now();
        final String sessionId;

        SessionDetail userSession = new SessionDetail();
        userSession.setUserId(userId);
        userSession.setStartTime(Timestamp.valueOf(localDateTime).getTime());
        sessionId = sessionDetailDao.startSession(companyId, storeId, userSession, platform);

        startSubSession(companyId, storeId, userId, sessionId, site, location, platform);
        return sessionId;
    }

    public String startSubSession(String companyId,
                                  String storeId,
                                  String userId,
                                  String sessionId,
                                  String site,
                                  String location, String platform) {
        final LocalDateTime localDateTime = LocalDateTime.now();
        final Timestamp time = Timestamp.valueOf(localDateTime);


        String encodedLocationName;
        try {
            encodedLocationName = EncoderUtil.encode(location);
        } catch (EncoderException e) {
            logger.error("Unable to encodedLocationName locationName for USER {} : {} ", ContextHolder.getContext().getUserId(), e);
            encodedLocationName = location;
        }

        final List<String> subSessionIdList;
        final SubSessionDetail subSessionDetail = new SubSessionDetail();
        subSessionDetail.setStartTime(time.getTime());
        subSessionDetail.setUserId(userId);
        subSessionDetail.setSiteName(site);
        subSessionDetail.setLocationName(encodedLocationName);
        subSessionDetail.setSessionId(sessionId);
        subSessionIdList = subSessionDetailDao.getNonTerminatedSubSessions(companyId, storeId, userId, sessionId);
        if (subSessionIdList != null && !subSessionIdList.isEmpty()) {
            logger.debug("NonTerminatedSubSessions for USER {}: {} ", ContextHolder.getContext().getUserId(), subSessionIdList);
            final StatusMessage status;
            status = subSessionDetailDao.endSubSession(companyId, storeId, userId, subSessionIdList.get(0), time, true);
            logger.debug("Ending last sub session for USER {}. status  : {} ", ContextHolder.getContext().getUserId(), status);

        }
        return subSessionDetailDao.saveSubSessionDetail(companyId, storeId, subSessionDetail, platform);

    }


    public StatusMessage endUserSession(String companyId, String storeId, String userId, String sessionId, String platform) {
        final LocalDateTime localDateTime = LocalDateTime.now();
        final Timestamp endTime = Timestamp.valueOf(localDateTime);
        final StatusMessage status;
        status = sessionDetailDao.endSession(companyId, storeId, userId, sessionId, endTime, true);
        List<String> subSessionIdList;
        subSessionIdList = subSessionDetailDao.getNonTerminatedSubSessions(companyId, storeId, userId, sessionId);
        updateSubSessions(companyId, storeId, userId, endTime, subSessionIdList, true, platform);

        return status;
    }

    public void endPreviousSessions(String companyId, String storeId, String userId, String platform) {
        StatusMessage status;
        final LocalDateTime localDateTime = LocalDateTime.now();
        final Timestamp endTime = Timestamp.valueOf(localDateTime);
        final List<String> sessionIdList;
        sessionIdList = sessionDetailDao.getNonTerminatedSessions(companyId, storeId, userId);
        if (sessionIdList != null && !sessionIdList.isEmpty()) {
            logger.debug("endPreviousSessions : sessionIdList  for USER {} : {} ", ContextHolder.getContext().getUserId(), sessionIdList);
            for (String sessionId : sessionIdList) {
                if (sessionId != null && sessionId.trim().length() > 0) {
                    status = sessionDetailDao.endSession(companyId, storeId, userId, sessionId, endTime, false);
                    logger.debug("Session update status for sessionId : {} , status : {} ", sessionId, status);
                    final List<String> subSessionIdList;
                    subSessionIdList = subSessionDetailDao.getNonTerminatedSubSessions(companyId, storeId, userId, sessionId);
                    logger.debug("endPreviousSessions : subSessionIdList : {} ", subSessionIdList);
                    updateSubSessions(companyId, storeId, userId, endTime, subSessionIdList, false, platform);
                }
            }
        }

    }

    private void updateSubSessions(String companyId, String storeId, String userId, Timestamp endTime, List<String> subSessionIdList,
                                   Boolean isValid, String platform) {
        StatusMessage status;
        if (subSessionIdList != null && !subSessionIdList.isEmpty()) {
            for (String subSessionId : subSessionIdList) {
                if (subSessionId != null && subSessionId.trim().length() > 0) {
                    status = subSessionDetailDao.endSubSession(companyId, storeId, userId, subSessionId, endTime, isValid);
                    logger.debug("SubSession update status for sessionId : {} , status : {} ", subSessionId, status);
                }
            }
        }
    }

    public String checkAndUpdateSessionDetails(String companyId, String storeId, String userId, String sessionId, String site,
                                               String location, String timeZoneID, boolean isExitLocation, String platform) {


        String sessionIDCreated = sessionId;

        if (isCurrentTimeValid(timeZoneID)) {

            /*Check sessionId
            * if sessionId is empty -->
            *             End previous non terminated sessions
            *             Start new session
            * if sessionId is non empty --->
            *             Start new SubSession
            */
            if (sessionId == null || sessionId.isEmpty() || "NULL".equalsIgnoreCase(sessionId)) {
                endPreviousSessions(companyId, storeId, userId, platform);
                sessionIDCreated = startUserSession(companyId, storeId, userId, site, location, platform);
            } else {

                if (isSessionValid(companyId, storeId, sessionId, platform)) {
                    if (isExitLocation) {
                        endUserSession(companyId, storeId, userId, sessionId, platform);
                    } else if (isMoreTraceAllowed(companyId, storeId, sessionId, platform)) {
                        startSubSession(companyId, storeId, userId, sessionId, site, location, platform);
                    }
                }
            }
        } else {
            logger.debug("Session out of store hours for USER {} : {}", ContextHolder.getContext().getUserId(), LocalTime.now(ZoneId.of(timeZoneID)));
        }
        return sessionIDCreated;
    }

    public StatusMessage startSessionSubSession(String companyId, Map<SessionDetail, List<SubSessionDetail>> sessionDetail, String devicetype) {
        StatusMessage statusMessage = null;
        StatusMessage.STATUS status = StatusMessage.STATUS.SUCCESS;
        String statusDescription;
        try {
            for (SessionDetail session : sessionDetail.keySet()) {
                String sessionId = null;
                if (session.getStartTime() < session.getEndTime() && isCurrentEpochTimeValid(session.getStartTime())
                        && isCurrentEpochTimeValid(session.getEndTime())) {
                    if (sessionDetail.values().size() < MAX_TRACE_COUNT) {
                        sessionId = sessionDetailDao.startSession(companyId, session.getStoreId(), session, devicetype);
                    }
                }
                if (sessionId == null || sessionId.isEmpty()) {
                    statusDescription = "Unable to create session";
                    logger.debug("Unable to create session for USER {} with Session Id :{}", ContextHolder.getContext().getUserId(), session.getSessionId());
                    statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, statusDescription);
                } else {
                    for (SubSessionDetail subSession : sessionDetail.get(session)) {
                        startSubSession(subSession, sessionId, companyId, devicetype);
                    }
                    statusDescription = "Successfully created user_Session.";
                    statusMessage = new StatusMessage(status, statusDescription);
                    logger.debug("{} . Generated sessionId for USER {} : {} "
                            , statusDescription, ContextHolder.getContext().getUserId(), sessionId);
                }
            }
        } catch (Exception ex) {
            String errorMessage = "Exception in saving data : " + ex.getMessage();
            logger.error("USER {} :{} {}", ContextHolder.getContext().getUserId(), errorMessage, ex);
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, errorMessage);
        }
        return statusMessage;
    }


    private void startSubSession(SubSessionDetail subSession, String sessionId, String companyId, String platform) {
        if (!val1ExlInL(subSession.getLocationName())) {
//            String encodedLocationName;
//            try {
//                encodedLocationName = EncoderUtil.encode(subSession.getLocationName());
//            } catch (EncoderException e) {
//                logger.error("Unable to encodedLocationName locationName for USER {} : {} ", ContextHolder.getContext().getUserId(), e);
//                encodedLocationName = subSession.getLocationName();
//            }
//            subSession.setLocationName(encodedLocationName);
            subSession.setSessionId(sessionId);
            subSessionDetailDao.saveSubSessionDetail(companyId, subSession.getStoreId(), subSession, platform);
        }
    }

    private boolean isSessionValid(String companyId, String storeId, String sessionId, String platform) {

        return !sessionDetailDao.isSessionTerminated(companyId, storeId, sessionId);

    }

    private boolean isMoreTraceAllowed(String companyId, String storeId, String sessionId, String platform) {
        return subSessionDetailDao.getSubSessionCount(companyId, storeId, sessionId) < MAX_TRACE_COUNT;
    }

    private boolean isCurrentTimeValid(String timeZoneID) {
        final LocalTime time = LocalTime.now(ZoneId.of(timeZoneID));
        return time.isAfter(LocalTime.of(STORE_OPENING_HOUR, 0)) && time.isBefore(LocalTime.of(STORE_CLOSING_HOUR, 0));
    }

    private boolean isCurrentEpochTimeValid(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        final LocalTime localTime = LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY), 0);
        return localTime.isAfter(LocalTime.of(STORE_OPENING_HOUR, 0)) && localTime.isBefore(LocalTime.of(STORE_CLOSING_HOUR, 0));
    }

    private boolean val1ExlInL(String l) {
        return Arrays.asList(exLoc).contains(l.toUpperCase());
    }

}