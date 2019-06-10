package com.bosch.pai.bearing.sensordatastore.restclient;



import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * The interface Cluster data service api.
 */
public interface ClusterDataServiceAPI {

    /**
     * Gets cluster data.
     *
     * @param siteId the site id
     * @return the cluster data
     */
    @Headers({"accept:application/zip"})
    @GET("sites/{siteId}/clusterData")
    Call<ResponseBody> getClusterData(@Path("siteId") long siteId);
}
