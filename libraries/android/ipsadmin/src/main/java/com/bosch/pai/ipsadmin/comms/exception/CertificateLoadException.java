package com.bosch.pai.ipsadmin.comms.exception;

/**
 * Custom certificate load exception thrown to the library user, if the module fails to load the SSLCertificate successfully
 */
public class CertificateLoadException extends Exception {

    private final String errMessage;

    /**
     * Instantiates a new Certificate load exception.
     *
     * @param message the message
     */
    public CertificateLoadException(String message) {
        super(message);
        this.errMessage = message;
    }

    /**
     * Instantiates a new Certificate load exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public CertificateLoadException(String message, Throwable cause) {
        super(message, cause);
        this.errMessage = message;
    }

    /**
     * Gets err message.
     *
     * @return the err message
     */
    public String getErrMessage() {
        return errMessage;
    }

}