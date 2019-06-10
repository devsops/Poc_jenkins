package com.bosch.pai.retail.encodermodel;


public class EncoderException extends Exception {

    private final String errorMessage;

    public EncoderException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public EncoderException(String errorMessage, Throwable e) {
        super(errorMessage, e);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return "Error in encrypting data . "+errorMessage;
    }
}
