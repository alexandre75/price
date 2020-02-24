package me.cuenca.price.domain.model.eod;

import me.cuenca.price.domain.model.ExchangeId;
import me.cuenca.price.domain.model.Instrument;
import me.cuenca.price.domain.service.Symbol;

import java.time.LocalDate;

public class Price {
  private LocalDate timepoint;
  private Quote quote;
  private transient Instrument instrument;
  private transient ExchangeId exchange;

  private Price() {
  }

  public Price(LocalDate timepoint, Quote quote, Instrument instrument, ExchangeId exchange) {
    this.timepoint = timepoint;
    this.quote = quote;
    this.instrument = instrument;
    this.exchange = exchange;
  }

  public LocalDate getTimepoint() {
    return timepoint;
  }

  public Quote getQuote() {
    return quote;
  }

  public Instrument getInstrument() {
    return instrument;
  }

  public ExchangeId getExchange() {
    return exchange;
  }

  public void update(Symbol sym) {
    instrument = sym.getInstrument();
    exchange = sym.getExchangeId();
  }

  public Price withQuote(Quote toQuote) {
    return new Price(timepoint, toQuote, instrument, exchange);
  }
}
