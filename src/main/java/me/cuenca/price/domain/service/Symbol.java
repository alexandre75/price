package me.cuenca.price.domain.service;

import com.google.common.base.Preconditions;
import me.cuenca.price.domain.model.ExchangeId;
import me.cuenca.price.domain.model.Instrument;

import static com.google.common.base.Preconditions.*;
import static java.util.Objects.requireNonNull;

public final class Symbol {
  private Instrument instrument;
  private ExchangeId exchangeId;

  public Symbol(Instrument instrument, ExchangeId exchangeId) {
    this.instrument = instrument;
    this.exchangeId = exchangeId;
  }

  public static Symbol of(String excode, String isin) {
    checkArgument(!excode.isBlank());
    checkArgument(!isin.isBlank());

    return new Symbol(Instrument.isin(isin), new ExchangeId(excode));
  }

  public Instrument getInstrument() {
    return instrument;
  }

  public ExchangeId getExchangeId() {
    return exchangeId;
  }
}
