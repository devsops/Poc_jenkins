package com.bosch.pai.ipsadmin.bearing.core.operation.readoperations;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Server response.
 */
public class ServerResponse {

    private boolean responseStatusSuccess;
    private List<String> names;


    /**
     * Instantiates a new Server response.
     *
     * @param responseStatus the response status
     * @param responseList   the response list
     */
    public ServerResponse(Boolean responseStatus, List<String> responseList) {
        this.responseStatusSuccess = responseStatus;
        this.names = responseList != null ? new ArrayList<>(responseList) : new ArrayList<String>();
    }

    /**
     * Is response status success boolean.
     *
     * @return the boolean
     */
    public boolean isResponseStatusSuccess() {
        return responseStatusSuccess;
    }

    /**
     * Gets names.
     *
     * @return the names
     */
    public List<String> getNames() {
        return Collections.unmodifiableList(names);
    }
}
