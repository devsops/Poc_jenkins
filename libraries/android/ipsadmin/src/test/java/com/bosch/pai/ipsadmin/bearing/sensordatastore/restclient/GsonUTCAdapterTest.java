package com.bosch.pai.ipsadmin.bearing.sensordatastore.restclient;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GsonUTCAdapter.class})
public class GsonUTCAdapterTest {

    private GsonUTCAdapter gsonUTCAdapter;
    @Mock
    JsonElement jsonElement;
    @Mock
    DateFormat dateFormat;

    @Before
    public void init() throws Exception {
        gsonUTCAdapter = new GsonUTCAdapter();
    }

    @Test
    public void serializeTest() throws Exception{
        String str_date="13-09-2011";
        DateFormat formatter ;
        Date date ;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date)formatter.parse(str_date);
        java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());
        final Type type = null;
        final JsonSerializationContext jsonSerializationContext = null;
        Assert.assertNotNull(gsonUTCAdapter.serialize(timeStampDate, type, jsonSerializationContext));
    }

    @Test
    public void deserializeTest() throws Exception{
        Timestamp timestamp1 = null;
        final Type type = null;
        Date date = null;
        final JsonDeserializationContext jsonDeserializationContext = null;
        whenNew(Timestamp.class).withAnyArguments().thenReturn(timestamp1);
        when(jsonElement.getAsString()).thenReturn("testString");
        when(dateFormat.parse(anyString())).thenReturn(date);
        Assert.assertNull(gsonUTCAdapter.deserialize(jsonElement, type, jsonDeserializationContext));
    }
}
