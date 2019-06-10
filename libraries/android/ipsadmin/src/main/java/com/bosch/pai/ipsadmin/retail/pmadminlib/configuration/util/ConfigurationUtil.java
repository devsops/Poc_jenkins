package com.bosch.pai.ipsadmin.retail.pmadminlib.configuration.util;

import android.util.Log;

import java.net.URLEncoder;

public class ConfigurationUtil {

    private static final String LOG_TAG = ConfigurationUtil.class.getSimpleName();

    private static final String COMPANIES = "companies/";
    private static final String STORES = "/stores/";
    private static final String SITES = "/sites/";
    private static final String LOCATIONS = "/locations/";

    private static final String CATEGORY = "/category/";

    private ConfigurationUtil() {
        //default
    }

    public static String getCategoriesForBayMapUrl(String company, String storeId) {
        if (company != null && storeId != null) {
            return COMPANIES + company + STORES + storeId + "/categories/";
        }
        throw new NullPointerException("COMPANYID and STOREID SHOULD NOT BE NULL");
    }

    public static String getCateDeptBrandEndpoint(String companyId, String storeID, String category, String department) {
        String cate = null;
        if (category != null) {
            try {
                cate = URLEncoder.encode(category, "UTF-8").replace("+", "%20");
            } catch (Exception e) {
                cate = category;
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        }

        String dept = null;
        if (department != null) {
            try {
                dept = URLEncoder.encode(department, "UTF-8").replace("+", "%20");
            } catch (Exception e) {
                dept = department;
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        }

        String ssDeptCateBrandUrl = COMPANIES + companyId + STORES + storeID;
        final String categoryEndPoint = "/getAllCategory/";
        final String deptEndPoint = "/getDepartmentForCategory/";
        final String brandEndPoint = "/getBrandForDepartment/";
        if (cate != null && !cate.isEmpty()) {
            if (dept != null && !dept.isEmpty()) {
                ssDeptCateBrandUrl += CATEGORY + cate + "/department/" + dept + brandEndPoint;
            } else {
                ssDeptCateBrandUrl += CATEGORY + cate + deptEndPoint;
            }
        } else {
            ssDeptCateBrandUrl += categoryEndPoint;
        }
        return ssDeptCateBrandUrl;
    }

    public static String getSaveLocationBayMapEndpoint(String companyId, String storeId, String site, String location) {
        return COMPANIES + companyId + STORES + storeId + SITES + site + LOCATIONS + location + "/SaveOrUpdateCateDeptBrandMappingDetails/";
    }

    public static String getLocationBayMapEndpoint(String companyId,
                                                   String storeId,
                                                   String site,
                                                   String location) {
        if (companyId != null && storeId != null) {
            final String getLocationbaymap = "/getCateDeptBrandMappingDetails/";
            if (site == null && location == null) {
                return COMPANIES + companyId + STORES + storeId + "/sites" + getLocationbaymap;
            } else if (site != null && !site.isEmpty() && location == null) {
                return COMPANIES + companyId + STORES + storeId + SITES + site + "/locations" + getLocationbaymap;
            } else if (site != null && !site.isEmpty() && location != null && !location.isEmpty()) {
                return COMPANIES + companyId + STORES + storeId + SITES + site + LOCATIONS + location + getLocationbaymap;
            }
        }
        return null;

    }

    public static String getSiteConfigurationEndpoint(String companyId, String storeId, String site) {
        if (companyId != null && storeId != null) {
            final String getSiteConfiguration = "/getSiteConfiguration/";
            if (site != null && !site.isEmpty()) {
                return COMPANIES + companyId + STORES + storeId + SITES + site + getSiteConfiguration;
            } else {
                return COMPANIES + companyId + STORES + storeId + "/sites" + getSiteConfiguration;
            }
        }
        return null;
    }

    public static String saveSiteConfigurationEndpoint(String companyId, String storeId, String siteName) {
        if (companyId != null && storeId != null) {
            return COMPANIES + companyId + STORES + storeId + SITES + siteName + "/SaveSiteConfiguration/";
        }
        return null;
    }

    public static String getStoreConfigurationEndpoint(String company, String store, String siteName) {
        if (company != null) {
            final String getStoreConfiguration = "/getStoreConfiguration/";
            if (store != null && siteName != null) {
                return COMPANIES + company + STORES + store + SITES + siteName + getStoreConfiguration;
            }
            return COMPANIES + company + getStoreConfiguration;
        }
        return null;
    }

    public static String getSaveStoreConfigurationEndpoint(String company) {
        if (company != null)
            return COMPANIES + company + "/saveStoreConfiguration/";
        return null;
    }

    public static String getSaveStoreLocationsEndpoint(String companyId, String storeId, String siteName) {
        if (companyId != null && storeId != null) {
            return COMPANIES + companyId + STORES + storeId + SITES + siteName + "/SaveOrUpdateLocations/";
        }
        return null;
    }


    public static String getLocationBayMapUrl(String companyId, String storeId, String site, String location) {
        if (companyId != null || storeId != null) {
            final String getLocationBaymap = "getLocationBayMap/";
            if (site == null && location == null) {
                return COMPANIES + companyId + STORES + storeId + SITES + getLocationBaymap;
            } else if (site != null && !site.isEmpty() && location == null) {
                return COMPANIES + companyId + STORES + storeId + SITES + site + LOCATIONS + getLocationBaymap;
            } else if (site != null && !site.isEmpty() && location != null && !location.isEmpty()) {
                return COMPANIES + companyId + STORES + storeId + SITES + site + LOCATIONS + location + "/" + getLocationBaymap;
            }
        }
        return null;
    }

    public static String saveLocationBayMapUrl(String companyId, String storeId, String site, String location) {

        if (companyId != null || storeId != null && site != null && location != null) {
            return COMPANIES + companyId + STORES + storeId + SITES + site + LOCATIONS + location + "/SaveLocationBayMap/";
        }
        return null;

    }

    public static String saveTuningConfigurationUrl(String company, String store, String siteName) {
        return "add/companies/" + company + "/stores/" + store + "/sites/" + siteName;
    }

    public static String getTuningConfigurationUrl(String company, String store, String siteName) {
        return "read/companies/" + company + "/stores/" + store + "/sites/" + siteName;
    }
}
