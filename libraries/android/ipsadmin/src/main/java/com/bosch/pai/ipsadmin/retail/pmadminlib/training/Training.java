package com.bosch.pai.ipsadmin.retail.pmadminlib.training;

import android.content.Context;

import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.SnapshotObservation;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.callback.IBearingTrainingCallback;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.BearingSitedetails;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.ScannedBleDetails;
import com.bosch.pai.ipsadmin.retail.pmadminlib.training.models.SnapshotItemWithSensorType;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Training {

    void setBearingServerEndPoint(String serverEndPoint, InputStream certificateStream, IBearingTrainingCallback.IBearingSetServerEndpointListener listener);

    void storeBearingData(boolean trueForExternalFalseForInternal, Context context);

    // changed apis
    void trainSite(String siteName, int numberOfFloors, int rssiValue, boolean isWifiSensor, IBearingTrainingCallback.ITrainsite listener);

    void snapshotFeatchSoonAfterTrainSite(String siteName, IBearingTrainingCallback.IBearingSiteSignalMergeListener listener);


    boolean siteBleUpdateOnMerge(String siteName, List<SnapshotObservation> snapshotObservations, Set<String> sourceIds);


    boolean updateSnapshot(String siteName, List<SnapshotObservation> snapshotObservations, List<SnapshotItemWithSensorType> snapshotItemWithSensorTypeList);


    boolean bleUpdateSnapshot(String siteName, List<SnapshotObservation> snapshotObservations, Set<String> sourceIds);

    void mergeSite(String site, IBearingTrainingCallback.IBearingSiteSignalMergeListener listener);

    void mergeBLESite(String site, IBearingTrainingCallback.IBearingBleSiteSignalMergeListener listener);

    boolean siteUpdateOnMerge(String siteName, List<SnapshotObservation> snapshotObservations, List<SnapshotItemWithSensorType> snapshotItemWithSensorTypeList);


    void trainLocation(String sitename, String locationName, IBearingTrainingCallback.IBearingOnLocationTrainAndRetrain listener);

    void retrainLocation(String siteName, String location, final IBearingTrainingCallback.IBearingOnLocationTrainAndRetrain listener);

    void uploadSite(String siteName, final IBearingTrainingCallback.IBearingOnUpload listener);

    void uploadLocations(String siteName,/*boolean blereTrained,boolean wifireTrained,*/ final IBearingTrainingCallback.IBearingOnUpload listener);

    void generateClaasifier(String sitename, final IBearingTrainingCallback.IBearingOnUpload listener);

    void uploadSiteLocations(String sitename,/*boolean blereTrained,boolean wifireTrained,*/ final IBearingTrainingCallback.IBearingOnUpload listener);

    void uploadSiteLocationAndGenerateClassifier(String siteName, final IBearingTrainingCallback.IBearingOnUpload listener);

    void downloadAllSitesAndLocationsFromServer(IBearingTrainingCallback.IBearingSuncWithServerListener listener);

    void downloadSiteAndLocations(String siteName, IBearingTrainingCallback.IDownloadSiteLocationsListener listener);

    Set<String> getAllSiteNamesFromLocal();

    void getAllSiteNamesFromServer(IBearingTrainingCallback.IUtilityGetSiteListListenerFromServer listener);

    BearingSitedetails getAllLocationNamesForSiteFromLocal(String sitename);

    void getAllLocationNamesForSiteFromServer(String sitename, IBearingTrainingCallback.IUtilityGetLocationListListenerFromServer listener);

    void deleteBearingData(String siteName, List<String> locations, IBearingTrainingCallback.IBearingDataDelete iBearingDataDelete);


    // BLES
    Set<ScannedBleDetails> getBleIds(String siteName);

    void trainBleLocation(String siteName, String locationName, double bleThreshold, String bleId, IBearingTrainingCallback.ITrainBleLocation listener);

    void retrainBleLocation(String siteName, String locationName, double bleThreshold, String bleId, IBearingTrainingCallback.ITrainBleLocation listener);

    boolean updateSiteConfig(final String siteName, final int rssiThresh);

    List<BearingConfiguration.SensorType> getSensorTypes(String siteName);

    Map<String, String> getBleMappingForLocation(String siteName);

    Map<String, Map<Double, List<String>>> getBleAndthreshMappingForLocation(String siteName);
}
