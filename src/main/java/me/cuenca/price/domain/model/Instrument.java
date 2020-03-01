package me.cuenca.price.domain.model;

public class Instrument {
  private String isin;

  public Instrument(String fr123456) {
    isin = fr123456.toUpperCase();
  }

  public static Instrument isin(String isin) {
    return new Instrument(isin);
  }

  public String getIsin() {
    return isin;
  }
}
