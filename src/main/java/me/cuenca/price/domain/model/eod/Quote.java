package me.cuenca.price.domain.model.eod;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import com.google.gson.annotations.JsonAdapter;
import com.mongodb.annotations.Immutable;
import com.mongodb.annotations.NotThreadSafe;
import me.cuenca.price.adapter.MoneyDeserializeJack;
import me.cuenca.price.adapter.MoneyDeserializer;
import me.cuenca.price.domain.model.adjust.CorpEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Immutable
public final class Quote {
  private static Random random = new Random();
  private static double delta = .08 / 250D;
  private static double VOL = .20 / Math.sqrt(250D);

  @JsonDeserialize(using=MoneyDeserializeJack.class)
  @JsonAdapter(MoneyDeserializer.class)
  private int open;

  @JsonDeserialize(using=MoneyDeserializeJack.class)
  @JsonAdapter(MoneyDeserializer.class)
  private int high;

  @JsonDeserialize(using=MoneyDeserializeJack.class)
  @JsonAdapter(MoneyDeserializer.class)
  private int low;

  @JsonDeserialize(using=MoneyDeserializeJack.class)
  @JsonAdapter(MoneyDeserializer.class)
  private int close;

  @JsonDeserialize
  private int volume;

  // for Jackson
  private Quote() {
  }

  public Quote(int open, int high, int low, int close, int volume) {
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
    this.volume = volume;
  }

  public static Quote from(List<Integer> quote) {
    return new Quote(quote.get(0), quote.get(1), quote.get(2), quote.get(3), quote.get(4));
  }

  public List<Integer> toDocument() {
    return Arrays.asList(open, high, low, close, volume);
  }

  public Quote next() {
    return new Quote(close,
            (int) (close + (close * (delta + random.nextGaussian() * VOL))),
            (int) (close + (close * (delta + random.nextGaussian() * VOL))),
            (int) (close + (close * (delta + random.nextGaussian() * VOL))),
            10000);
  }

  public MutableQuote newMutableQuote() {
    return new MutableQuote(this);
  }

  @NotThreadSafe
  public static class MutableQuote {
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;

    private MutableQuote(Quote quote) {
      open = quote.open;
      high = quote.high;
      low = quote.low;
      close = quote.close;
      volume = quote.volume;
    }

    public MutableQuote adjust(CorpEvent event) {
      open = event.adjustPrice(open);
      high = event.adjustPrice(high);
      low = event.adjustPrice(low);
      close = event.adjustPrice(close);

      volume = event.adjustVolume(volume);
      return this;
    }

    public Quote toQuote() {
      return new Quote((int) Math.round(open), (int) Math.round(high), (int) Math.round(low),
                        (int) Math.round(close), (int) Math.round(volume));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Quote quote = (Quote) o;
    return open == quote.open &&
            high == quote.high &&
            low == quote.low &&
            close == quote.close &&
            volume == quote.volume;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(open, high, low, close, volume);
  }

  @Override
  public String toString() {
    return "Quote{" +
            "open=" + open +
            ", high=" + high +
            ", low=" + low +
            ", close=" + close +
            ", volume=" + volume +
            '}';
  }
}
