package me.cuenca.price.domain.model;

public class Instrument {
  private String isin;

  public Instrument(String fr123456) {
    isin = fr123456;
  }

  public String getIsin() {
    return isin;
  }
}
