package me.cuenca.price;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("application.properties")
public class PriceConf {
  private String mongoHost;
  private String mongoDb = "Price";

  public String getMongoHost() {
    return mongoHost;
  }

  public String getMongoDb() {
    return mongoDb;
  }
}
