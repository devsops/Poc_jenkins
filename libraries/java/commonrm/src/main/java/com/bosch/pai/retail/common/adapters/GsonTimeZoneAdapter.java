package com.bosch.pai.retail.common.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.TimeZone;

public class GsonTimeZoneAdapter implements JsonSerializer<TimeZone>, JsonDeserializer<TimeZone>{


    @Override
    public TimeZone deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return TimeZone.getTimeZone(json.toString());
    }

    @Override
    public JsonElement serialize(TimeZone timeZone, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(timeZone.toString());
    }
}
