package me.cuenca.price.domain.model.adjust;

import java.time.LocalDate;

public class Dividend implements CorpEvent {
  private double amount;
  private LocalDate exDate;

  @Override
  public LocalDate date() {
    return exDate;
  }

  @Override
  public double adjustPrice(double price) {
    return amount + price;
  }

  @Override
  public double adjustVolume(double volume) {
    return volume;
  }
}
