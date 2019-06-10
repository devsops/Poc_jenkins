package com.bosch.pai.retail.configuration.service;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.configmodel.BaymapDetail;
import com.bosch.pai.retail.configmodel.ConfigModel;
import com.bosch.pai.retail.configmodel.LocationCateDeptBrand;
import com.bosch.pai.retail.configmodel.SiteLocationDetails;
import com.bosch.pai.retail.configmodel.SiteLocationHierarchyDetail;
import com.bosch.pai.retail.configuration.dao.HierarchyConfigDAO;
import com.bosch.pai.retail.configuration.dao.SiteConfigDAO;
import com.bosch.pai.retail.configuration.dao.SiteLocationDAO;
import com.bosch.pai.retail.configuration.dao.StoreDetailDAO;
import com.bosch.pai.retail.configmodel.HierarchyDetail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ConfigurationService {

    @Autowired
    private SiteLocationDAO sldao;

    @Autowired
    private SiteConfigDAO scdao;

    @Autowired
    private StoreDetailDAO sddao;

    @Autowired
    private HierarchyConfigDAO hierarchyConfigDAO;


    @Autowired
    public ConfigurationService() {
        //default constructor
    }

    public StatusMessage saveOrUpdateCateDeptBrandMappingDetails(String companyId, String storeId, String siteName,
                                                                 String locationName, LocationCateDeptBrand locationDeptCateBrands, String platform, boolean overrideRequired) {

        if (locationDeptCateBrands == null ||
                locationDeptCateBrands.getLocationType() == null ||
                locationDeptCateBrands.getLocationCategorys() == null ||
                locationDeptCateBrands.getLocationCategorys().isEmpty() ||
                locationDeptCateBrands.getLocationCateDeptBrands() == null ||
                locationDeptCateBrands.getLocationCateDeptBrands().isEmpty()) {
            return new StatusMessage(StatusMessage.STATUS.FAILED_TO_SAVE_STORE_LOCATION_MAPPING, "SiteLocationDetails details is not valid.");
        }

        return sldao.saveSiteLocationsDetails(companyId, storeId, siteName, locationName, locationDeptCateBrands, platform, overrideRequired);
    }

    public List<SiteLocationDetails> getCateDeptBrandMappingDetails(String companyId, String storeId, String siteName, String locationName, String platform) {
        return sldao.getSiteLocationDetails(companyId, storeId, siteName, locationName, platform);
    }

    public ResponseEntity getLocationDetail(String companyId, String storeId, String siteName, String locationName,String platform) {

        return sldao.getSiteLocationDetail(companyId, storeId, siteName, locationName,platform);
    }


    public StatusMessage deleteStoreLocationDetails(String companyId, String storeId, String siteName, String locationName, String platform) {
        return sldao.deleteSiteLocationDetails(companyId, storeId, siteName, locationName, platform);
    }

    ////////

    public StatusMessage saveLocationBayMap(String companyId, String storeId, String siteName, String locationName, Set<String> bayList, String platform, boolean override) {
        if (bayList == null || bayList.isEmpty()) {
            return new StatusMessage(StatusMessage.STATUS.FAILED_TO_SAVE_STORE_LOCATION_MAPPING, "BaymapDetail details is not valid.");
        }

        return sldao.saveLocationBayMap(companyId, storeId, siteName, locationName, bayList, platform, override);
    }


    public List<BaymapDetail> getLocationBayMap(String companyId, String storeId, String siteName, String locationName, String platform) {
        return sldao.getLocationBayMap(companyId, storeId, siteName, locationName, platform);
    }


    public StatusMessage deleteLocationBayMap(String companyId, String storeId, String siteName, String locationName, String platform) {
        return sldao.deleteLocationBayMap(companyId, storeId, siteName, locationName, platform);
    }


    public List<ConfigModel> getBearingConfiguration(String companyName, String storeId, String siteName, String platform) {
        return scdao.getSiteConfigDetails(companyName, storeId, siteName,platform);
    }

    public StatusMessage saveBearingConfiguration(String companyName,
                                                  String storeId,
                                                  String siteName,
                                                  Map<String, String> map, String platform) {

        if (map == null || map.isEmpty()) {
            return new StatusMessage(StatusMessage.STATUS.FAILED_TO_SAVE_BEARING_CONFIGURATION, "ConfigModel details is not valid.");
        }

        return scdao.saveSiteConfigDetails(companyName, storeId, siteName, map, platform);
    }

    public StatusMessage deleteBearingConfiguration(String companyId, String storeId, String siteName, String platform) {
        return scdao.deleteSiteConfigDetails(companyId, storeId, siteName,platform);
    }

    public List getStoreInfo(String companyId, String storeId, String deptName, String categoryName, String brandName) {
        return sddao.getStoreDetails(companyId, storeId, deptName, categoryName, brandName);
    }

    public List<String> getCategory(String companyId, String storeId) {
        return sddao.getCategories(companyId, storeId);
    }

    public List<HierarchyDetail> getHierarchies(String companyId, String storeId,String platform) {
        return hierarchyConfigDAO.getHierarchies(companyId, storeId,platform);
    }

    public StatusMessage saveHierarchies(String companyId,String storeId,List<HierarchyDetail> hierarchyDetails,String platform){
        return hierarchyConfigDAO.saveHierarchies(companyId,storeId,hierarchyDetails,platform);
    }

    public StatusMessage saveOrUpdateHierarchyMappingDetails(String companyId, String storeId, String siteName, String locationName, List<HierarchyDetail> hierarchies, String platform) {

        if (hierarchies == null ||
              hierarchies.isEmpty()) {
            return new StatusMessage(StatusMessage.STATUS.FAILED_TO_SAVE_STORE_LOCATION_MAPPING, "Hierarchy mapping details is not valid.");
        }

        return sldao.saveSiteLocationsHierarchyMappingDetails(companyId, storeId, siteName, locationName, hierarchies, platform);

    }

    public List<SiteLocationHierarchyDetail> getSiteLocationHierarchyMapping(String companyId, String storeId, String siteName, String locationName, String platform) {
        return sldao.getSiteLocationHierarchyMapping(companyId, storeId, siteName, locationName, platform);
    }
}

