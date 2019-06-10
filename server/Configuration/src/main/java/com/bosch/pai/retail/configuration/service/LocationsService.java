package com.bosch.pai.retail.configuration.service;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.configmodel.SiteLocations;
import com.bosch.pai.retail.configuration.dao.LocationsDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by sjn8kor on 3/9/2018.
 */

@Service
public class LocationsService {

    @Autowired
    private LocationsDAO ldao;

    @Autowired
    public LocationsService() {
        //default constructor
    }

    public List<SiteLocations> getAllLocations(String companyId, String storeId, String siteName, String platform) {
        return ldao.getAllLocations(companyId, storeId, siteName, platform);
    }

    public StatusMessage saveOrUpdateLocations(String companyId, String storeId, String siteName, Set<String> locations, String platform) {
        if (locations == null || locations.isEmpty()) {
            return new StatusMessage(StatusMessage.STATUS.LOCATION_LIST_SHOULD_NOT_BE_EMPTY, "location list should not be empty");
        }
        return ldao.saveOrUpdateLocations(companyId, storeId, siteName, locations, platform);
    }
}
