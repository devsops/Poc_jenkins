package com.bosch.pai.retail.configuration.controllers;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.configuration.service.ConfigurationService;
import com.bosch.pai.retail.configmodel.HierarchyDetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ Created by chu7kor on 12/15/2017.
 */

@EnableAutoConfiguration
@RestController("StoreDetailsController")
public class StoreDetailsController {

    private final Logger logger = LoggerFactory.getLogger(StoreDetailsController.class);

    @Autowired
    private ConfigurationService configurationService;

    @Deprecated
    @RequestMapping(value = {"companies/{companyId}/stores/{storeId}/getAllCategory/",
            "companies/{companyId}/stores/{storeId}/category/{categoryId:.+}/getDepartmentForCategory/",
            "companies/{companyId}/stores/{storeId}/category/{categoryId:.+}/department/{departmentId:.+}/getBrandForDepartment/"

    }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List>  gSD(@PathVariable(name = "companyId")String companyId,
                                                      @PathVariable(name = "storeId")String storeId,
                                                      @PathVariable(name = "departmentId" , required = false)String departmentId,
                                                      @PathVariable(name = "categoryId" , required = false)String categoryId,
                                                      @PathVariable(name = "brandId" , required = false)String brandId
                                                      ){
        logger.debug("Controller received getStoreInfo for User : {}.", ContextHolder.getContext().getUserId());
        List s1 = configurationService.getStoreInfo(companyId, storeId, departmentId, categoryId, brandId);
        final ResponseEntity<List> responseEntity = new ResponseEntity<>(s1,
                HttpStatus.OK);
        logger.debug("Controller response for getStoreLocationDetail for User {} : {}", ContextHolder.getContext().getUserId()
                , responseEntity);
        return responseEntity;
    }

    @Deprecated
    @RequestMapping(value = {"companies/{companyId}/stores/{storeId}/categories/"},
            method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<String>>  gAC(@PathVariable(name = "companyId")String companyId,
                                     @PathVariable(name = "storeId")String storeId
    ){
        logger.debug("Controller received getStoreInfo for User : {}.", ContextHolder.getContext().getUserId());
        List<String> s1 = configurationService.getCategory(companyId, storeId);
        final ResponseEntity<List<String>> responseEntity = new ResponseEntity<>(s1,
                HttpStatus.OK);
        logger.debug("Controller response for getStoreInfo for User {} : {}", ContextHolder.getContext().getUserId()
                , responseEntity);
        return responseEntity;
    }

    @RequestMapping(path = {"companies/{companyId}/stores/{storeId}/hierarchy"},method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<List<HierarchyDetail>>  getStoreHierarchy(@PathVariable(name = "companyId")String companyId,
                                                                    @PathVariable(name = "storeId")String storeId,
                                                                    @RequestHeader(value = "platform", defaultValue = "android") String platform
    ){
        try {

            logger.debug("Controller received getHierarchy for User : {}.", ContextHolder.getContext().getUserId());
            List<HierarchyDetail> s1 = configurationService.getHierarchies(companyId.trim(), storeId.trim(),platform);
            final ResponseEntity<List<HierarchyDetail>> responseEntity = new ResponseEntity<>(s1,
                    HttpStatus.OK);
            logger.debug("Controller response for getStoreHierarchy for User {} : {}", ContextHolder.getContext().getUserId()
                    , responseEntity);
            return responseEntity;
        }catch (Exception e){
            logger.debug("Failed in getting hierarchy details : {}",e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = {"companies/{companyId}/stores/{storeId}/hierarchy"},method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<StatusMessage>  saveStoreHierarchy(@PathVariable(name = "companyId")String companyId,
                                                             @PathVariable(name = "storeId")String storeId,
                                                             @RequestBody List<HierarchyDetail> hierarchyDetails,
                                                             @RequestHeader(value = "platform", defaultValue = "android") String platform
    ){
        try {
            logger.debug("Controller received getHierarchy for User : {}.", ContextHolder.getContext().getUserId());
            StatusMessage s1 = configurationService.saveHierarchies(companyId.trim(), storeId.trim(), hierarchyDetails,platform);
            final ResponseEntity<StatusMessage> responseEntity = new ResponseEntity<>(s1,
                    HttpStatus.OK);
            logger.debug("Controller response for saveStoreHierarchy for User {} : {}", ContextHolder.getContext().getUserId()
                    , responseEntity);
            return responseEntity;
        }catch (Exception e){
            logger.debug("Exception in saving hierarchy Details : {} ",e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


/*
    @RequestMapping(value = {
            "companies/{companyId}/getStoreConfiguration/",
            "companies/{companyId}/stores/{storeId}/getStoreConfiguration/",
            "companies/{companyId}/stores/{storeId}/sites/{siteName}/getStoreConfiguration/"

    },method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<StoreConfigResponse> gsC(@PathVariable(name = "companyId")String companyId,
                                                   @PathVariable(name = "storeId" , required = false)String storeId,
                                                   @PathVariable(name = "siteName",required = false)String siteName
                    ){
        logger.debug("Controller received getStoreConfiguration");
        StoreConfigResponse sc = configurationService.getStoreConfiguration(companyId,storeId,siteName);
        final ResponseEntity<StoreConfigResponse> storeConfigResponse = new ResponseEntity<>(sc,HttpStatus.OK);
        logger.debug("Sending getStoreConfiguration Response ");
        return  storeConfigResponse;
    }

    @RequestMapping(value = {
            "companies/{companyId}/saveStoreConfiguration/"
    },method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<StatusMessage> sSC(@PathVariable(name = "companyId")String companyId,
                                                   @RequestBody StoreConfig storeConfig){
        logger.debug("Controller received getStoreConfiguration");
        StatusMessage sc = configurationService.saveStoreConfiguration(companyId,storeConfig);
        final ResponseEntity<StatusMessage> storeConfigResponse = new ResponseEntity<>(sc,HttpStatus.OK);
        logger.debug("Sending saveStoreConfiguration Response ");
        return  storeConfigResponse;
    }*/
}
