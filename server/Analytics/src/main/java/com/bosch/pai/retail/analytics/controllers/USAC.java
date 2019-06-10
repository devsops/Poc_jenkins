package com.bosch.pai.retail.analytics.controllers;

import com.bosch.pai.retail.analytics.exception.AnalyticsServiceException;
import com.bosch.pai.retail.analytics.model.dwelltime.LocationDwellTime;
import com.bosch.pai.retail.analytics.model.heatmap.HeatMapDetail;
import com.bosch.pai.retail.analytics.responses.UserDwellTimeAnalyticsResponse;
import com.bosch.pai.retail.analytics.responses.UserHeatMapAnalyticsResponse;
import com.bosch.pai.retail.analytics.service.UserSessionAnalyticsService;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class USAC {

    private final Logger logger = LoggerFactory
            .getLogger(USAC.class);
    @Autowired
    private UserSessionAnalyticsService usas;

    @RequestMapping(
            method = RequestMethod.GET,
            value = {
                    "companies/{companyId}/stores/{storeId}/sites/HeatMap/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/HeatMap/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/{locationName}/HeatMap/"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<HeatMapDetail> gehs(@PathVariable("companyId") String ci,
                                    @PathVariable("storeId") String sti,
                                    @PathVariable(value = "siteName", required = false) String sn,
                                    @PathVariable(value = "locationName", required = false) String ln,
                                    @RequestParam(name = "startTime") Long st,
                                    @RequestParam(name = "endTime") Long et,
                                    @RequestHeader(value = "platform", defaultValue = "android") String platform
    ) throws AnalyticsServiceException {

        logger.debug("getHeatMaps getHeatMapRequest for USER {} : companyId: {}, storeId : {}, siteName : {}, locationId : {},  startTime : {},  endTime : {}"
                , ContextHolder.getContext().getUserId(), ci, sti, sn, ln, st, et);
        try {
            List<HeatMapDetail> lslu = usas.getHeatMaps(ci, sti, sn, ln, st, et, platform);
            logger.debug("getHeatMapResponse for USER {} : {}", ContextHolder.getContext().getUserId(), lslu);
            return lslu;
        } catch (Exception e) {
            logger.error("Exception occurred while fetching heatmap for USER {} . "
                    , ContextHolder.getContext().getUserId(), e);
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_HEATMAP, e.getMessage()), e);
        }
    }

    @RequestMapping(
            value = {"companies/{companyId}/stores/{storeId}/sites/DwellTime/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/DwellTime/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/{locationName}/DwellTime/"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<LocationDwellTime> gdd(@PathVariable("companyId") String ci,
                                       @PathVariable(value = "storeId") String sti,
                                       @PathVariable(value = "siteName", required = false) String sn,
                                       @PathVariable(value = "locationName", required = false) String ln,
                                       @RequestParam(name = "startTime") Long st,
                                       @RequestParam(name = "endTime") Long et,
                                       @RequestHeader(value = "platform", defaultValue = "android") String platform
    ) throws AnalyticsServiceException {
        try {

            logger.debug("Controller received getDwellTimeDetails request for USER {} : siteId : {}, " +
                            "locationId : {},  startTime : {},  endTime : {} ",
                    ContextHolder.getContext().getUserId(), sn, ln, st, et);
            List<LocationDwellTime> ldtl = usas.getDwellTimeDetails(ci, sti, sn, ln, st, et, platform);

            logger.debug("Controller response for gdd request for USER {} : {}"
                    , ContextHolder.getContext().getUserId(), ldtl);
            return ldtl;
        } catch (Exception e) {
            logger.error("Exception occurred while fetching dwelltime details for USER {}. "
                    , ContextHolder.getContext().getUserId(), e);
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_DWELL_TIME_ANALYTICS, e.getMessage()), e);
        }
    }

    @RequestMapping(
            value = {"companies/{companyId}/stores/{storeId}/dwelltime",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/dwelltime"
            },
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<UserDwellTimeAnalyticsResponse> getHierarchyDwellTime(@PathVariable("companyId") String ci,
                                                                      @PathVariable(value = "storeId") String storeId,
                                                                      @PathVariable(value = "siteName" , required = false) String siteName,
                                                                      @RequestParam(name = "startTime") String startTime,
                                                                      @RequestParam(name = "endTime") String endTime,
                                                                      @RequestHeader(value = "platform", defaultValue = "android") String platform,
                                                                      @RequestParam(value = "hierarchyMap", required = false) String hierarchyMap
    ) {
        try {
            Long start = Long.parseLong(new String(Base64.getDecoder().decode(startTime)));
            Long end = Long.parseLong(new String(Base64.getDecoder().decode(endTime)));
            Map<String, List<String>> hierarchyLevelNameMap = new HashMap<>();
            if (hierarchyMap != null && !hierarchyMap.isEmpty()) {
                Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
                Type type = new TypeToken<Map<String, List<String>>>() {
                }.getType();
                String decodedHierarchyMap = new String(Base64.getDecoder().decode(hierarchyMap));
                hierarchyLevelNameMap = gson.fromJson(decodedHierarchyMap, type);
            }
            logger.debug("Controller received getHierarchyDwellTimeDetails request for USER {},  startTime : {},  endTime : {} ",
                    ContextHolder.getContext().getUserId(), startTime, endTime);
            List<UserDwellTimeAnalyticsResponse> dwellTimeResponse = usas.getHierarchyDwellTimeDetails(ci, storeId,siteName, start, end, platform, hierarchyLevelNameMap);
            logger.debug("Controller response for gdd request for USER {} : {}", ContextHolder.getContext().getUserId(), dwellTimeResponse);
            return dwellTimeResponse;
        } catch (Exception e) {
            logger.error("Exception occurred while fetching dwelltime details for USER {}. "
                    , ContextHolder.getContext().getUserId(), e);
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_DWELL_TIME_ANALYTICS, e.getMessage()), e);
        }
    }


    @RequestMapping(
            method = RequestMethod.GET,
            value = {"companies/{companyId}/stores/{storeId}/heatmap",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/heatmap"
            },
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<UserHeatMapAnalyticsResponse> getHierarchyHeatMap(@PathVariable("companyId") String companyId,
                                                                  @PathVariable("storeId") String storeId,
                                                                  @PathVariable(value = "siteName",required = false) String siteName,
                                                                  @RequestParam(name = "startTime") String startTime,
                                                                  @RequestParam(name = "endTime") String endTime,
                                                                  @RequestHeader(value = "platform", defaultValue = "android") String platform,
                                                                  @RequestParam(value = "hierarchyMap", required = false) String hierarchyMap
//                                                   @RequestParam(required = false) MultiValueMap<String, List<String>> hierarchyLevelNameMap
    ) {
        Long start = Long.parseLong(new String(Base64.getDecoder().decode(startTime)));
        Long end = Long.parseLong(new String(Base64.getDecoder().decode(endTime)));
        Map<String, List<String>> hierarchyLevelNameMap = new HashMap<>();
        if (hierarchyMap != null && !hierarchyMap.isEmpty()) {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            Type type = new TypeToken<Map<String, List<String>>>() {
            }.getType();
            String decodedHierarchyMap = new String(Base64.getDecoder().decode(hierarchyMap));
            hierarchyLevelNameMap = gson.fromJson(decodedHierarchyMap, type);
        }
        logger.debug("getHeatMaps getHierarchyHeatMap for USER {} : companyId: {}, storeId : {},  startTime : {},  endTime : {}"
                , ContextHolder.getContext().getUserId(), companyId, storeId, startTime, endTime);
        try {
            List<UserHeatMapAnalyticsResponse> lslu = usas.getHierarchyHeatMaps(companyId, storeId,siteName, start, end, platform, hierarchyLevelNameMap);
            logger.debug("getHeatMapResponse for USER {} : {}", ContextHolder.getContext().getUserId(), lslu);
            return lslu;
        } catch (Exception e) {
            logger.error("Exception occurred while fetching heatmap for USER {} . "
                    , ContextHolder.getContext().getUserId(), e);
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_HEATMAP, e.getMessage()), e);
        }
    }
}
