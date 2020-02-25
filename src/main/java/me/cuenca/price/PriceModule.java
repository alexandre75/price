package me.cuenca.price;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.mongodb.MongoClient;
import me.cuenca.price.adapter.PriceResource;
import me.cuenca.price.domain.model.ExchangeId;
import me.cuenca.price.domain.model.Instrument;
import me.cuenca.price.domain.service.Symbol;
import me.cuenca.price.domain.service.SymbolMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;

@Configuration
public class PriceModule extends ResourceConfig {

  public PriceModule() {
    register(PriceResource.class);
  }

  @Bean
  public SymbolMapper symbolMapper() {
    return sym -> new Symbol(new Instrument(sym), new ExchangeId("NYE"));
  }
  @Bean
  public MongoClient mongo() {
    return new MongoClient("localhost");
  }

  @Bean
  public ObjectMapper mapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(LocalDate.class, new LocalDateDeserializer());
    objectMapper.registerModule(module);
    return objectMapper;
  };
}

class LocalDateDeserializer extends StdDeserializer<LocalDate> {

  protected LocalDateDeserializer() {
    super(LocalDate.class);
  }

  @Override
  public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    return LocalDate.parse(p.getValueAsString());
  }
}