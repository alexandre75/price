package me.cuenca.price.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.cuenca.price.PriceModule;
import me.cuenca.price.domain.model.ExchangeId;
import me.cuenca.price.domain.model.Instrument;
import me.cuenca.price.domain.model.eod.Price;
import me.cuenca.price.domain.model.eod.Prices;
import me.cuenca.price.domain.model.eod.PricesRepo;
import me.cuenca.price.domain.service.Symbol;
import me.cuenca.price.domain.service.SymbolMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

public class PriceResourceTest {
  private static final String JSON;

  static {
    try {
      JSON = new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("prices.json").toURI())));
    } catch (URISyntaxException| IOException e) {
      throw new AssertionError();
    }
  }

  private Map<LocalDate, Price> refPrices;
  private PricesPayload refValues;

  private PriceResource subject;

  @Mock
  private PricesRepo pricesRepo;
  private SymbolMapper symMapper = new PriceModule().symbolMapper();
  private ObjectMapper objectMapper = new PriceModule().mapper();

  @BeforeEach
  void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    refValues = objectMapper.readValue(JSON, PricesPayload.class);
    refPrices = new HashMap<>();
    refValues.prices.forEach(price -> refPrices.put(price.getTimepoint(), price));

    subject = new PriceResource(pricesRepo, symMapper);
  }

  @Test
  void shouldThrow404() throws Exception {
    given(pricesRepo.of(any(), anyInt(), any())).willThrow(NoSuchElementException.class);

    Response response = subject.eods("FR123456", 2001, "");

    assertThat(response.getStatus(), is(404));
  }

  @Test
  void shouldGetAYear() throws Exception {
    when(pricesRepo.of(any(), eq(2001), isNull()))
            .thenReturn(Optional.of(new Prices(symbol("FR123456"), 1995, refValues.prices, "sdf")));

    Response response = subject.eods("FR123456", 2001, null);

    assertThat(response.getStatus(), is(200));
    StreamingOutput prices = (StreamingOutput) response.getEntity();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    prices.write(out);
    PricesPayload res = objectMapper.readValue(out.toByteArray(), PricesPayload.class);

    assertThat(res.prices.size(), is(refPrices.size()));
    for (Price price : res.prices) {
      assertThat(price.getQuote(), is(refPrices.get(price.getTimepoint()).getQuote()));
    }
  }

  @Test
  void shouldGet304WhenUnmodified() {
    given(pricesRepo.of(any(), eq(2001), eq("123")))
            .willReturn(Optional.empty());

    Response response = subject.eods("FR123456", 2001, "123");

    assertThat(response.getStatus(), is(304));
  }

  private Symbol symbol(String sym) {
    return new Symbol(new Instrument(sym), new ExchangeId("NYE"));
  }

//  @Test
//  void shouldSetFullYear() {
//    PricesPayload payload = new PricesPayload("TEST", readValue.prices);
//    this.restTemplate.put("/symbols/TEST/prices/1995", payload);
//
//    ResponseEntity<PricesPayload> entity = this.restTemplate.getForEntity("/symbols/TEST/prices/1995", PricesPayload.class);
//
//    assertThat(entity.getStatusCode(), is(HttpStatus.OK));
//    assertThat(entity.getBody().prices.size(), is(readValue.prices.size()));
//
//    Map<LocalDate, Quote> received = new HashMap<>();
//    entity.getBody().prices.forEach(price -> received.put(price.getTimepoint(), price.getQuote()));
//
//    for (Price price : readValue.prices) {
//      assertThat(price.getQuote(), is(received.get(price.getTimepoint())));
//    }
//  }
//
//  @Test
//  void canSetOneEod() {
//    Quote quote = new Quote(1000, 1200, 900, 950, 1_000_000);
//    Price price = new Price(LocalDate.of(1999, 7,26), quote, new Instrument("PATCH:TEST"), new ExchangeId("TEST"));
//
//    String str = this.restTemplate.patchForObject("/symbols/TEST/prices/1999", price, String.class);
//
//    ResponseEntity<PricesPayload> entity = this.restTemplate.getForEntity("/symbols/TEST/prices/1999", PricesPayload.class);
//
//    assertThat(entity.getBody().prices.size(), is(1));
//    assertThat(entity.getBody().prices.get(0), is(quote));
//  }
}