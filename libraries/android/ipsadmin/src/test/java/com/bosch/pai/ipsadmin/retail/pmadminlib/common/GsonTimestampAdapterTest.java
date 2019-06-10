package com.bosch.pai.ipsadmin.retail.pmadminlib.common;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JsonElement.class, Type.class,JsonDeserializationContext.class})
public class GsonTimestampAdapterTest {
    GsonTimestampAdapter gsonTimestampAdapter;

    @Mock
    DateFormat dateFormat;

    @Mock
    JsonElement jsonElement;
    @Mock
    Type type;
    @Mock
    JsonDeserializationContext context;

    Date date;

    SimpleDateFormat sdf;
    String timeStamp;
    Timestamp timestamp1;
    @Before
    public void init() {
        gsonTimestampAdapter = new GsonTimestampAdapter();
        sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.getDefault());
        date = new Date();
        timeStamp = sdf.format(date);
        timestamp1 = new Timestamp(date.getTime());
       // dateFormat = new SimpleDateFormat();

    }


    @Test
    public void ConstructorTest() {

        gsonTimestampAdapter = new GsonTimestampAdapter();
    }

    @Test
    public void deserializeTest() throws Exception {

      //  Timestamp timestamp=new Timestamp(dateFormat.parse(jsonElement.getAsString()).getTime());
        whenNew(Timestamp.class).withAnyArguments().thenReturn(timestamp1);
        when(jsonElement.getAsString()).thenReturn("testString");
        when(dateFormat.parse(anyString())).thenReturn(date);
        gsonTimestampAdapter.deserialize(jsonElement, type,context);


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
        Assert.assertNotNull(gsonTimestampAdapter.serialize(timeStampDate, type, jsonSerializationContext));
    }

}
