package me.cuenca.price.domain.service;

import me.cuenca.price.domain.model.ExchangeId;
import me.cuenca.price.domain.model.Instrument;

public final class Symbol {
  private Instrument instrument;
  private ExchangeId exchangeId;

  public Symbol(Instrument instrument, ExchangeId exchangeId) {
    this.instrument = instrument;
    this.exchangeId = exchangeId;
  }

  public Instrument getInstrument() {
    return instrument;
  }

  public ExchangeId getExchangeId() {
    return exchangeId;
  }
}
