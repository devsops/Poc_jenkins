package com.bosch.pai.retail.requests;

import com.bosch.pai.retail.configmodel.Timetype;

import org.junit.Test;

import java.security.PublicKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SaveLocationBaymapRequestTest {

    @Test
    public void testSaveLocationBaymapRequestTest(){


        SaveLocationBaymapRequest saveLocationBaymapRequest = new SaveLocationBaymapRequest("test",false);
        SaveLocationBaymapRequest saveLocationBaymapRequest2 = new SaveLocationBaymapRequest("test",false);
        SaveLocationBaymapRequest saveLocationBaymapRequest3 = new SaveLocationBaymapRequest();

        saveLocationBaymapRequest.setLocationbaymapping("test");
        saveLocationBaymapRequest.setOverrideRequired(false);



        assertEquals("test",saveLocationBaymapRequest.getLocationbaymapping());
        assertEquals(false,saveLocationBaymapRequest.isOverrideRequired());

        assertNotNull(saveLocationBaymapRequest3);
        assertNotNull(saveLocationBaymapRequest.toString());
        assertNotNull(saveLocationBaymapRequest.hashCode());
        assertTrue(saveLocationBaymapRequest.equals(saveLocationBaymapRequest2));








    }























}
