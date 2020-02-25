package me.cuenca.price.port.adapter.mongo;

import me.cuenca.price.domain.model.eod.Price;
import me.cuenca.price.domain.service.Symbol;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.google.common.base.Preconditions.checkState;

public class PricesTranslator {
  private static Random random = ThreadLocalRandom.current();

  private Symbol symbol;
  private List<Price> quotes;
  private int year;

  public PricesTranslator(Symbol symbol, int year, List<Price> quotes) {
    this.symbol = symbol;
    this.quotes = quotes;
    this.year = year;
  }

  public Document toDocument() {
    Map<String, Object> priceMap = new HashMap<>();

    for (Price quote : quotes) {
      checkState(quote.getTimepoint().getYear() == year, quote);

      ((Document)priceMap.computeIfAbsent(quote.getTimepoint().getMonth().name(), k -> new Document()))
              .put(Integer.toString(quote.getTimepoint().getDayOfMonth()), quote.getQuote().toDocument());
    }

    return new Document("isin", symbol.getInstrument().getIsin())
            .append("excode", symbol.getExchangeId().getExcode())
            .append("year", year)
            .append("version", 1)
            .append("eods", new Document(priceMap))
            .append("version", random.nextInt());
  }

  public int getYear() {
    return year;
  }
}
