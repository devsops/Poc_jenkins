package com.bosch.pai.retail.db.util;


public class DBUtil {

    private DBUtil() {
        //Util class
    }

    public static String getCollectionName(String companyId, String storeId, String collectionName) {
        return companyId + "_" + storeId + "_" + collectionName;
    }

    public static String getCollectionName(String companyId, String collectionName) {
        return companyId + "_" + collectionName;
    }
}
