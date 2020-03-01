package me.cuenca.price.domain.model;

public class ExchangeId {
  private String excode;

  public ExchangeId(String nye) {
    excode = nye.toUpperCase();
  }

  public String getExcode() {
    return excode;
  }
}
