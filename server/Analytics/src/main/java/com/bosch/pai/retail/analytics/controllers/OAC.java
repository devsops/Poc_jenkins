package com.bosch.pai.retail.analytics.controllers;

import com.bosch.pai.retail.analytics.exception.AnalyticsServiceException;
import com.bosch.pai.retail.analytics.responses.OfferAnalyticsResponse;
import com.bosch.pai.retail.analytics.responses.UserOfferAnalyticsResponse;
import com.bosch.pai.retail.analytics.service.OfferAnalyticsService;
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
public class OAC {


    private final Logger logger = LoggerFactory
            .getLogger(OAC.class);
    @Autowired
    private OfferAnalyticsService offerAnalyticService;

    /**
     * gives offer analytics
     *
     * @return
     */

    @RequestMapping(
            value = {
                    "companies/{companyId}/stores/{storeId}/sites/OfferAnalytics/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/OfferAnalytics/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/{locationName}/OfferAnalytics/"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<OfferAnalyticsResponse> goa(@PathVariable(name = "companyId") String ci,
                                            @PathVariable(name = "storeId") String storeId,
                                            @PathVariable(name = "siteName", required = false) String siteName,
                                            @PathVariable(name = "locationName", required = false) String locationName,
                                            @RequestParam(name = "startTime") Long startTime,
                                            @RequestParam(name = "endTime") Long endTime,
                                            @RequestHeader(value = "platform", defaultValue = "android") String platform
        ) throws AnalyticsServiceException {
        logger.debug("Controller received getOfferAnalytics for USER {} :startTime : {}, endTime : {},  siteName : {},locationName : {}",
                ContextHolder.getContext().getUserId(), startTime, endTime, siteName, locationName);
        try {
            final List<OfferAnalyticsResponse> oa = offerAnalyticService.goa(startTime, endTime, ci, storeId, siteName, locationName,platform);
            logger.debug("Controller response for getOfferAnalytics for USER {} : {} "
                    ,ContextHolder.getContext().getUserId(), oa);
            return oa;
        } catch (Exception e) {
            logger.error("Exception occurred while fetching offer analytics for USER {}. "
                    ,ContextHolder.getContext().getUserId(), e);
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_OFFER_ANALYTICS, e.getMessage()), e);
        }
    }


    @RequestMapping(
            value = {"companies/{companyId}/stores/{storeId}/offeranalytics",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/offeranalytics"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<UserOfferAnalyticsResponse> getOfferAnalytics(
                                            @PathVariable(name = "companyId") String companyId,
                                            @PathVariable(name = "storeId") String storeId,
                                            @PathVariable(name = "siteName",required = false)String siteName,
                                            @RequestParam(name = "startTime") String startTime,
                                            @RequestParam(name = "endTime") String endTime,
                                            @RequestHeader(value = "platform", defaultValue = "android")String platform ,
                                            @RequestParam(value = "hierarchyMap",required = false) String hierarchyMap
    ) {
        logger.debug("Controller received getOfferAnalytics for USER {} :startTime : {}, endTime : {}",
                ContextHolder.getContext().getUserId(), startTime, endTime);
        Long start = Long.parseLong(new String(Base64.getDecoder().decode(startTime)));
        Long end = Long.parseLong(new String(Base64.getDecoder().decode(endTime)));
        try {
            Map<String, List<String>> hierarchyLevelNameMap = new HashMap<>();
            if (hierarchyMap != null && !hierarchyMap.isEmpty()) {
                Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
                Type type = new TypeToken<Map<String, List<String>>>() {
                }.getType();
                String decodedHierarchyMap = new String(Base64.getDecoder().decode(hierarchyMap));
                hierarchyLevelNameMap = gson.fromJson(decodedHierarchyMap, type);
            }
            final List<UserOfferAnalyticsResponse> oa = offerAnalyticService.getOfferAnalytics(start, end,siteName, companyId, storeId,hierarchyLevelNameMap,platform);
            logger.debug("Controller response for getOfferAnalytics for USER {} : {} "
                    ,ContextHolder.getContext().getUserId(), oa);
            return oa;
        } catch (Exception e) {
            logger.error("Exception occurred while fetching offer analytics for USER {}. "
                    ,ContextHolder.getContext().getUserId(), e);
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_OFFER_ANALYTICS, e.getMessage()), e);
        }
    }

}
