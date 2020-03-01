package me.cuenca.price.port.adapter.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.cuenca.price.domain.model.eod.Price;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PricesPayload {
  public List<Price> prices;

  private PricesPayload() {}

  public PricesPayload(List<Price> prices) {
    this.prices = prices;
  }
}
