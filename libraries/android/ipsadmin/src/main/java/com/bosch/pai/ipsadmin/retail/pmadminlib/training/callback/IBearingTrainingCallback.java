package com.bosch.pai.ipsadmin.retail.pmadminlib.training.callback;

import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.ScannedBleDetails;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.SnapshotItemWithSensorType;

import java.util.List;
import java.util.Set;

public interface IBearingTrainingCallback {

    interface IDownloadSiteLocationsListener {

        void onSuccess();

        void onFailure(String errorMessage);

    }

    interface IBearingSetServerEndpointListener {

        void onSuccess();

        void onFailure(String errormessage);

    }

    interface IBearingSiteSignalMergeListener {

        void onSuccess(String siteName, List<SnapshotObservation> observations, List<SnapshotItemWithSensorType> snapshotObservations);

        void onFailure(String errorMessage);
    }


    interface IBearingOnLocationTrainAndRetrain {

        void onSuccess(Integer progress);

        void onFailure(String errorMessage);

    }

    interface IBearingOnUpload {

        void onSuccess();

        void onFailure(String errorMessage);

    }

    interface IBearingSuncWithServerListener {

        void onSuccess();

        void onFailure(String errorMessage);

    }

    interface IUtilityGetSiteListListenerFromServer {

        void onSuccess(Set<String> siteNames);

        void onFailure(String errorMessage);
    }

    interface IUtilityGetLocationListListenerFromServer {

        void onSuccess(Set<String> locationNames);

        void onFailure(String errorMessage);
    }


    interface IBearingDataDelete {

        void onSuccess();

        void onFailure();

    }

    interface ITrainsite {

        void onFailure(String errorMessage);

        void onWifiSignalCapture(List<SnapshotObservation> observations, List<SnapshotItemWithSensorType> snapshotObservations);

        void onBleSignalCapture(List<SnapshotObservation> observations, List<ScannedBleDetails> bleSourceId);
    }

    interface IBearingBleSiteSignalMergeListener {

        void onSuccess(List<SnapshotObservation> observations, List<ScannedBleDetails> bleSourceId);

        void onFailure(String errorMessage);
    }


    interface ITrainBleLocation {

        void onSuccess();

        void onFailure(String errorMessage);
        
    }
}
