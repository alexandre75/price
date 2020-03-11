package me.cuenca.price.domain.model.eod;

import me.cuenca.price.domain.model.ExchangeId;
import me.cuenca.price.domain.model.Instrument;
import me.cuenca.price.domain.service.Symbol;

import java.time.LocalDate;

public class Price {
  private LocalDate timepoint;
  private Quote quote;

  private transient Symbol symbol;

  private Price() {
  }

  public Price(LocalDate timepoint, Quote quote, Symbol symbol) {
    this.timepoint = timepoint;
    this.quote = quote;
    this.symbol = symbol;
  }

  public LocalDate getTimepoint() {
    return timepoint;
  }

  public Quote getQuote() {
    return quote;
  }

  public Instrument getInstrument() {
    return symbol.getInstrument();
  }

  public ExchangeId getExchange() {
    return symbol.getExchangeId();
  }

  public void update(Symbol sym) {
    symbol = sym;
  }

  public Price withQuote(Quote toQuote) {
    return new Price(timepoint, toQuote, symbol);
  }

  @Override
  public String toString() {
    return "Price{" +
            "timepoint=" + timepoint +
            ", quote=" + quote +
            ", symbol=" + symbol +
            '}';
  }
}
