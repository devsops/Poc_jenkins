package com.bosch.pai.bearing.sensordatastore.restclient;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * The type Gson utc adapter.
 */
public class GsonUTCAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {

    private final DateFormat dateFormat;

    /**
     * Instantiates a new Gson utc adapter.
     */
    public GsonUTCAdapter() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public synchronized JsonElement serialize(Timestamp timestamp, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dateFormat.format(timestamp));
    }

    @Override
    public synchronized Timestamp deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        try {
            return new Timestamp(dateFormat.parse(jsonElement.getAsString()).getTime());

        } catch (ParseException e) {

            return new Timestamp(jsonElement.getAsLong());
        }
    }
}
