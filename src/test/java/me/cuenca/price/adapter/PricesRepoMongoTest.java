package me.cuenca.price.adapter;

import com.mongodb.MongoClient;
import me.cuenca.price.domain.model.ExchangeId;
import me.cuenca.price.domain.model.Instrument;
import me.cuenca.price.domain.model.eod.Price;
import me.cuenca.price.domain.model.eod.Prices;
import me.cuenca.price.domain.model.eod.Quote;
import me.cuenca.price.domain.service.Symbol;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PricesRepoMongoTest {

  private PricesRepoMongo subject;

  private LocalDate date;

  private Quote quote = new Quote(1000, 1100, 900, 1050, 10000);

  MongoClient client;

  @BeforeEach
  void setup() {
    client = new MongoClient("localhost");
    subject = new PricesRepoMongo(client);

    date = LocalDate.of(1990, 01, 01);
  }

  @Test
  public void shouldLoadAndRetrieve() {
    subject.add(newPrice("TEST"));

    Prices prices = subject.of(new Symbol(new Instrument("TEST"), new ExchangeId("NYE")), 1990);

    assertThat(prices.prices().size()).isEqualTo(1L);

    subject.of(new Symbol(new Instrument("TEST"), new ExchangeId("NYE")), 1990, prices.version());
  }

  @Test
  public void populate() {
    long start = System.nanoTime();
    for (int i = 0 ; i < 10000 ; i++) {
      subject.add(newPrice("FR123456"));
    }
  }

  private Price newPrice(String symbol){
    Price price = new Price(date, quote(), new Instrument(symbol), new ExchangeId("NYE"));
    do {
      date = date.plus(1, ChronoUnit.DAYS);
    } while(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
    return price;
  }

  private Quote quote() {
    quote = quote.next();

    return quote;
  }

  @Test
  void testPut() {
    populate();

    List<Price> prices = subject.of(new Symbol(new Instrument("FR123456"), new ExchangeId("NYE")), 1997)
            .prices();
    int oldSize = prices.size();
    client.getDatabase("Price").getCollection("prices").deleteMany(new Document());

    subject.addYear(new Symbol(new Instrument("FR123456"), new ExchangeId("NYE")), new Prices(prices));

    prices = subject.of(new Symbol(new Instrument("FR123456"), new ExchangeId("NYE")), 1997)
            .prices();
    assertThat(prices.size()).isEqualTo(oldSize);
  }
}