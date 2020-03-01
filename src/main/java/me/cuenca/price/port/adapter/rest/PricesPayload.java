package me.cuenca.price.port.adapter.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.cuenca.price.domain.model.eod.Price;

import java.util.List;

public class PricesPayload {
  @JsonIgnore
  public String excode;

  @JsonIgnore
  public String isin;

  public List<Price> prices;

  private PricesPayload() {}

  public PricesPayload(List<Price> prices) {
    this.prices = prices;
  }
}
