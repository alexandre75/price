package me.cuenca.price.port.adapter.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.math.BigDecimal;

public class MoneyDeserializeJack extends StdDeserializer<Integer> {

  protected MoneyDeserializeJack() {
    super(Integer.class);
  }

  @Override
  public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return (int) Math.round(p.getDoubleValue() * 100);
  }
}
