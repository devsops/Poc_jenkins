package com.bosch.pai.retail.common.serviceutil;

import org.junit.Test;
import org.junit.runner.Request;

import javax.naming.Context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ContextHolderTest {






    @Test


    public void testContextHolderTest(){

        ContextHolder.setContext(ContextHolder.getContext());

        assertEquals(ContextHolder.getContext(),ContextHolder.getContext());











    }










}
