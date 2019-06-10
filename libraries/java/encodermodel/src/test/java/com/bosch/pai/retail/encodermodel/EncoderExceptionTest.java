package com.bosch.pai.retail.encodermodel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EncoderExceptionTest {
    EncoderException encoderException = new EncoderException("error");
    @Test

            public void testEncoderExceptionTest(){
    EncoderException encoderException = new EncoderException("error");
    EncoderException encoderException2 = new EncoderException("error",new Throwable("test"));

    assertEquals("Error in encrypting data . "+"error",encoderException.getErrorMessage());
        assertEquals("Error in encrypting data . "+"error",encoderException2.getErrorMessage());



    }

}
