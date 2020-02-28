package me.cuenca.price;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application.properties")
public class PriceConf {
  private String mongoHost;
  private String mongoDb = "Price";

  public String getMongodb() {
    return mongoHost;
  }

  public String getMongoDb() {
    return mongoDb;
  }
}
