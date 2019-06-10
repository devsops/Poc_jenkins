package com.bosch.pai.retail.analytics.service;

import com.bosch.pai.retail.analytics.dao.LocationsDAO;
import com.bosch.pai.retail.analytics.dao.SubSessionDetailDAO;
import com.bosch.pai.retail.analytics.exception.AnalyticsServiceException;
import com.bosch.pai.retail.analytics.model.dwelltime.LocationDwellTime;
import com.bosch.pai.retail.analytics.model.heatmap.HeatMapDetail;
import com.bosch.pai.retail.analytics.responses.UserDwellTimeAnalyticsResponse;
import com.bosch.pai.retail.analytics.responses.UserHeatMapAnalyticsResponse;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.configmodel.SiteLocations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("UserSessionAnalyticsService")
public class UserSessionAnalyticsService {

    @Autowired
    private SubSessionDetailDAO ssdao;

    @Autowired
    private LocationsDAO locationsDAO;

    @Autowired
    public UserSessionAnalyticsService() {
        //default constructor
    }


    public List<HeatMapDetail> getHeatMaps(String companyId, String storeId, String siteName, String locationName,
                                           Long startTime, Long endTime, String platform) throws AnalyticsServiceException {

        if (startTime != null && endTime != null) {
            final Set<String> locations = getLocations( companyId, storeId, platform);
            return ssdao.getHeatMaps(startTime, endTime, companyId, storeId, siteName, locationName, locations, platform);
        } else {
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.INVALID_INPUTS,
                    "Invalid values for startTime(" + startTime + ") and endTime(" + endTime + ")"));
        }

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

        /*final List<SiteLocationDetails> siteLocationList = sldao.getSiteLocationList(companyId, storeId, platform);
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

    public List<LocationDwellTime> getDwellTimeDetails(String companyId, String storeId, String siteName, String locationName,
                                                       Long startTime, Long endTime, String platform) throws AnalyticsServiceException {
        if (startTime != null && endTime != null) {
            final Set<String> locations = getLocations( companyId, storeId, platform);
            return ssdao.getDwellTimeDetails(startTime, endTime, companyId, storeId, siteName, locationName, locations, platform);
        } else {

            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.INVALID_INPUTS,
                    "Invalid values for startTime(" + startTime + ") and endTime(" + endTime + ")"));
        }
    }

    private Timestamp convertToServerTimeZone(Long timestamp) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("GMT"));
        return Timestamp.from(zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toInstant());
    }

    public List<UserDwellTimeAnalyticsResponse> getHierarchyDwellTimeDetails(String companyId, String storeId, String siteName, Long startTime, Long endTime, String platform, Map<String, List<String>> hierarchyLevelNameMap) {
        if (startTime == null || endTime == null || startTime >= endTime  ){
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILURE,"Invalid Start or End Time "));
        }
        return ssdao.getHierarchyDwellTime(companyId,storeId,siteName,startTime,endTime,platform,hierarchyLevelNameMap);
    }

    public List<UserHeatMapAnalyticsResponse> getHierarchyHeatMaps(String companyId, String storeId,String siteName, Long startTime, Long endTime, String platform, Map<String,List<String>> hierarchyLevelNameMap) {
        if (startTime == null || endTime == null || startTime >= endTime  ){
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILURE,"Invalid Start or End Time "));
        }
        return ssdao.getHierarchyHeatMap(companyId,storeId,siteName, startTime,endTime,platform,hierarchyLevelNameMap);
    }
}