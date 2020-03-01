package driver

class Eods {
  String symbol
  List prices = []

  boolean equals(o) {
    if (this.is(o)) return true
    if (getClass() != o.class) return false

    Eods eods = (Eods) o

    if (prices.sort(comparing(timepoint)) != eods.prices.sort(comparing(timepoint))) return false
    if (symbol != eods.symbol) return false

    return true
  }

  int hashCode() {
    int result
    result = (symbol != null ? symbol.hashCode() : 0)
    result = 31 * result + (prices != null ? prices.hashCode() : 0)
    return result
  }
}

class PriceElem {
  String timepoint
  Quote quote
}

class Quote {
  double open;
  double high;
  double low;
  double close;
  int volume;
}

