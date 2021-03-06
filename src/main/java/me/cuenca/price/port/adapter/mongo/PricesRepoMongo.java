package me.cuenca.price.port.adapter.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import me.cuenca.price.MongoConf;
import me.cuenca.price.domain.model.eod.Prices;
import me.cuenca.price.domain.service.Symbol;
import me.cuenca.price.domain.model.eod.Price;
import me.cuenca.price.domain.model.eod.PricesRepo;
import me.cuenca.price.domain.model.eod.Quote;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
  private static Logger logger = LoggerFactory.getLogger(PricesRepoMongo.class);

  static {
    OPTIONS.upsert(true);
  }

  private MongoCollection<Document> prices;

  @Autowired
  public PricesRepoMongo(MongoClient client, MongoConf conf) {
    logger.info("Mongo - Connection to " +  conf.getHosts());
    logger.info("Using database : " + conf.getDb());
    prices = client.getDatabase(conf.getDb()).getCollection("prices");
    prices.createIndex(new Document("isin", 1).append("mic", 1).append("year", 1));
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
            .append("mic", price.getExchange().getMic())
            .append("year", price.getTimepoint().getYear())
            .append("version", 1)
            .append("eods", new Document(price.getTimepoint().getMonth().name(),
                    new Document(Integer.toString(price.getTimepoint().getDayOfMonth()), price.getQuote().toDocument())));
  }

  @Override
  public Optional<Prices> of(Symbol symbol, int year, @Nullable String version) {
    List<Price> quotes;
    Document price = prices.find(filter(symbol, year)).first();
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
          @SuppressWarnings("unchecked") List<Integer> quoteList = (List<Integer>) quote;
          res.add(new Price(LocalDate.of(year, Month.valueOf(month), Integer.parseInt(day)),
                  Quote.from(quoteList), symbol));
        });
      });
      quotes = res;
    }
    return Optional.of(new Prices(symbol, year, quotes, dbVersion));
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
            eq("mic", price.getExchange().getMic()),
            eq("year", price.getTimepoint().getYear()));
  }

  private Bson filter(Symbol symbol, int year) {
    return and(eq("isin", symbol.getInstrument().getIsin()),
            eq("mic", symbol.getExchangeId().getMic()),
            eq("year", year));
  }

  private String serie(Price price) {
    return "eods." + price.getTimepoint().getMonth().name() + "." + price.getTimepoint().getDayOfMonth();
  }
}
