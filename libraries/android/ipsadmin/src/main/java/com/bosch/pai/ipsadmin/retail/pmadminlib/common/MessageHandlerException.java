package com.bosch.pai.ipsadmin.retail.pmadminlib.common;

public class MessageHandlerException extends Exception {


    public MessageHandlerException(String message) {
        super(message);
    }

    @Override
    public String getMessage(){

        return "Internal error from proximity library. ";

    }
}