db.getCollection('SSL_STORE_DETAILS').updateMany({"Category_Code":/MEN�S LEATHER and luggage/},{$set:{"Category_Code":"MENS LEATHER and luggage"}}); 
db.getCollection('SSL_STORE_DETAILS').updateMany({"Category_Code":/MEN�S FORMAL/},{$set:{"Category_Code":"MENS FORMAL"}});
db.getCollection('SSL_LOCATION_BAY_MAP_IOS').updateMany({"siteName":"114_MaladShoppersStop","locationType":"CATEGORY"},{$set:{"locationType":"DEPARTMENT"}}) 
db.getCollection('SSL_LOCATION_BAY_MAP_IOS').updateMany({"locationCategorys":{$all:["MENS FORMAL & LUGGAGE"]}},{$set:{locationCategorys:["MENS FORMAL"]}})
