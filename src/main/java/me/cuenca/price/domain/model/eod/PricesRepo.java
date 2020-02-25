package me.cuenca.price.domain.model.eod;

import me.cuenca.price.domain.service.Symbol;

import java.util.NoSuchElementException;
import java.util.Optional;

public interface PricesRepo {
  /**
   *
   * @param price
   * @return the new version
   */
  void add(Price price);

  default Prices of(Symbol symbol, int year) {
    return of(symbol, year, null).get();
  }

  /**
   * Returns prices ofr the given parameters.
   * If version is not modified, return empty;
   * @param symbol
   * @param year
   * @param version
   * @return
   * @throws NoSuchElementException if the document does not exists
   */
  Optional<Prices> of(Symbol symbol, int year, String version) throws NoSuchElementException;

  /**
   * Sets the version in the @code prices
   * @param symbol
   * @param prices
   * @return true if the new otherwise false
   */
  boolean addYear(Symbol symbol, int year, Prices prices);
}
