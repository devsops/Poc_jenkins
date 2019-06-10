package com.bosch.pai.retail.analytics.service;

import com.bosch.pai.retail.analytics.dao.EntryExitDAO;
import com.bosch.pai.retail.analytics.responses.EntryExitResponse;
import com.bosch.pai.retail.common.responses.StatusMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service("EntryExitAnalyticsService")
public class EntryExitAnalyticsService {

    @Autowired
    private EntryExitDAO eedao;

    @Autowired
    public EntryExitAnalyticsService() {
        //default constructor
    }

    public EntryExitResponse getEntryExit(String companyId, String storeId, String requestInterval, Long startTime, Long endTime,String platform) {

        if (startTime == null || endTime == null) {
            final EntryExitResponse entryExitResponse = new EntryExitResponse();

            entryExitResponse.setIntervalDetails(null);
            entryExitResponse.setEntryExitDetails(null);
            entryExitResponse.setStatusMessage(new StatusMessage(StatusMessage.STATUS.FAILURE, "StartTime or EndTime is null "));

            return entryExitResponse;
        }

        if (startTime >= endTime) {
            final EntryExitResponse entryExitResponse = new EntryExitResponse();

            entryExitResponse.setIntervalDetails(null);
            entryExitResponse.setEntryExitDetails(null);
            entryExitResponse.setStatusMessage(new StatusMessage(StatusMessage.STATUS.FAILURE, "StartTime is greaterthan or equal to EndTime "));

            return entryExitResponse;
        }
        final Timestamp startTimeInServerTimeZone = getServerTimestamp(startTime);
        final Timestamp endTimeInServerTimeZone = getServerTimestamp(endTime);

        return eedao.getEntryExit(companyId, storeId, requestInterval, startTimeInServerTimeZone, endTimeInServerTimeZone,platform);
    }

    private Timestamp getServerTimestamp(Long timestamp) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("GMT"));
        return Timestamp.from(zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toInstant());
    }
}
