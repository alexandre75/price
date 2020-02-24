package me.cuenca.price.domain.service;

public interface SymbolMapper {
  /**
   * Returns static data about the given QMCI symbol
   *
   * @param symbol a symbol, for instance BB:CA
   */
  Symbol map(String symbol);
}
