package me.cuenca.price;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("mongo")
public class MongoConf {
  private String host;
  private String db = "Price";

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getDb() {
    return db;
  }

  public void setDb(String db) {
    this.db = db;
  }
}
