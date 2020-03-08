package me.cuenca.price.port.adapter.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import me.cuenca.price.PriceModule;
import me.cuenca.price.domain.model.eod.Quote;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class PricesPayloadTest {

  @Autowired
  private ObjectMapper mapper = new PriceModule().mapper();

  @Test
  void shouldDeserializeCents() throws Exception {
    String json = "{\"symbol\":\"FR123456\",\"prices\":[{\"timepoint\":\"1995-01-02\",\"quote\":{\"open\":307.27,\"high\":305.28,\"low\":305.27,\"close\":304.28,\"volume\":10000}}]}";

    PricesPayload prices = mapper.readValue(json, PricesPayload.class);

    Quote quote = prices.prices.get(0).getQuote();
    assertEquals(ImmutableList.of(307270,305280,305270,304280,10000L), quote.toDocument());
  }

  @Test
  void shouldDeserializeBigVolume() throws Exception {
    String json = "{\"symbol\":\"FR123456\",\"prices\":[{\"timepoint\":\"1995-01-02\",\"quote\":{\"open\":307.27,\"high\":305.28,\"low\":305.27,\"close\":304.28,\"volume\":10000000000000000}}]}";

    PricesPayload prices = mapper.readValue(json, PricesPayload.class);

    Quote quote = prices.prices.get(0).getQuote();
    assertEquals(ImmutableList.of(307270,305280,305270,304280,10000000000000000L), quote.toDocument());
  }

}