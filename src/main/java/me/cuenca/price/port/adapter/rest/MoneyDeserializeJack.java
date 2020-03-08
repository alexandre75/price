package me.cuenca.price.port.adapter.rest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class MoneyDeserializeJack extends StdDeserializer<Integer> {

  protected MoneyDeserializeJack() {
    super(Integer.class);
  }

  @Override
  public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    if (p.getCurrentValue() == null) {
      return null;
    } else {
      return (int) Math.round(p.getDoubleValue() * 1000);
    }
  }
}
