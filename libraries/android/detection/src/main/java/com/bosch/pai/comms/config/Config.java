package com.bosch.pai.comms.config;


import javax.validation.constraints.NotNull;

/**
 * The type Config.
 * Needed for configuring multiple SSL Cert.  Will be use later.
 */
public class Config {

    private static final String DEFAULT_HOST_URL = "https://localhost:8080/gatewayService/";

    private final String hostURL;

    private String absoluteFilePath;

    /**
     * Instantiates a new Config.
     */
    public Config() {
        this(DEFAULT_HOST_URL);
    }

    /**
     * Instantiates a new Config.
     *
     * @param hostURL the host url
     */
    public Config(@NotNull String hostURL) {
        this.hostURL = hostURL;
    }

    /**
     * The type Builder.
     */
    public static class Builder {

        private String hostURL = DEFAULT_HOST_URL;

        /**
         * Create config.
         *
         * @return the config
         */
        public Config create() {
            return new Config(hostURL);
        }

        /**
         * Sets host url.
         *
         * @param hostURL the host url
         * @return the host url
         */
        public Builder setHostURL(@NotNull String hostURL) {
            this.hostURL = hostURL;
            return this;
        }
    }

    /**
     * Gets host url.
     *
     * @return the host url
     */
    public String getHostURL() {
        return hostURL;
    }

    public String getAbsoluteFilePath() {
        return absoluteFilePath;
    }

    public void setAbsoluteFilePath(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
    }
}