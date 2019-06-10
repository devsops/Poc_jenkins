package com.bosch.pai.retail.analytics.service;

import com.bosch.pai.retail.analytics.dao.LocationsDAO;
import com.bosch.pai.retail.analytics.dao.OfferResponseDAO;
import com.bosch.pai.retail.analytics.exception.AnalyticsServiceException;
import com.bosch.pai.retail.analytics.responses.OfferAnalyticsResponse;
import com.bosch.pai.retail.analytics.responses.UserOfferAnalyticsResponse;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.configmodel.SiteLocations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("OfferAnalyticsService")
public class OfferAnalyticsService {

    @Autowired
    private LocationsDAO locationsDAO;

    @Autowired
    private OfferResponseDAO ord;

    @Autowired
    public OfferAnalyticsService() {
        //default constructor
    }


    public List<OfferAnalyticsResponse> goa(Long startTime, Long endTime, String companyId, String storeId, String siteName, String locationName, String platform) throws AnalyticsServiceException {
        if (startTime == null || endTime == null) {
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_OFFER_ANALYTICS, "Request not valid."));
        }
//        final Timestamp startTimeInServerTimeZone = getServerTimestamp(startTime);
//        final Timestamp endTimeInServerTimeZone = getServerTimestamp(endTime);

        Set<String> locations = getLocations(companyId, storeId, platform);
        return ord.getOfferAnalytics(startTime, endTime, companyId, storeId, siteName, locationName, locations, platform);
    }

    private Timestamp getServerTimestamp(Long timestamp) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("GMT"));
        return Timestamp.from(zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toInstant());
    }

    public Set<String> getLocations(String companyId, String storeId, String platform) {

        List<SiteLocations> allSiteLocations = locationsDAO.getAllLocations(companyId, storeId, null, platform);
        if (allSiteLocations == null || allSiteLocations.isEmpty()) {
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.INVALID_INPUTS,
                    "Unable to fetch siteLocation list for company(" + companyId + ") and storeId(" + storeId + ")"));
        } else {
            final Set<String> locations = new HashSet<>();
            allSiteLocations.forEach(siteLocation -> {
                        Set<String> locations1 = siteLocation.getLocations();
                        if (locations1 != null) {
                            locations.addAll(locations1);
                        }
                    }
            );
            return locations;
        }


      /*  final List<SiteLocationDetails> siteLocationList = siteLocationDAO.getSiteLocationList(companyId, storeId, platform);
        if (siteLocationList == null || siteLocationList.isEmpty()) {
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.INVALID_INPUTS,
                    "Unable to fetch siteLocation list for company(" + companyId + ") and storeId(" + storeId + ")"));
        } else {
            final Set<String> locations = new HashSet<>();
            siteLocationList.forEach(siteLocation ->
                    locations.add(siteLocation.getLocationName())
            );
            return locations;
        }*/
    }

    public List<UserOfferAnalyticsResponse> getOfferAnalytics(Long startTime, Long endTime, String siteName, String companyId, String storeId, Map<String, List<String>> hierarchyLevelNameMap, String platform) {
        if (startTime == null || endTime == null) {
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_OFFER_ANALYTICS, "Request not valid."));
        }
        return ord.getOfferAnalytics(startTime, endTime,siteName, companyId, storeId, hierarchyLevelNameMap, platform);
    }
}

