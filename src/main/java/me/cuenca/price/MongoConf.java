package me.cuenca.price;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties("mongo")
public class MongoConf {
  private String host;
  private String db = "Price";

  public String getMongoHost() {
    return host;
  }

  public String getMongoDb() {
    return db;
  }
}
