package me.cuenca.price.domain.model;

public class ExchangeId {
  private String mic;

  public ExchangeId(String mic) {
    this.mic = mic.toUpperCase();
  }

  public String getMic() {
    return mic;
  }
}
