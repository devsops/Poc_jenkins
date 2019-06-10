package com.bosch.pai.retail.configuration.dao;


import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.configmodel.DemoAdConfiguration;
import com.bosch.pai.retail.configuration.Exception.DemoConfigExceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository
public class DemoConfigDao {
    private final MongoOperations mongoOperations;
    private Logger logger = LoggerFactory.getLogger(DemoConfigDao.class);
    private static final String COLLECTION_NAME = "AdConfiguration";
    private static final String IS_LATEST = "isLatest";
    @Autowired
    public DemoConfigDao(MongoOperations mongoOperations){
        this.mongoOperations = mongoOperations;
    }

    public DemoAdConfiguration getSiteConfig(String companyName, String storeName, String siteName){
        Query query = new Query();

        query.addCriteria(Criteria.where("siteName").is(siteName)
                .and("companyName").is(companyName)
                .and("storeName").is(storeName)
                .and(IS_LATEST).is(true));
        logger.debug("Received getSiteConfig for USER {}",ContextHolder.getContext().getUserId());
        return mongoOperations.findOne(query, DemoAdConfiguration.class,getCollectionName(companyName));
    }

    public int addOrUpdateConfig(DemoAdConfiguration configuration) throws DemoConfigExceptions {
        String siteName = configuration.getSiteName();
        String companyName = configuration.getCompanyName();
        String storeName = configuration.getStoreName();
        logger.debug("Received addOrUpdateConfig for USER {} : {}", ContextHolder.getContext().getUserId(),configuration);
        checkIfValidInputs(siteName, companyName, storeName);

        DemoAdConfiguration config = getSiteConfig(companyName, storeName, siteName);

        configuration.setLatest(true);
        if(config == null){
            mongoOperations.insert(configuration,getCollectionName(companyName));
            return 1;
        }else {
            Query query = new Query();
            query.addCriteria(Criteria.where("siteName").is(siteName)
                    .and("companyName").is(companyName)
                    .and("storeName").is(storeName)
                    .and(IS_LATEST).is(true));

            Update update = new Update();
            update.set("endTime", Calendar.getInstance().getTimeInMillis());
            update.set(IS_LATEST, false);

            mongoOperations.findAndModify(query, update, DemoAdConfiguration.class,getCollectionName(companyName));

            mongoOperations.insert(configuration,getCollectionName(companyName));
            return 2;
        }
    }

    private void checkIfValidInputs(String siteName, String companyName, String storeName) throws DemoConfigExceptions {
        if(siteName == null || siteName.isEmpty() || companyName == null
                || companyName.isEmpty() || storeName == null || storeName.isEmpty()){
            throw new DemoConfigExceptions("mandatory fields missing: companyName=>"+companyName+", storeName=>"+storeName+", siteName=>"+siteName);
        }
    }

    private String getCollectionName(String companyId){
        return  (companyId +"_"+ COLLECTION_NAME).toUpperCase();
    }
}
