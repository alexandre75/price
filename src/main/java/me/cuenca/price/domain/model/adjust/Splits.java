package me.cuenca.price.domain.model.adjust;

import java.time.LocalDate;

public class Splits implements CorpEvent {
  private double ratio;

  private LocalDate date;

  public Splits(double ratio) {
    if (ratio == 0) {
      throw new ArithmeticException("ratio = 0");
    }
    this.ratio = ratio;
  }

  @Override
  public LocalDate date() {
    return date;
  }

  @Override
  public double adjustPrice(double price) {
    return price * ratio;
  }

  @Override
  public double adjustVolume(double volume) {
    return volume / ratio;
  }

  public static Splits xForY(int x, int y) {
    return new Splits(x / (double) y);
  }

  @Override
  public String toString() {
    return "Splits{" +
            "ratio=" + ratio +
            ", date=" + date +
            '}';
  }
}
