
package com.bosch.pai.retail.adtuning.responses;

import com.bosch.pai.retail.adtuning.model.offer.PromoDetail;
import com.bosch.pai.retail.common.responses.StatusMessage;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;




public class GetOfferResponseTest {
    private final String SUCCESS = "Successfully";
    private String key = "1";
    private final String companyId = "20";
    private final String displayMessage = "welcome";
    private final String messageCode = "100";
    private final String storeId = "20011";
    private final String locationName = "lab";
    private final String locationCode = "200";
    private final String siteName = "20011_abc";
    private final Integer rank = 1;
    private Map<String, String> customDetailMap = new HashMap<>();
    private final String ASSERT_MESSAGE = "Assertion failed";

    @Test
    public void testGetOfferResponse() {
        GetOfferResponse getOfferResponse = new GetOfferResponse();
        Set<PromoDetail> promoDetailSet = new HashSet<>();
        PromoDetail promoDetail = new PromoDetail();
        promoDetailSet.add(promoDetail);
        getOfferResponse.setPromoDetailList(promoDetailSet);
        StatusMessage statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS, SUCCESS);
        getOfferResponse.setStatusMessage(statusMessage);

        final StatusMessage statusMessageActual = getOfferResponse.getStatusMessage();
        Assert.assertEquals(ASSERT_MESSAGE, statusMessage, statusMessageActual);
        final Set<PromoDetail> promoDetailListActual = getOfferResponse.getPromoDetailList();
        Assert.assertEquals(ASSERT_MESSAGE, promoDetailSet, promoDetailListActual);

    }

}
