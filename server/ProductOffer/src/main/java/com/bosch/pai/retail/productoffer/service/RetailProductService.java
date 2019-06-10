package com.bosch.pai.retail.productoffer.service;

import com.bosch.pai.retail.adtuning.model.offer.OfferResponse;
import com.bosch.pai.retail.adtuning.model.offer.UserOfferResponse;
import com.bosch.pai.retail.adtuning.model.offer.UserPromoOfferResponse;
import com.bosch.pai.retail.adtuning.responses.GetOfferResponse;
import com.bosch.pai.retail.adtuning.responses.PromoMapOfferResponse;
import com.bosch.pai.retail.adtuning.responses.PromoOfferResponse;
import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.db.model.OfferResponseDetail;
import com.bosch.pai.retail.productoffer.dao.OfferResponseDetailDAO;
import com.bosch.pai.retail.productoffer.dao.SiteLocationDAO;
import com.bosch.pai.retail.productoffer.dao.ValidPromoDetailDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service("RetailProductService")
public class RetailProductService {

    @Autowired
    private ValidPromoDetailDAO validPromoDetailDAO;

    @Autowired
    private OfferResponseDetailDAO offerResponseDetailDAO;

    @Autowired
    private SiteLocationDAO siteLocationDAO;

    @Autowired
    public RetailProductService() {
        //default constructor
    }

    public GetOfferResponse getPromosForSection(String companyId, String storeId, String siteName, String locationName) {
        return validPromoDetailDAO.getValidPromosForLocation(companyId, storeId, siteName, locationName);
    }

    public PromoMapOfferResponse getPromosForStore(String companyId, String storeId) {
        return validPromoDetailDAO.getValidPromosForStore(companyId, storeId);
    }

    public PromoOfferResponse getAllPromosForStore(String companyId, String storeId,String platform) {
        return validPromoDetailDAO.getAllValidPromosForStore(companyId, storeId,platform);
    }


    public PromoMapOfferResponse getPromosForSite(String companyId, String storeId, String siteId) {
        return validPromoDetailDAO.getValidPromosForSite(companyId, storeId,siteId);
    }

    private String[] getLocationDetails(String promoCode) {
        return promoCode.split("_");
    }

    public StatusMessage saveOfferResponseDetails(
            OfferResponse offerResponse,String storeId,String companyId) {

        final OfferResponseDetail offerResponseDetail = new OfferResponseDetail();
        final String promoCode = offerResponse.getPromoCode();
        if(promoCode != null) {
            final String[] locationDetails = getLocationDetails(promoCode);
            final String siteName = locationDetails[1];
            final String bay = locationDetails[2];
            final String locationName = siteLocationDAO.getLocationNameByLocationCode(companyId, siteName, bay);
            offerResponseDetail.setSiteName(siteName);
            offerResponseDetail.setLocationName(locationName);
        }
        offerResponseDetail.setOfferResponseStatus(offerResponse.getOfferResponseStatus());
        offerResponseDetail.setMessageCode(offerResponse.getPromoCode());
        offerResponseDetail.setUserId(offerResponse.getUserId());
        offerResponseDetail.setOfferActiveDuration(offerResponse.getOfferActiveDuration());
        offerResponseDetail.setUserResponseTimeStamp(new Timestamp(System.currentTimeMillis()));
        return offerResponseDetailDAO.addCustomerAcceptedOfferDetail(companyId, storeId, offerResponseDetail);
    }

    public StatusMessage saveCompleteOfferResponse(List<UserOfferResponse> offerResponseList, String companyId,String platform){
        return offerResponseDetailDAO.addCompleteCustomerAcceptedOfferDetail(companyId,offerResponseList,platform);
    }

    public StatusMessage saveCompletePromoOfferResponse(List<UserPromoOfferResponse> offerResponseList, String companyId, String platform) {
        return offerResponseDetailDAO.addPromoOfferResponse(companyId,offerResponseList,platform);
    }
}

