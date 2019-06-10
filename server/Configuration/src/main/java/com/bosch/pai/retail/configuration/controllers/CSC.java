package com.bosch.pai.retail.configuration.controllers;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.configmodel.BaymapDetail;
import com.bosch.pai.retail.configmodel.ConfigModel;
import com.bosch.pai.retail.configmodel.HierarchyDetail;
import com.bosch.pai.retail.configmodel.LocationCateDeptBrand;
import com.bosch.pai.retail.configmodel.SiteLocationDetails;
import com.bosch.pai.retail.configmodel.SiteLocationHierarchyDetail;
import com.bosch.pai.retail.configuration.service.ConfigurationService;
import com.bosch.pai.retail.requests.SaveLocationBaymapRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class CSC {


    private final Logger logger = LoggerFactory
            .getLogger(CSC.class);
    @Autowired
    private ConfigurationService configurationService;

    /**
     * Saves the store location to db
     *
     * @param
     * @return
     */
    @Deprecated
    @RequestMapping(
            value = {
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/{locationName}/SaveOrUpdateCateDeptBrandMappingDetails/"
            },
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<StatusMessage> saveOrUpdateCateDeptBrandMappingDetails(@PathVariable(name = "companyId", required = true) String companyId,
                                                                                 @PathVariable(name = "storeId", required = true) String storeId,
                                                                                 @PathVariable(name = "siteName", required = true) String siteName,
                                                                                 @PathVariable(name = "locationName", required = true) String locationName,
                                                                                 @RequestBody SaveLocationBaymapRequest saveLocationBaymapRequest,
                                                                                 @RequestHeader(value = "platform", defaultValue = "android") String platform) {

        final String userId = logUserInformation();


        LocationCateDeptBrand locationCateDeptBrand = null;
        StatusMessage statusMessage = null;
        final Gson gson = new Gson();
        final Type type = new TypeToken<LocationCateDeptBrand>() {
        }.getType();
        try {
            if (saveLocationBaymapRequest != null) {
                locationCateDeptBrand = gson.fromJson(saveLocationBaymapRequest.getLocationbaymapping(), type);
                logger.debug("override_required : {}", saveLocationBaymapRequest.isOverrideRequired());
                logger.debug("Controller received for userId : {} , saveOrUpdateCateDeptBrandMappingDetails  {}.", userId, locationCateDeptBrand);

                statusMessage = configurationService.saveOrUpdateCateDeptBrandMappingDetails(companyId, storeId, siteName,
                        locationName, locationCateDeptBrand, platform, saveLocationBaymapRequest.isOverrideRequired());
            } else {
                statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, "Body is null");
            }
        } catch (Exception e) {
            logger.debug("Exception in saveOrUpdateCateDeptBrandMappingDetails mrthod :  {}", e);
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, "Some error occurred");
        }
        final ResponseEntity<StatusMessage> responseEntity = new ResponseEntity<>(statusMessage, HttpStatus.ACCEPTED);
        logger.debug("Controller response for saveOrUpdateCateDeptBrandMappingDetails : {} ", responseEntity);
        return responseEntity;
    }

    private String logUserInformation() {
        String userId = "";
        if (ContextHolder.getContext().getUserId() != null) {
            userId = ContextHolder.getContext().getUserId();
        }
        return userId;
    }

    /**
     * fetches the detail for store and location
     *
     * @return
     */
    @Deprecated
    @RequestMapping(
            value = {
                    "companies/{companyId}/stores/{storeId}/sites/getCateDeptBrandMappingDetails/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/getCateDeptBrandMappingDetails/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/{locationName}/getCateDeptBrandMappingDetails/"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SiteLocationDetails>> glbm(@PathVariable(name = "companyId", required = true) String companyId,
                                                          @PathVariable(name = "storeId", required = true) String storeId,
                                                          @PathVariable(value = "siteName", required = false) String siteName,
                                                          @PathVariable(value = "locationName", required = false) String locationName,
                                                          @RequestHeader(value = "platform", defaultValue = "android") String platform

    ) {

        final String userId = logUserInformation();
        logger.debug("Controller received getCateDeptBrandMappingDetails for user : {} ", userId);
        logger.debug("platform . {} ", platform);

        final List<SiteLocationDetails> sl = configurationService.getCateDeptBrandMappingDetails(companyId, storeId, siteName, locationName, platform);

        final ResponseEntity<List<SiteLocationDetails>> responseEntity = new ResponseEntity<>(sl,
                HttpStatus.ACCEPTED);
        logger.debug("Controller response for getCateDeptBrandMappingDetails : {} "
                , responseEntity);
        return responseEntity;
    }

    @Deprecated
    @RequestMapping(
            value = {
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/{locationName}/getLocationInfo/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/getLocationInfo/"
            },
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SiteLocationDetails> getLocationInfo(@PathVariable(name = "companyId", required = true) String companyId,
                                                               @PathVariable(name = "storeId", required = true) String storeId,
                                                               @PathVariable(value = "siteName", required = false) String siteName,
                                                               @PathVariable(value = "locationName", required = false) String locationName,
                                                               @RequestHeader(value = "platform", defaultValue = "android") String platform
    ) {
        final String userId = logUserInformation();
        logger.debug("Controller received getLocationInfo for user : {}.", userId);

        final ResponseEntity responseEntity = configurationService.getLocationDetail(companyId, storeId, siteName, locationName, platform);

        logger.debug("Controller response for getLocationInfo : {} "
                , responseEntity);
        return responseEntity;
    }


    /**
     * fetches the detail for store and location
     *
     * @return
     */
    @Deprecated
    @RequestMapping(
            value = {
                    "companies/{companyId}/stores/{storeId}/sites/deleteCateDeptBrandMappingDetails/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/deleteCateDeptBrandMappingDetails/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/{locationName}/deleteCateDeptBrandMappingDetails/"},
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<StatusMessage> deleteCateDeptBrandMappingDetails(@PathVariable(name = "companyId", required = true) String companyId,
                                                                           @PathVariable(name = "storeId", required = true) String storeId,
                                                                           @PathVariable(value = "siteName", required = false) String siteName,
                                                                           @PathVariable(value = "locationName", required = false) String locationName,
                                                                           @RequestHeader(value = "platform", defaultValue = "android") String platform
    ) {
        final String userId = logUserInformation();
        logger.debug("Controller received deleteCateDeptBrandMappingDetails for user  : {}", userId);

        final StatusMessage sl = configurationService.deleteStoreLocationDetails(companyId, storeId, siteName, locationName, platform);

        final ResponseEntity<StatusMessage> responseEntity = new ResponseEntity<>(sl,
                HttpStatus.ACCEPTED);
        logger.debug("Controller response for deleteCateDeptBrandMappingDetails : {} "
                , responseEntity);
        return responseEntity;
    }


    ///////////////////////////////

    /**
     * Saves the store location to db
     *
     * @param
     * @return
     */
    @Deprecated
    @RequestMapping(
            value = {
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/{locationName}/SaveLocationBayMap/"
            },
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
        public ResponseEntity<StatusMessage> saveLocationBayMap(@PathVariable(name = "companyId", required = true) String companyId,
                                                            @PathVariable(name = "storeId", required = true) String storeId,
                                                            @PathVariable(name = "siteName", required = true) String siteName,
                                                            @PathVariable(name = "locationName", required = true) String locationName,
                                                            @RequestBody SaveLocationBaymapRequest saveLocationBaymapRequest,
                                                            @RequestHeader(value = "platform", defaultValue = "android") String platform) {

        final String userId = logUserInformation();
        logger.debug("Controller received saveLocationBayMap for user : {}", userId);

        Set<String> bayList = null;
        StatusMessage statusMessage = null;
        final Gson gson = new Gson();
        final Type type = new TypeToken<Set<String>>() {
        }.getType();
        try {
            if(saveLocationBaymapRequest != null){
                bayList = gson.fromJson(saveLocationBaymapRequest.getLocationbaymapping(), type);
                statusMessage = configurationService.saveLocationBayMap(companyId, storeId, siteName, locationName, bayList, platform, saveLocationBaymapRequest.isOverrideRequired());
            }
            else {
                statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, "Body is null");
            }
        } catch (Exception e) {
            logger.debug("Exception in saveLocationBayMap mrthod :  {}", e);
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, "Some error occurred");
        }

        final ResponseEntity<StatusMessage> responseEntity = new ResponseEntity<>(statusMessage,
                HttpStatus.ACCEPTED);
        logger.debug("Controller response for saveLocationBayMap : {} "
                , responseEntity);
        return responseEntity;
    }

    /**
     * fetches the detail for store and location
     *
     * @return
     */
    @Deprecated
    @RequestMapping(
            value = {
                    "companies/{companyId}/stores/{storeId}/sites/getLocationBayMap/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/getLocationBayMap/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/{locationName}/getLocationBayMap/"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<BaymapDetail>> getLocationBayMap(@PathVariable(name = "companyId", required = true) String companyId,
                                                                @PathVariable(name = "storeId", required = true) String storeId,
                                                                @PathVariable(value = "siteName", required = false) String siteName,
                                                                @PathVariable(value = "locationName", required = false) String locationName,
                                                                @RequestHeader(value = "platform", defaultValue = "android") String platform) {
        final String userId = logUserInformation();
        logger.debug("Controller received getLocationBayMap for user : {} ", userId);

        final List<BaymapDetail> sl = configurationService.getLocationBayMap(companyId, storeId, siteName, locationName, platform);

        final ResponseEntity<List<BaymapDetail>> responseEntity = new ResponseEntity<>(sl,
                HttpStatus.ACCEPTED);
        logger.debug("Controller response for getLocationBayMap : {} "
                , responseEntity);
        return responseEntity;
    }


    /**
     * fetches the detail for store and location
     *
     * @return
     */
    @Deprecated
    @RequestMapping(
            value = {
                    "companies/{companyId}/stores/{storeId}/sites/deleteLocationBayMap/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/deleteLocationBayMap/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/{locationName}/deleteLocationBayMap/"},
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<StatusMessage> deleteLocationBayMap(@PathVariable(name = "companyId", required = true) String companyId,
                                                              @PathVariable(name = "storeId", required = true) String storeId,
                                                              @PathVariable(value = "siteName", required = false) String siteName,
                                                              @PathVariable(value = "locationName", required = false) String locationName,
                                                              @RequestHeader(value = "platform", defaultValue = "android") String platform) {
        final String userId = logUserInformation();
        logger.debug("Controller received deleteLocationBayMap for user : {} ", userId);

        final StatusMessage sl = configurationService.deleteLocationBayMap(companyId, storeId, siteName, locationName, platform);

        final ResponseEntity<StatusMessage> responseEntity = new ResponseEntity<>(sl,
                HttpStatus.ACCEPTED);
        logger.debug("Controller response for deleteLocationBayMap : {} "
                , responseEntity);
        return responseEntity;
    }


    /**
     * Saves the bearing configuration
     */

    @RequestMapping(value = {
            "companies/{companyId}/stores/{storeId}/sites/{siteName}/SaveSiteConfiguration/"},
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<StatusMessage> ssc(@PathVariable(value = "companyId", required = true) String companyId,
                                             @PathVariable(value = "storeId", required = true) String storeId,
                                             @PathVariable(value = "siteName", required = true) String siteName,
                                             @RequestBody Map<String, String> map,
                                             @RequestHeader(value = "platform", defaultValue = "android") String platform) {
        logger.debug("Controller received getBearingConfiguration.");

        final StatusMessage sm = configurationService.saveBearingConfiguration(companyId, storeId, siteName, map, platform);

        final ResponseEntity<StatusMessage> responseEntity = new ResponseEntity<>(sm,
                HttpStatus.ACCEPTED);
        logger.debug("Controller response for SaveSiteConfiguration : {} "
                , responseEntity);
        return responseEntity;
    }

    /**
     * fetches the bearing configuration
     */
    @RequestMapping(value = {
            "companies/{companyId}/stores/{storeId}/sites/getSiteConfiguration/",
            "companies/{companyId}/stores/{storeId}/sites/{siteName}/getSiteConfiguration/"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ConfigModel>> gsc(@PathVariable(value = "companyId", required = true) String companyId,
                                                 @PathVariable(value = "storeId", required = true) String storeId,
                                                 @PathVariable(name = "siteName", required = false) String siteName,
                                                 @RequestHeader(value = "platform", defaultValue = "android") String platform) {
        logger.debug("Controller received getSiteConfiguration.");

        final List<ConfigModel> bc = configurationService.getBearingConfiguration(companyId, storeId, siteName, platform);

        final ResponseEntity<List<ConfigModel>> responseEntity = new ResponseEntity<>(bc, HttpStatus.ACCEPTED);
        logger.debug("Controller response for getBearingConfiguration : {} "
                , responseEntity);
        return responseEntity;
    }

    @Deprecated
    @RequestMapping(value = {
            "companies/{companyId}/stores/{storeId}/sites/deleteSiteConfiguration/",
            "companies/{companyId}/stores/{storeId}/sites/{siteName}/deleteSiteConfiguration/"},
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<StatusMessage> dsc(@PathVariable(value = "companyId", required = true) String companyId,
                                             @PathVariable(value = "storeId", required = true) String storeId,
                                             @PathVariable(name = "siteName", required = false) String siteName,
                                             @RequestHeader(value = "platform", defaultValue = "android") String platform) {
        logger.debug("Controller received getBearingConfiguration.");

        final StatusMessage bc = configurationService.deleteBearingConfiguration(companyId, storeId, siteName, platform);

        final ResponseEntity<StatusMessage> responseEntity = new ResponseEntity<>(bc, HttpStatus.ACCEPTED);
        logger.debug("Controller response for getBearingConfiguration : {} "
                , responseEntity);
        return responseEntity;
    }


    /**
     * Saves the store location to  hierarchy mapping in db
     *
     * @param
     * @return
     */
    @RequestMapping(
            value = {
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/{locationName}/saveOrUpdateHierarchyMappingDetails/"
            },
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<StatusMessage> saveOrUpdateCateDeptBrandMappingDetails(@PathVariable(name = "companyId", required = true) String companyId,
                                                                                 @PathVariable(name = "storeId", required = true) String storeId,
                                                                                 @PathVariable(name = "siteName", required = true) String siteName,
                                                                                 @PathVariable(name = "locationName", required = true) String locationName,
                                                                                 @RequestBody List<HierarchyDetail> hierarchies,
                                                                                 @RequestHeader(value = "platform", defaultValue = "android") String platform) {

        try {
            final String userId = logUserInformation();
            logger.debug("Controller received for userId : {} , SaveOrUpdateHierarchyMappingDetails  {}.", userId, hierarchies);

            StatusMessage sm = configurationService.saveOrUpdateHierarchyMappingDetails(companyId.trim(), storeId.trim(), siteName, locationName, hierarchies, platform);
            final ResponseEntity<StatusMessage> responseEntity = new ResponseEntity<>(sm, HttpStatus.ACCEPTED);

            logger.debug("Controller response for SaveOrUpdateHierarchyMappingDetails : {} ", responseEntity);

            return responseEntity;
        } catch (Exception e) {
            logger.debug("Exception in saving Hierarchy Mapping Details : {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * fetches the detail for store and location
     *
     * @return
     */

    @RequestMapping(
            value = {
                    "companies/{companyId}/stores/{storeId}/sites/siteLocationHierarchyMapping/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/siteLocationHierarchyMapping/",
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/{locationName}/siteLocationHierarchyMapping/"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SiteLocationHierarchyDetail>> getSiteLocationHierarchyMapping(@PathVariable(name = "companyId") String companyId,
                                                                                             @PathVariable(name = "storeId") String storeId,
                                                                                             @PathVariable(value = "siteName", required = false) String siteName,
                                                                                             @PathVariable(value = "locationName", required = false) String locationName,
                                                                                             @RequestHeader(value = "platform", defaultValue = "android") String platform) {
        try {
            final String userId = logUserInformation();
            logger.debug("Controller received siteLocationHierarchyMapping for user : {} ", userId);

            final List<SiteLocationHierarchyDetail> sl = configurationService.getSiteLocationHierarchyMapping(companyId.trim(), storeId.trim(), siteName, locationName, platform);

            final ResponseEntity<List<SiteLocationHierarchyDetail>> responseEntity = new ResponseEntity<>(sl,
                    HttpStatus.ACCEPTED);
            logger.debug("Controller response for siteLocationHierarchyMapping : {} "
                    , responseEntity);
            return responseEntity;
        } catch (Exception e) {
            logger.debug("Exception while fetching Site Location Hierarchy Mapping : {} ", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
