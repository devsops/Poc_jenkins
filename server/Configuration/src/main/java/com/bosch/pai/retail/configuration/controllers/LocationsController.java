package com.bosch.pai.retail.configuration.controllers;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.configmodel.SiteLocations;
import com.bosch.pai.retail.configuration.service.LocationsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * Created by sjn8kor on 3/9/2018.
 */

@EnableAutoConfiguration
@RestController
public class LocationsController {

    private final Logger logger = LoggerFactory.getLogger(LocationsController.class);

    @Autowired
    private LocationsService locationsService;

    /**
     * Saves the store location to db
     *
     * @param
     * @return
     */
    @RequestMapping(
            value = {
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/SaveOrUpdateLocations/"
            },
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<StatusMessage> saveOrUpdateLocations(@PathVariable(name = "companyId", required = true) String companyId,
                                                               @PathVariable(name = "storeId", required = true) String storeId,
                                                               @PathVariable(name = "siteName", required = true) String siteName,
                                                               @RequestBody Set<String> locations,
                                                               @RequestHeader(value = "platform", defaultValue = "android") String platform) {

        final String userId = getUserIdFromHeader();

        logger.debug("Controller received for userId : {} , locations  {}.", userId, locations);

        final StatusMessage sm =
                locationsService.saveOrUpdateLocations(companyId, storeId, siteName, locations, platform);

        final ResponseEntity<StatusMessage> responseEntity = new ResponseEntity<>(sm, HttpStatus.ACCEPTED);

        logger.debug("Controller response for SaveOrUpdateLocations : {} ", responseEntity);

        return responseEntity;
    }

    @RequestMapping(
            value = {
                    "companies/{companyId}/stores/{storeId}/sites/getAllLocations/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/getAllLocations/"
            },
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SiteLocations>> getAllLocations(@PathVariable(name = "companyId", required = true) String companyId,
                                                               @PathVariable(name = "storeId", required = true) String storeId,
                                                               @PathVariable(name = "siteName", required = false) String siteName,
                                                               @RequestHeader(value = "platform", defaultValue = "android") String platform) {

        final String userId = getUserIdFromHeader();

        logger.debug("Controller received for userId : {} ", userId);

        final List<SiteLocations> sm =
                locationsService.getAllLocations(companyId, storeId, siteName, platform);

        final ResponseEntity<List<SiteLocations>> responseEntity = new ResponseEntity<>(sm, HttpStatus.ACCEPTED);

        logger.debug("Controller response for getAllLocations : {} ", responseEntity);

        return responseEntity;
    }

    private String getUserIdFromHeader() {
        String userId = "";
        if (ContextHolder.getContext().getUserId() != null) {
            userId = ContextHolder.getContext().getUserId();
        }
        return userId;
    }
}

