package me.cuenca.price;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Splitter;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import me.cuenca.price.port.adapter.rest.PriceResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Configuration
public class PriceModule extends ResourceConfig {
  private static Logger logger = LoggerFactory.getLogger(PriceModule.class);

  public PriceModule() {
    register(PriceResource.class);
  }

  @Bean
  public MongoClient mongo(MongoConf conf) {
    logger.info("Hosts " + conf.getHosts());
    return new MongoClient(Splitter.on(',').trimResults().splitToList(conf.getHosts())
            .stream()
            .map(host -> new ServerAddress(host, 27017))
            .collect(Collectors.toList()));
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