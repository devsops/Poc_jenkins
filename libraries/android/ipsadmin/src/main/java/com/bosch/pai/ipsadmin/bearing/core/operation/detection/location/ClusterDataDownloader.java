package com.bosch.pai.ipsadmin.bearing.core.operation.detection.location;


import com.bosch.pai.ipsadmin.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.datamodel.bearingdatamodels.Snapshot;
import com.bosch.pai.bearing.persistence.PersistenceHandler;
import com.bosch.pai.bearing.persistence.datastore.DataStore;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.restclient.BearingClientCallback;
import com.bosch.pai.ipsadmin.bearing.sensordatastore.restclient.BearingRESTClient;

class ClusterDataDownloader {

    private static final String TAG = ClusterDataDownloader.class.getName();

    private ClusterDataDownloader() {
        //Needed private constructor
    }

    static synchronized void validateClusterDataAndDownload(final String siteName) {
        final PersistenceHandler persistenceHandler = new PersistenceHandler(DataStore.StoreType.FILE);
        final Snapshot snapshot = persistenceHandler.readSnapShot(siteName);
        final long availableDocumentVersion = persistenceHandler.getDocVersionAvailableLocally(siteName);
        final BearingRESTClient bearingRESTClient = BearingRESTClient.getInstance();
        if (availableDocumentVersion == snapshot.getDocVersion()) {
            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "No version change for classifier version. " +
                    "Not downloading cluster data");
        } else {
            final Thread validateAndDownloadClusterDataThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    bearingRESTClient.getClusterData(siteName, new BearingClientCallback() {
                        @Override
                        public void onRequestSuccess(String message) {
                            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, message);
                        }

                        @Override
                        public void onRequestFailure(String errMessage) {
                            LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, errMessage);
                        }
                    });
                }
            });
            validateAndDownloadClusterDataThread.setName("ValidateAndDownloadClusterDataThread");
            validateAndDownloadClusterDataThread.start();
        }
        bearingRESTClient.downloadSiteThreshData(siteName, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, message);
            }

            @Override
            public void onRequestFailure(String errMessage) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.ERROR, TAG, errMessage);
            }
        });
    }
}
