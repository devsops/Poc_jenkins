package com.bosch.pai.ipsadmin.retail.pmadminlib.analytics.callback;

import com.bosch.pai.retail.analytics.model.dwelltime.LocationDwellTime;
import com.bosch.pai.retail.analytics.model.heatmap.HeatMapDetail;
import com.bosch.pai.retail.analytics.responses.EntryExitResponse;
import com.bosch.pai.retail.analytics.responses.OfferAnalyticsResponse;

import java.util.List;

public interface IAnalyticsCallbacks {

    interface IAuthenticationListener {

        void onSuccess();

        void failure(String errorMessage);

    }

    interface IDwelltimeListener {

        void onSuccess(List<LocationDwellTime> locationDwellTimes);

        void onFailure(String errorMessage);

    }

    interface IHeatmapListener {

        void onSuccess(List<HeatMapDetail> heatMapDetails);

        void onFailure(String errorMessage);

    }

    interface IOfferAnalyticstListener {

        void onSuccess(List<OfferAnalyticsResponse> offerAnalyticsResponseList);

        void onFailure(String errorMessage);

    }

    interface IEntryExitListener {

        void onSuccess(EntryExitResponse entryExitResponse);

        void onFailure(String errorMessage);

    }
}
