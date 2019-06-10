package com.bosch.pai.retail.analytics.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QuickAssistAnalyticsResponseTest {

    @Test

    public void testQuickAssistAnalyticsResponseTest() {

        QuickAssistAnalyticsResponse quickAssistAnalyticsResponse = new QuickAssistAnalyticsResponse();

        quickAssistAnalyticsResponse.setQuickAssistRequestCount(18);
        quickAssistAnalyticsResponse.setServedQuickAssistCount(12);

        assertEquals(18,quickAssistAnalyticsResponse.getQuickAssistRequestCount());
        assertEquals(12,quickAssistAnalyticsResponse.getServedQuickAssistCount());
        assertNotNull(quickAssistAnalyticsResponse.toString());













    }











}
