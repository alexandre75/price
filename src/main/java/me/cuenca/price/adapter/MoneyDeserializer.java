package me.cuenca.price.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;

public class MoneyDeserializer implements JsonSerializer<Integer>, JsonDeserializer<Integer> {
  @Override
  public JsonElement serialize(Integer src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.doubleValue() / 100D);
  }

  @Override
  public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    return (int) Math.round(json.getAsDouble() * 100);
  }
}
