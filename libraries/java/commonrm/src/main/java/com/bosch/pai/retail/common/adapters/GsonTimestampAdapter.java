package com.bosch.pai.retail.common.adapters;



import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class GsonTimestampAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {
    private final DateFormat dateFormat;

    public GsonTimestampAdapter() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
    }

    @Override
    public Timestamp deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return new Timestamp(dateFormat.parse(jsonElement.getAsString()).getTime());

        } catch (ParseException e) {

            return new Timestamp(jsonElement.getAsLong());
        }
    }

    @Override
    public JsonElement serialize(Timestamp timestamp, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(dateFormat.format(timestamp));
    }

 /*   @Override
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
    }*/



}
