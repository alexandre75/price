package me.cuenca.price;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mongodb.MongoClient;
import me.cuenca.price.port.adapter.rest.PriceResource;
import me.cuenca.price.domain.model.ExchangeId;
import me.cuenca.price.domain.model.Instrument;
import me.cuenca.price.domain.service.Symbol;
import me.cuenca.price.domain.service.SymbolMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;

@Configuration
public class PriceModule extends ResourceConfig {
  private static Logger logger = LoggerFactory.getLogger(PriceModule.class);

  public PriceModule() {
    register(PriceResource.class);
  }

  @Bean
  public SymbolMapper symbolMapper() {
    return sym -> new Symbol(new Instrument(sym), new ExchangeId("NYE"));
  }

  @Bean
  public MongoClient mongo(@Value("${mongo.host}") String host) {
    logger.info("Host " + host);
    return new MongoClient(host);
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