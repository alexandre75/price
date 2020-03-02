package me.cuenca.price;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("mongo")
public class MongoConf {
  private String hosts;
  private String db = "Price";

  public String getHosts() {
    return hosts;
  }

  public void setHosts(String hosts) {
    this.hosts = hosts;
  }

  public String getDb() {
    return db;
  }

  public void setDb(String db) {
    this.db = db;
  }
}
