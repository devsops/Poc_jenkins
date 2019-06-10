package com.bosch.pai.retail.analytics.controllers;


import com.bosch.pai.retail.analytics.exception.AnalyticsServiceException;
import com.bosch.pai.retail.analytics.responses.EntryExitResponse;
import com.bosch.pai.retail.analytics.service.EntryExitAnalyticsService;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class EntryExitAnalysisController {

    private final Logger logger = LoggerFactory
            .getLogger(EntryExitAnalysisController.class);

    private EntryExitAnalyticsService entryExitAnalyticsService;

    @Autowired
    public EntryExitAnalysisController(EntryExitAnalyticsService entryExitAnalyticsService) {
        this.entryExitAnalyticsService = entryExitAnalyticsService;
    }


    @RequestMapping(
            method = RequestMethod.GET,
            value = {
                    "companies/{companyId}/stores/{storeId}/getEntryExit/",
            },
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EntryExitResponse getEntryExitAnalytics(@PathVariable("companyId") String ci,
                                                   @PathVariable("storeId") String si,
                                                   @RequestParam(name = "requestInterval", required = false) String ri,
                                                   @RequestParam(name = "startTime") Long st,
                                                   @RequestParam(name = "endTime") Long et,
                                                   @RequestHeader(value = "platform", defaultValue = "android") String platform

    ) {

        logger.debug("getEntryExit getEntryExitRequest for User {}: companyId: {}, storeId : {}, requestInterval : {} ,startTime : {}, endTime : {} ",
                ContextHolder.getContext().getUserId(),ci, si, ri, st, et);
        try {

            long startTime = System.nanoTime();

            final EntryExitResponse entryExitResponse = entryExitAnalyticsService.getEntryExit(ci, si, ri, st, et,platform);

            long endTime = System.nanoTime();

            long duration = (endTime - startTime) / 1000000;

            logger.debug("**************** TIME TAKEN FROM THIS METHOD FOR EXECUTION FOR USER {} : {} millisec",ContextHolder.getContext().getUserId(), duration);

            logger.debug("getEntryExit for USER {}: {}",ContextHolder.getContext().getUserId(), entryExitResponse);

            return entryExitResponse;

        } catch (Exception e) {
            logger.error("Exception occurred while fetching entry exit for USER {}. "
                    , ContextHolder.getContext().getUserId(),e);
            throw new AnalyticsServiceException(new StatusMessage(StatusMessage.STATUS.FAILED_TO_FETCH_ENTRYEXIT, e.getMessage()), e);
        }
    }

}
