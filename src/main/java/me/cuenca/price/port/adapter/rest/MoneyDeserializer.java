package me.cuenca.price.port.adapter.rest;

import com.google.gson.*;

import java.lang.reflect.Type;

public class MoneyDeserializer implements JsonSerializer<Integer>, JsonDeserializer<Integer> {
  @Override
  public JsonElement serialize(Integer src, Type typeOfSrc, JsonSerializationContext context) {
    if (src == null) {
      return JsonNull.INSTANCE;
    } else {
      return new JsonPrimitive(src.doubleValue() / 1000D);
    }
  }

  @Override
  public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if (json.isJsonNull()) {
      return null;
    } else {
      return (int) Math.round(json.getAsDouble() * 1000);
    }
  }
}
