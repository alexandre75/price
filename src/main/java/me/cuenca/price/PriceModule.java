package me.cuenca.price;

import com.mongodb.MongoClient;
import me.cuenca.price.adapter.PriceResource;
import me.cuenca.price.domain.model.ExchangeId;
import me.cuenca.price.domain.model.Instrument;
import me.cuenca.price.domain.service.Symbol;
import me.cuenca.price.domain.service.SymbolMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PriceModule extends ResourceConfig {

  public PriceModule() {
    register(PriceResource.class);
  }

  @Bean
  public SymbolMapper mapper() {
    return sym -> new Symbol(new Instrument(sym), new ExchangeId("NYE"));
  }
  @Bean
  public MongoClient mongo() {
    return new MongoClient("localhost");
  }
}
