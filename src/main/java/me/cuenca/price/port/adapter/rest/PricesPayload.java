package me.cuenca.price.port.adapter.rest;

import me.cuenca.price.domain.model.eod.Price;

import java.util.List;

public class PricesPayload {
  public String symbol;
  public List<Price> prices;

  private PricesPayload() {}

  public PricesPayload(String symbol, List<Price> prices) {
    this.symbol = symbol;
    this.prices = prices;
  }
}
