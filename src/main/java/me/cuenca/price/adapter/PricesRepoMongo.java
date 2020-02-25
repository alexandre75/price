package me.cuenca.price.adapter;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import me.cuenca.price.domain.model.eod.Prices;
import me.cuenca.price.domain.service.Symbol;
import me.cuenca.price.domain.model.eod.Price;
import me.cuenca.price.domain.model.eod.PricesRepo;
import me.cuenca.price.domain.model.eod.Quote;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.mongodb.client.model.Filters.*;

@Service
public class PricesRepoMongo implements PricesRepo {
  private static final ReplaceOptions OPTIONS = new ReplaceOptions();

  static {
    OPTIONS.upsert(true);
  }

  private MongoCollection<Document> prices;

  public PricesRepoMongo(MongoClient client) {
    prices = client.getDatabase("Price").getCollection("prices");
    prices.createIndex(new Document("isin", 1).append("excode", 1).append("year", 1));
  }

  @Override
  public void add(Price price) {
    Document updates = new Document("$set", new Document(serie(price), price.getQuote().toDocument()))
            .append("$inc", new Document("version", 1));
    UpdateResult updateResult = prices.updateOne(filter(price), updates);
    if (updateResult.getMatchedCount() == 0) {
      prices.insertOne(newSerie(price));
    }
  }

  private Document newSerie(Price price) {
    return new Document("isin", price.getInstrument().getIsin())
            .append("excode", price.getExchange().getExcode())
            .append("year", price.getTimepoint().getYear())
            .append("version", 1)
            .append("eods", new Document(price.getTimepoint().getMonth().name(),
                    new Document(Integer.toString(price.getTimepoint().getDayOfMonth()), price.getQuote().toDocument())));
  }

  @Override
  public Optional<Prices> of(Symbol symbol, int i, @Nullable String version) {
    List<Price> quotes;
    Document price = prices.find(filter(new Price(LocalDate.of(i, 1, 1), null,
            symbol.getInstrument(), symbol.getExchangeId()))).first();
    if (price == null) {
      throw new NoSuchElementException("No EOD found");
    }
    String dbVersion = price.getInteger("version").toString();
    if (dbVersion.equals(version)) {
      return Optional.empty();
    } else {
      List<Price> res = new ArrayList<>();
      Document eods = price.get("eods", Document.class);
      eods.forEach((month, days) -> {
        ((Document) days).forEach((day, quote) -> {
          res.add(new Price(LocalDate.of(i, Month.valueOf(month), Integer.parseInt(day)),
                  Quote.from((List<Integer>) quote), symbol.getInstrument(), symbol.getExchangeId()));
        });
      });
      quotes = res;
    }
    return Optional.of(new Prices(symbol, i, quotes, dbVersion));
  }

  @Override
  public boolean addYear(Symbol symbol, int year, Prices quotes) {
    try {
      PricesTranslator translator = new PricesTranslator(symbol, year, quotes.prices());
      Document replacement = translator.toDocument();
      UpdateResult updateResult = prices.replaceOne(filter(symbol, translator.getYear()),
              replacement, OPTIONS);
      quotes.setVersion(replacement.getInteger("version").toString());
      return updateResult.getMatchedCount() == 1;
    } catch(IllegalStateException e) {
      throw new IllegalArgumentException(symbol + "," + year, e);
    }
  }

  private Bson filter(Price price) {
    return and(eq("isin", price.getInstrument().getIsin()),
            eq("excode", price.getExchange().getExcode()),
            eq("year", price.getTimepoint().getYear()));
  }

  private Bson filter(Symbol symbol, int year) {
    return and(eq("isin", symbol.getInstrument().getIsin()),
            eq("excode", symbol.getExchangeId().getExcode()),
            eq("year", year));
  }

  private String serie(Price price) {
    return "eods." + price.getTimepoint().getMonth().name() + "." + price.getTimepoint().getDayOfMonth();
  }
}
