package me.cuenca.price.adapter;

import me.cuenca.price.domain.model.eod.Price;

import java.util.List;

public class PricesPayload {
  public String symbol;
  public List<Price> prices;
}
