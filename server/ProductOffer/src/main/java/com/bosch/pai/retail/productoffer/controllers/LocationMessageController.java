package com.bosch.pai.retail.productoffer.controllers;

import com.bosch.pai.retail.adtuning.model.offer.UserOfferResponse;
import com.bosch.pai.retail.adtuning.model.offer.UserPromoOfferResponse;
import com.bosch.pai.retail.adtuning.responses.PromoMapOfferResponse;
import com.bosch.pai.retail.adtuning.responses.PromoOfferResponse;
import com.bosch.pai.retail.common.DEVICE_TYPE;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.productoffer.service.RetailProductService;
import com.bosch.pai.retail.adtuning.responses.GetOfferResponse;
import com.bosch.pai.retail.common.responses.StatusMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@EnableAutoConfiguration
@RestController("LocationMessageController")
public class LocationMessageController {


    private static String[] exLoc = new String[]{"CHECKOUT", "EXIT", "BILLING"};
    private final Logger logger = LoggerFactory
            .getLogger(LocationMessageController.class);
    @Autowired
    private RetailProductService rps;

    /**
     * Provides location specific offers based on the category of the user
     *
     * @return getOfferResponse
     */
    @Deprecated
    @RequestMapping(
            value = {
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/{locationName}/offers"/*,
                    //Commenting this as implementation is not there now - Anju
                    "companies/{companyId}/stores/{storeId}/sites/{siteName}/locations/offers"*/},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<GetOfferResponse> getUSPOFF(
            @PathVariable("companyId") String ci,
            @PathVariable("storeId") String sti,
            @PathVariable("siteName") String sn,
            @PathVariable(name = "locationName") String ln) {
        logger.debug("Controller received getOfferRequest for location for USER {} : companyId : {}, storeId: {}, siteName: {}, locationName: {} ", ContextHolder.getContext().getUserId(), ci, sti, sn, ln);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        GetOfferResponse gor;
        if (sn != null && !sn.isEmpty() && ln != null && !ln.isEmpty()) {
            final boolean isEX2 = val1ExlInL(ln);
            if (!isEX2) {

                gor = rps.getPromosForSection(ci, sti, sn, ln);

            } else {
                gor = new GetOfferResponse();
                gor.setStatusMessage(new StatusMessage(StatusMessage.STATUS.FAILURE, "No Offers for exit locations"));
            }

        } else if (ln == null || ln.isEmpty()) {
            gor = rps.getPromosForSection(ci, sti, sn, ln);
        } else {
            gor = new GetOfferResponse();
            gor.setStatusMessage(new StatusMessage(StatusMessage.STATUS.FAILURE, "Site, Location is not valid."));
        }
        final ResponseEntity<GetOfferResponse> getOfferResponseResponseEntity = new ResponseEntity<>(gor, headers,
                HttpStatus.ACCEPTED);
        logger.debug("Controller response for getOffers(location) for USER {} : {} "
                ,ContextHolder.getContext().getUserId(), getOfferResponseResponseEntity);
        return getOfferResponseResponseEntity;
    }

    private boolean val1ExlInL(String l) {
        return Arrays.asList(exLoc).contains(l.toUpperCase());
    }

    /**
     * Saves the user response in server for offer
     *
     * @param offerResponse
     * @return responseStatus
     */
    //Commenting this api as it is not working. PromoCode -> locationName is not present - Anju
   /* @RequestMapping(
            value = "companies/{companyId}/stores/{storeId}/offers/response",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<StatusMessage> adCusOR1(
            @PathVariable("companyId") String companyId,
            @PathVariable("storeId") String storeId,
            @RequestBody OfferResponse offerResponse) {
        logger.debug("Controller received add customer offer response request : {} "
                , offerResponse);

        StatusMessage statusMessage = rps.saveOfferResponseDetails(offerResponse, storeId, companyId);

        logger.debug("Controller response for add customer offer response request : {} "
                , statusMessage);
        return new ResponseEntity<>(statusMessage,
                HttpStatus.ACCEPTED);
    }*/

   // Offer response upload without hierarchy mapping
    @RequestMapping(
            value = "companies/{companyId}/offers/completeOfferResponse/",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<StatusMessage> adCCusOR(
            @PathVariable("companyId") String companyId,
            @RequestBody List<UserOfferResponse> offerResponseList,
            @RequestHeader(value = "platform" ,defaultValue = "android") String platform
            ) {
        logger.debug("Controller received add complete customer offer response request for USER {} : {} "
                ,ContextHolder.getContext().getUserId(), offerResponseList);

        StatusMessage statusMessage = rps.saveCompleteOfferResponse(offerResponseList, companyId,platform);

        logger.debug("Controller response for add customer offer response request for USER {} : {} "
                ,ContextHolder.getContext().getUserId(), statusMessage);
        return new ResponseEntity<>(statusMessage,
                HttpStatus.ACCEPTED);
    }

    // Offer response upload with hierarchy mapping
    @RequestMapping(
            value = "companies/{companyId}/offers/completePromoOfferResponse/",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<StatusMessage> completePromoOfferResponse(
            @PathVariable("companyId") String companyId,
            @RequestBody List<UserPromoOfferResponse> offerResponseList,
            @RequestHeader(value = "platform" ,defaultValue = "android") String platform
    ) {
        logger.debug("Controller received add complete customer offer response request for USER {} : {} "
                ,ContextHolder.getContext().getUserId(), offerResponseList);

        StatusMessage statusMessage = rps.saveCompletePromoOfferResponse(offerResponseList, companyId,platform);

        logger.debug("Controller response for add customer offer response request for USER {} : {} "
                ,ContextHolder.getContext().getUserId(), statusMessage);
        return new ResponseEntity<>(statusMessage,
                HttpStatus.ACCEPTED);
    }

    @Deprecated
    @RequestMapping(
            value = {"companies/{companyId}/stores/{storeId}/sites/{siteId}/offers"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE )
    @ResponseBody
    public ResponseEntity<PromoMapOfferResponse> getSITEOFF(
            @PathVariable("companyId") String ci,
            @PathVariable("storeId") String sti,
            @PathVariable("siteId") String sii) {
        logger.debug("Controller received getAllOffers for site for USER {}: companyId : {}, storeId: {}",ContextHolder.getContext().getUserId(), ci, sti);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        PromoMapOfferResponse pmor;
        pmor = rps.getPromosForSite(ci, sti,sii);
        final ResponseEntity<PromoMapOfferResponse> getOfferResponseResponseEntity = new ResponseEntity<>(pmor, headers,
                HttpStatus.OK);
        logger.debug("Controller response for getContext Offers(MAP) at site level for USER {} : {} "
                ,ContextHolder.getContext().getUserId(), getOfferResponseResponseEntity);
        return getOfferResponseResponseEntity;
    }

    @Deprecated
    @RequestMapping(
            value = {"companies/{companyId}/stores/{storeId}/offers"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE )
    @ResponseBody
    public ResponseEntity<PromoMapOfferResponse> getSOFF(
            @PathVariable("companyId") String ci,
            @PathVariable("storeId") String sti) {
        logger.debug("Controller received getContext offers(Map) for Store for USER {} : companyId : {}, storeId: {}",ContextHolder.getContext().getUserId(), ci, sti);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        PromoMapOfferResponse pmor;
        pmor = rps.getPromosForStore(ci, sti);
        final ResponseEntity<PromoMapOfferResponse> getOfferResponseResponseEntity = new ResponseEntity<>(pmor, headers,
                HttpStatus.OK);
        logger.debug("Controller response for getContext Offers(MAP) for store for USER {} :  {} "
                ,ContextHolder.getContext().getUserId(), getOfferResponseResponseEntity);
        return getOfferResponseResponseEntity;
    }

    @RequestMapping(
            value = {"companies/{companyId}/stores/{storeId}/getAllOffers"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE )
    @ResponseBody
    public ResponseEntity<PromoOfferResponse> getASOFF(
            @PathVariable("companyId") String ci,
            @PathVariable("storeId") String sti,
            @RequestHeader(value = "platform" ,defaultValue = "android") String platform) {
        logger.debug("Controller received getAllOffers(List) for Store and USER {} : companyId : {}, storeId: {}",ContextHolder.getContext().getUserId(), ci, sti);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        PromoOfferResponse por;
        por = rps.getAllPromosForStore(ci, sti,platform);
        final ResponseEntity<PromoOfferResponse> getOfferResponseResponseEntity = new ResponseEntity<>(por, headers,
                HttpStatus.OK);
        logger.debug("Controller response for getContext Offers (List) for USER {} : {} "
                ,ContextHolder.getContext().getUserId(), getOfferResponseResponseEntity);
        return getOfferResponseResponseEntity;
    }

}
