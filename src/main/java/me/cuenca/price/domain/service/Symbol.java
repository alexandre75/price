package me.cuenca.price.domain.service;

import me.cuenca.price.domain.model.ExchangeId;
import me.cuenca.price.domain.model.Instrument;

import static java.util.Objects.requireNonNull;

public final class Symbol {
  private Instrument instrument;
  private ExchangeId exchangeId;

  public Symbol(Instrument instrument, ExchangeId exchangeId) {
    this.instrument = instrument;
    this.exchangeId = exchangeId;
  }

  public static Symbol of(String excode, String isin) {
    requireNonNull(excode);
    requireNonNull(isin);
    return new Symbol(Instrument.isin(isin), new ExchangeId(excode));
  }

  public Instrument getInstrument() {
    return instrument;
  }

  public ExchangeId getExchangeId() {
    return exchangeId;
  }
}
