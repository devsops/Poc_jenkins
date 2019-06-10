package com.bosch.pai.retail.configuration.controllers;


import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.configmodel.DemoAdConfiguration;
import com.bosch.pai.retail.configuration.dao.DemoConfigDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@RestController
public class DemoConfigController {

    private final DemoConfigDao configDao;
    private Logger logger = LoggerFactory.getLogger(DemoConfigController.class);
    @Autowired
    public DemoConfigController(DemoConfigDao configDao){
        this.configDao = configDao;
    }

    @RequestMapping(value="/add/companies/{companyName}/stores/{storeName}/sites/{siteName}", method = RequestMethod.POST)
    public ResponseEntity<String> addDemoConfiguration(@PathVariable("companyName") String companyName,
                                                       @PathVariable("storeName") String storeName,
                                                       @PathVariable("siteName") String siteName,
                                                       @RequestBody String addConfiguration){
        logger.debug("Controller received addDemoConfiguration for USER {}", ContextHolder.getContext().getUserId());
        final DemoAdConfiguration demoAdConfiguration = new DemoAdConfiguration();
        demoAdConfiguration.setConfiguration(addConfiguration);
        demoAdConfiguration.setSiteName(siteName);
        demoAdConfiguration.setCompanyName(companyName);
        demoAdConfiguration.setStoreName(storeName);

        demoAdConfiguration.setStartTime(Calendar.getInstance().getTimeInMillis());
        demoAdConfiguration.setLatest(true);
        try {
            demoAdConfiguration.setEndTime(new SimpleDateFormat("dd/MM/yyyy").parse("31/12/2999").getTime());
            configDao.addOrUpdateConfig(demoAdConfiguration);
        }catch(Exception e){
            logger.debug("Exception received in addDemoConfiguration for USER {} : {} {}",ContextHolder.getContext().getUserId(),e.getMessage(),e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        logger.debug("Configuration Added Successfully for User {}",ContextHolder.getContext().getUserId());
        return ResponseEntity.ok("Configuration Added Successfully");
    }

    @RequestMapping(value="/read/companies/{companyName}/stores/{storeName}/sites/{siteName}", method = RequestMethod.GET,
            produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> readDemoConfiguration(@PathVariable("companyName") String companyName,
                                                        @PathVariable("storeName") String storeName,
                                                        @PathVariable("siteName") String siteName){
        logger.debug("Controller received readDemoConfiguration for USER {}",ContextHolder.getContext().getUserId());
        final DemoAdConfiguration config = configDao.getSiteConfig(companyName, storeName, siteName);
        if(config !=null) {
            logger.debug("Received readDemoConfiguration for USER {} : {}",ContextHolder.getContext().getUserId(),config.getConfiguration());
            final String configuration = config.getConfiguration();
            return ResponseEntity.ok(configuration);
        }else {
            return ResponseEntity.noContent().build();
        }
    }

}
