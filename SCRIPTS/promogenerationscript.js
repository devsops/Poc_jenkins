/* #1 Clear valid promo details  */
db.getCollection('SSL_valid_promo_details').remove({});
/* #2 Convert Store ID to string */
db.getCollection('SSL_test_promo_details').find().snapshot().forEach(function(doc){
    doc.Store_ID = doc.Store_ID.toString();
      db.SSL_test_promo_details.save(doc)})
/* #3 Convert MEN�S FORMAL to MENS FORMAL and 
      MEN�S LEATHER and luggage to MENS LEATHER and luggage */    
db.getCollection('SSL_test_promo_details').updateMany({Promo_Item_Category_ID:/MEN�S LEATHER and luggage/},{$set:{Promo_Item_Category_ID:"MENS LEATHER and luggage"}})
db.getCollection('SSL_test_promo_details').updateMany({Promo_Item_Category_ID:/MEN�S FORMAL/},{$set:{Promo_Item_Category_ID:"MENS FORMAL"}})
/* #4 Fetch Promo_Department_Name */
db.getCollection('SSL_test_promo_details').aggregate([{
	$match: {
		"Promo_Status": "Active"
	}
},
{
	$lookup: {
		from: "SSL_STORE_DETAILS",
		let: {
			store_ID: "$Store_ID",
			promo_On: "$Promo_On",
			promo_Item_Department_ID: "$Promo_Item_Department_ID",
			promo_Item_Category_ID: "$Promo_Item_Category_ID",
			promo_Item_Brand_ID: "$Promo_Item_Brand_ID"
		},
		pipeline: [{
                    
                    $match: {
				$expr: {
                                    $and: [{
						$eq: [0,{$strcasecmp:["$Department_Code",
						"$$promo_Item_Department_ID"]}]
					},
					
						{$eq: [0,{$strcasecmp:["$Category_Code",
						"$$promo_Item_Category_ID"]}]
                                                }
                                                ]
					}
                                    }
                    }],
		as: "locdata"
                    
                }
                
                
},
{
    $addFields:{

    Promo_Item_Department_Name:
               {
                 $cond: [ {$and : [ { $ne: [ "$locdata", null] },
                                    { $gt: [ { $size: "$locdata" },0] }
                                  ] },
                            "$locdata.Department_Name",
                            "NA"
                         ]
               }
           }
},
{
	$unwind: "$Promo_Item_Department_Name"
},
{
	$project: {
		locdata: 0
	}
},
{
    $group: {
	_id: { Promo_Code:"$Promo_Code", 
            Store_ID:"$Store_ID",
            Store_Description:"$Store_Description",
            Promo_On:"$Promo_On",
            Promo_Start_Date:"$Promo_Start_Date",
            Promo_End_Date : "$Promo_End_Date",
            Promo_Status : "$Promo_Status",
            Promo_Rank : "$Promo_Rank",
            image_URL : "$image_URL",
            Promo_Description:"$Promo_Description",
            Promo_Item_Category_ID:"$Promo_Item_Category_ID",
            Promo_Item_Brand_ID:"$Promo_Item_Brand_ID",
            Promo_Item_Department_ID:"$Promo_Item_Department_ID",
            Promo_Item_Department_Name:"$Promo_Item_Department_Name"
        }
	}
        
},
{
    $project:{
        _id:0,
        Promo_Code:"$_id.Promo_Code", 
            Store_ID:"$_id.Store_ID",
            Store_Description:"$_id.Store_Description",
            Promo_On:"$_id.Promo_On",
            Promo_Start_Date:"$_id.Promo_Start_Date",
            Promo_End_Date : "$_id.Promo_End_Date",
            Promo_Status : "$_id.Promo_Status",
            Promo_Rank : "$_id.Promo_Rank",
            image_URL : "$_id.image_URL",
            Promo_Description:"$_id.Promo_Description",
            Promo_Item_Category_ID:"$_id.Promo_Item_Category_ID",
            Promo_Item_Brand_ID:"$_id.Promo_Item_Brand_ID",
            Promo_Item_Department_ID:"$_id.Promo_Item_Department_ID",
            Promo_Item_Department_Name:"$_id.Promo_Item_Department_Name"
        
        
        }
    
},
{
    $out:"SSL_test_promo_details"
}
])
/* #5 Add locations for promo */ 
db.SSL_LOCATION_BAY_MAP.aggregate([
{
	$addFields: {
		locationCategorys: {
			$cond: [{
				$and: [{
					$ne: ["$locationCategorys",
					null]
				},
				{
					$gt: [{
						$size: "$locationCategorys"
					},
					0]
				}]
			},
			"$locationCategorys",
			"NA"
			]
		},
		locationDepartments: {
			$cond: [{
				$and: [{
					$ne: ["$locationDepartments",
					null]
				},
				{
					$gt: [{
						$size: "$locationDepartments"
					},
					0]
				}]
			},
			"$locationDepartments",
			"NA"]
		},
		locationBrands: {
			$cond: [{
				$and: [{
					$ne: ["$locationBrands",
					null]
				},
				{
					$gt: [{
						$size: "$locationBrands"
					},
					0]
				}]
			},
			"$locationBrands",
			
				"NA"
			]
		}
	}
},
{
	$unwind: "$locationCategorys"
},
{
	$unwind: "$locationDepartments"
},
{
	$unwind: "$locationBrands"
},
{
	$lookup: {
		from: "SSL_test_promo_details",
		let: {
            store_ID: "$storeId",
                    locationType:"$locationType",
			category_ID: "$locationCategorys",
			department_ID: "$locationDepartments",
			brand_ID: "$locationBrands"
		},
		pipeline: [{
			$match: {
                              
							
				$expr: { 
                                    $and: [{
                                                $eq: [{ $toLower:"$$store_ID"},
                                                { $toLower:"$Store_ID"}]},
					{$or: [{
						$and: [{
							$eq: [{ $toLower:"Category"},
							{ $toLower:"$Promo_On"}]
						},
						{
							$eq: [{ $toLower:"$$category_ID"},
							{ $toLower:"$Promo_Item_Category_ID"}]
						}]
					},
					{
						$and: [{
							$eq: [{ $toLower:"Department"},
							{ $toLower:"$Promo_On"}]
						},
						{
							$or: [{
								$and: [{
									$eq: [{ $toLower:"CATEGORY"},
									{ $toLower:"$$locationType"}]
								},
								{
									$eq: [{ $toLower:"$$category_ID"},
									{ $toLower:"$Promo_Item_Category_ID"}]
								}]
							},
							{
								$and: [{
									$eq: [{ $toLower:"$Promo_Item_Category_ID"},
									{ $toLower:"$$category_ID"}]
								},
								{
									$eq: [{ $toLower:"$$department_ID"},
									{ $toLower:"$Promo_Item_Department_Name"}]
								}]
							}]
						}]
					},
					{
						$and: [{
							$eq: [{ $toLower:"Brand"},
							{ $toLower:"$Promo_On"}]
						},
						{
							$or: [{
								$and: [{
									$eq: [{ $toLower:"CATEGORY"},
									{ $toLower:"$$locationType"}]
								},
								{
									$eq: [{ $toLower:"$$category_ID"},
									{ $toLower:"$Promo_Item_Category_ID"}]
								}]
							},
							{
								$and: [{
									$eq: [{ $toLower:"DEPARTMENT"},
									{ $toLower:"$$locationType"}]
								},
								{
									$eq: [{ $toLower:"$Promo_Item_Category_ID"},
									{ $toLower:"$$category_ID"}]
								},
								{
									$eq: [{ $toLower:"$$department_ID"},
									{ $toLower:"$Promo_Item_Department_Name"}]
								}]
							},
							{
								$and: [{
									$eq: [{ $toLower:"$Promo_Item_Category_ID"},
									{ $toLower:"$$category_ID"}]
								},
								{
									$eq: [{ $toLower:"$$department_ID"},
									{ $toLower:"$Promo_Item_Department_Name"}]
								},
								{
									$eq: [{ $toLower:"$$brand_ID"},
									{ $toLower:"$Promo_Item_Brand_ID"}]
								}]
							}]
						}]
					}]
                                    }]
				}
			}
                    }


		],
		as: "promos"
	}
},
{
    $match: {
            $expr :{
                $and: [{
					$ne: ["$promos",
					null]
				},
				{
					$gt: [{
						$size: "$promos"
					},0]
                                 }
                ]
                                 }
        
        }
},
{
    $group: {
	_id: { 
            Store_ID:"$storeId",
            siteName:"$siteName",
            promos:"$promos",
            locationName:"$locationName"
        }
	}
        
},
{
    $project:{
        _id:0,
        Store_ID:"$_id.Store_ID", 
            siteName:"$_id.siteName",
           locationName:"$_id.locationName",
            promos:"$_id.promos"
         
    }
},
{
	$unwind: "$promos"
},
{
    $addFields :{
        
        
        "Promo_Code" : "$promos.Promo_Code",
        "Store_Description" : "$promos.Store_Description",
        "Promo_On" : "$promos.Promo_On",
        "Promo_Start_Date" : "$promos.Promo_Start_Date",
        "Promo_End_Date" : "$promos.Promo_End_Date",
        "Promo_Status" : "$promos.Promo_Status",
        "Promo_Rank" : "$promos.Promo_Rank",
        "image_URL" : "$promos.image_URL",
        "Promo_Description" : "$promos.Promo_Description",
        "Promo_Item_Category_ID" : "$promos.Promo_Item_Category_ID",
        "Promo_Item_Brand_ID" : "$promos.Promo_Item_Brand_ID",
        "Promo_Item_Department_ID" : "$promos.Promo_Item_Department_ID",
        "Promo_Item_Department_Name" : "$promos.Promo_Item_Department_Name"
        
        }
    
},
{
    $project :{
        promos:0
        }
    
},
{
	$group: {
		_id: {
			PROMO_CODE: "$Promo_Code",
			OFFER_DESCRIPTION: "$Promo_Description",
			Promo_On: "$Promo_On",
			PROMO_START_DATE: "$Promo_Start_Date",
			PROMO_END_DATE: "$Promo_End_Date",
			RANK: {
				$cond: [{
					$and: [{
						$ne: ["$Promo_Rank",
						null]
					},
					{
						$ne: ["$Promo_Rank",
						""]
					}]
				},
				"$Promo_Rank",
				NumberInt(1)]
			},
			Store_ID: "$Store_ID",
			Store_Description: "$Store_Description",
			Promo_Item_Brand_ID: "$Promo_Item_Brand_ID",
			Promo_Item_Category_ID: "$Promo_Item_Category_ID",
			Promo_Item_Department_ID: "$Promo_Item_Department_ID",
			Promo_Status: "$Promo_Status",
			siteName: "$siteName",
			image_URL: "$image_URL"
		},
		locations: {
			$addToSet: "$locationName"
		}
	}
},
{
	$project: {
		"_id": 0,
		"PROMO_CODE": "$_id.PROMO_CODE",
		"OFFER_DESCRIPTION": "$_id.OFFER_DESCRIPTION",
		"PROMO_ON": "$_id.Promo_On",
        "STORE_ID":"$_id.Store_ID",
		"PROMO_START_DATE": "$_id.PROMO_START_DATE",
		"PROMO_END_DATE": "$_id.PROMO_END_DATE",
		"RANK": "$_id.RANK",
		"SITE_NAME": "$_id.siteName",
		"LOCATIONS": "$locations",
		"IMAGE_URL": "$_id.image_URL",
		"CUSTOM_DETAIL_MAP": {
			"$arrayToObject": {
				"$map": {
					input: {
						"$objectToArray": {
							"Store_Description": "$_id.Store_Description",
							"Store_Id": "$_id.Store_ID",
							"Promo_Item_Brand_ID": "$_id.Promo_Item_Brand_ID",
							"Promo_Item_Category_ID": "$_id.Promo_Item_Category_ID",
							"Promo_Item_Department_ID": "$_id.Promo_Item_Department_ID",
							"Promo_Status": "$_id.Promo_Status",
							
						}
					},
					as: "field",
					in: ["$$field.k",
					"$$field.v"]
				}
			}
		}
	}
},
{
	"$out": "SSL_valid_promo_details"
}],
{
	explain: false,
	cursor: {
		
	}
});
/* #6 Convert PROMO_START_DATE and PROMO_END_DATE to ISO Date format */
db.getCollection('SSL_valid_promo_details').aggregate([
{
   $addFields: {
      "PROMO_START_DATE": {
         $dateFromString: {
            dateString: '$PROMO_START_DATE',
            timezone: 'UTC'
         }
      },
       "PROMO_END_DATE": {
         $dateFromString: {
            dateString: '$PROMO_END_DATE',
            timezone: '-23:00'
         }
      }
   }
},
{
    $out:"SSL_valid_promo_details"
}
] 
)