package com.bosch.pai.bearing.core.operation.readoperations;



import com.bosch.pai.bearing.benchmark.LogAndToastUtil;
import com.bosch.pai.bearing.core.operation.detection.location.LocationDetectorUtil;
import com.bosch.pai.bearing.core.operation.detection.site.SiteDetectorUtil;
import com.bosch.pai.bearing.datamodel.apimodels.configuration.BearingConfiguration;
import com.bosch.pai.bearing.sensordatastore.restclient.BearingClientCallback;
import com.bosch.pai.bearing.sensordatastore.restclient.BearingRESTClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The type Synchronous server calls.
 */
public class SynchronousServerCalls {

    private final Logger logger = LoggerFactory.getLogger(SynchronousServerCalls.class);

    private BearingRESTClient bearingRESTClient;
    private SiteDetectorUtil siteDetectorUtil;
    private LocationDetectorUtil locationDetectorUtil;
    private static final String TAG = SynchronousServerCalls.class.getName();


    /**
     * Instantiates a new Synchronous server calls.
     *
     * @param bearingRESTClient    the bearing rest client
     * @param siteDetectorUtil     the site detector util
     * @param locationDetectorUtil the location detector util
     */
    public SynchronousServerCalls(BearingRESTClient bearingRESTClient, SiteDetectorUtil siteDetectorUtil, LocationDetectorUtil locationDetectorUtil) {
        this.bearingRESTClient = bearingRESTClient;
        this.locationDetectorUtil = locationDetectorUtil;
        this.siteDetectorUtil = siteDetectorUtil;
    }


    /**
     * Upload site Snapshot for the corresponding site. The response returned is a synchronous response for the site .
     *
     * @param siteName name of the site whose snapshot has to be uploaded.
     * @return the boolean
     */
    public boolean uploadSiteData(String siteName) {
        final AtomicReference<Boolean> notifier = new AtomicReference<>();
        bearingRESTClient.uploadSiteData(siteName, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                synchronized (notifier) {
                    notifier.set(true);
                    notifier.notifyAll();
                }
            }

            @Override
            public void onRequestFailure(String errMessage) {
                synchronized (notifier) {
                    notifier.set(false);
                    notifier.notifyAll();
                }

            }
        });
        synchronized (notifier) {
            while (notifier.get() == null) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                    logger.error(TAG, "Error uploading site data", e);
                    // Restore interrupted state...
                    Thread.currentThread().interrupt();
                }
            }
        }
        return notifier.get();
    }

    /**
     * Upload the location csv for the location corresponding to the site .
     *
     * @param siteName     name of the site to upload the siteName.
     * @param locationName name of the location whose data has to
     * @param approach     the approach
     * @return the boolean
     */
    public boolean uploadLocationData(String siteName, String locationName, BearingConfiguration.Approach approach) {
        final AtomicReference<Boolean> notifier = new AtomicReference();
        bearingRESTClient.uploadLocationData(siteName, locationName, approach, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                synchronized (notifier) {
                    notifier.set(true);
                    notifier.notifyAll();
                }

            }

            @Override
            public void onRequestFailure(String errMessage) {

                synchronized (notifier) {
                    notifier.set(false);
                    notifier.notifyAll();
                }

            }
        });
        synchronized (notifier) {
            while (notifier.get() == null) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                    logger.error(TAG, "Error uploading location data", e);
                    // Restore interrupted state...
                    Thread.currentThread().interrupt();
                }
            }
        }

        return notifier.get();
    }

    /**
     * Upload all locations for the site. The siteName to upload all locations for the site.
     *
     * @param siteName the site name
     * @param approach the approach
     * @return the boolean
     */
    public boolean uploadAllLocationsForSite(String siteName, BearingConfiguration.Approach approach) {

        final AtomicReference<Boolean> notifier = new AtomicReference();

        bearingRESTClient.uploadLocationsDataForSite(siteName, approach, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                synchronized (notifier) {
                    notifier.set(true);
                    notifier.notifyAll();
                }

            }

            @Override
            public void onRequestFailure(String errMessage) {
                synchronized (notifier) {
                    notifier.set(false);
                    notifier.notifyAll();
                }

            }
        });

        synchronized (notifier) {
            while (notifier.get() == null) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                    logger.error(TAG, "Error uploading all locations", e);
                    // Restore interrupted state...
                    Thread.currentThread().interrupt();
                }
            }
        }

        return notifier.get();

    }


    /**
     * Downloads the site data to Bearing Storage synchronously and returns a boolean for success and failure scenarios.
     *
     * @param siteName the site name
     * @return true : download for the site was successful,false: download for the site was a failure.
     */
    public boolean downloadSiteDataAndWriteToPersistence(final String siteName) {

        final AtomicReference<Boolean> notifier = new AtomicReference();

        bearingRESTClient.downloadSiteData(siteName, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                synchronized (notifier) {
                    notifier.set(true);
                    notifier.notifyAll();
                }
            }

            @Override
            public void onRequestFailure(String errMessage) {
                synchronized (notifier) {
                    notifier.set(false);
                    notifier.notifyAll();
                }
            }
        });

        synchronized (notifier) {
            while (notifier.get() == null) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                    logger.error(TAG, "Error downloading site data", e);
                    // Restore interrupted state...
                    Thread.currentThread().interrupt();
                }
            }
        }
        return notifier.get();

    }


      /*Synchronously gets all the site Names from the server*/

    /**
     * Gets all sites names synchronous server call.
     *
     * @return the all sites names synchronous server call
     */
    public ServerResponse getAllSitesNamesSynchronousServerCall() {

        final AtomicReference<ServerResponse> notifier = new AtomicReference();

        bearingRESTClient.getAllSiteNames(new BearingClientCallback.GetDataCallback() {
            @Override
            public void onDataReceived(List<String> dataList) {
                synchronized (notifier) {
                    ServerResponse serverResponse = new ServerResponse(true, dataList);
                    notifier.set(serverResponse);
                    notifier.notifyAll();
                }
            }

            @Override
            public void onDataReceivedError(String errorMessage) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "onDataReceivedError: " + errorMessage);
                synchronized (notifier) {
                    ServerResponse serverResponse = new ServerResponse(false, new ArrayList<String>());
                    notifier.set(serverResponse);
                    notifier.notifyAll();
                }

            }
        });

        synchronized (notifier) {
            while (notifier.get() == null) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                    logger.error(TAG, "Error getting site names", e);
                    // Restore interrupted state...
                    Thread.currentThread().interrupt();
                }
            }
        }


        return notifier.get();
    }


    /**
     * Site rename synchronous server call boolean.
     *
     * @param oldSiteName the old site name
     * @param newSiteName the new site name
     * @return the boolean
     */
    public boolean siteRenameSynchronousServerCall(String oldSiteName, String newSiteName) {

        final AtomicReference<Boolean> notifier = new AtomicReference();
        bearingRESTClient.renameSiteOnServer(oldSiteName, newSiteName, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                synchronized (notifier) {
                    notifier.set(true);
                    notifier.notifyAll();
                }
            }

            @Override
            public void onRequestFailure(String errMessage) {
                synchronized (notifier) {
                    notifier.set(false);
                    notifier.notifyAll();
                }
            }
        });

        synchronized (notifier) {
            while (notifier.get() == null) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                    logger.error(TAG, "Error renaming site", e);
                    // Restore interrupted state...
                    Thread.currentThread().interrupt();
                }
            }
        }
        return notifier.get();

    }


    /**
     * Download all the locations for the site and return a status for complete download
     *
     * @param siteName name of the site which has locations to download.
     * @return the boolean
     */
    public boolean downloadLocationsForSiteAndWriteToPersistence(final String siteName) {

        final AtomicReference<Boolean> notifier = new AtomicReference();

        bearingRESTClient.downloadLocationsDataForSite(siteName, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                synchronized (notifier) {
                    notifier.set(true);
                    notifier.notifyAll();
                }

            }

            @Override
            public void onRequestFailure(String errMessage) {
                synchronized (notifier) {
                    notifier.set(false);
                    notifier.notifyAll();
                }

            }
        });

        synchronized (notifier) {
            while (notifier.get() == null) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                    logger.error(TAG, "Error downloading locations", e);
                    // Restore interrupted state...
                    Thread.currentThread().interrupt();
                }
            }
        }

        return notifier.get();
    }

    /**
     * Gets all sites sync with server.
     *
     * @return the all sites sync with server
     */
    public List<String> getAllSitesSyncWithServer(boolean isPersist) {

        final ServerResponse allSitesNamesSynchronousServerCall = getAllSitesNamesSynchronousServerCall();
        final boolean success = allSitesNamesSynchronousServerCall.isResponseStatusSuccess();
        if (!success) {
            return new ArrayList<>();
        }
        List<String> siteNameList = allSitesNamesSynchronousServerCall.getNames();
        if (!isPersist) {
            return siteNameList;
        }
        boolean downloadStatusForSiteData = true;
        for (String siteName : siteNameList) {
            final boolean downloadStatus = downloadSiteDataAndWriteToPersistence(siteName);
            downloadStatusForSiteData = downloadStatusForSiteData && downloadStatus;
        }
        if (downloadStatusForSiteData) {
            return siteDetectorUtil.getSiteNames();
        }
        return new ArrayList<>();
    }

    /**
     * Gets all locations sync with server.
     *
     * @param siteName the site name
     * @return the all locations sync with server
     */
    public List<String> getAllLocationsSyncWithServer(String siteName, boolean isPersist) {


        if (!isPersist) {
            final ServerResponse allLocationNamesSynchronousServerCall = getAllLocationNamesSynchronousServerCall(siteName);
            final boolean isSuccess = allLocationNamesSynchronousServerCall.isResponseStatusSuccess();

            if (isSuccess) {
                return allLocationNamesSynchronousServerCall.getNames();
            } else {
                return new ArrayList<>();
            }
        }

        final boolean svmtSynchronousServerCall = getCellHierarchySynchronousServerCall(siteName);
        if (svmtSynchronousServerCall) {
            final List<String> locationList = locationDetectorUtil.getLocationNames(siteName, BearingConfiguration.Approach.FINGERPRINT);
            final List<String> locationFromSVMT = locationDetectorUtil.getLocationNamesFromClusterData(siteName);
            locationFromSVMT.addAll(locationList);
            return locationFromSVMT;
        }
        return new ArrayList<>();
    }


    private boolean getCellHierarchySynchronousServerCall(String siteName) {

        final AtomicReference<Boolean> notifier = new AtomicReference();

        bearingRESTClient.getClusterData(siteName, new BearingClientCallback() {
            @Override
            public void onRequestSuccess(String message) {
                synchronized (notifier) {
                    notifier.set(true);
                    notifier.notifyAll();
                }
            }

            @Override
            public void onRequestFailure(String errMessage) {
                synchronized (notifier) {
                    notifier.set(false);
                    notifier.notifyAll();
                }
            }
        });
        synchronized (notifier) {
            while (notifier.get() == null) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                    logger.error(TAG, "Error getting cell hierarchy", e);
                    // Restore interrupted state...
                    Thread.currentThread().interrupt();
                }
            }
        }
        return notifier.get();
    }


    private ServerResponse getAllLocationNamesSynchronousServerCall(String siteName) {

        final AtomicReference<ServerResponse> notifier = new AtomicReference();

        bearingRESTClient.getAllLocationNamesForSite(siteName, new BearingClientCallback.GetDataCallback() {
            @Override
            public void onDataReceived(List<String> dataList) {
                synchronized (notifier) {
                    ServerResponse serverResponse = new ServerResponse(true, dataList);
                    notifier.set(serverResponse);
                    notifier.notifyAll();
                }
            }

            @Override
            public void onDataReceivedError(String errorMessage) {
                LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG, TAG, "onDataReceivedError: " + errorMessage);
                synchronized (notifier) {
                    ServerResponse serverResponse = new ServerResponse(false, new ArrayList<String>());
                    notifier.set(serverResponse);
                    notifier.notifyAll();
                }

            }
        });

        synchronized (notifier) {
            while (notifier.get() == null) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                    logger.error(TAG, "Error getting site names", e);
                    // Restore interrupted state...
                    Thread.currentThread().interrupt();
                }
            }
        }


        return notifier.get();
    }
}
