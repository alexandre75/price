package me.cuenca.price.port.adapter.rest;

import com.google.common.base.Charsets;
import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import me.cuenca.price.domain.model.eod.AdjustementCurve;
import me.cuenca.price.domain.model.eod.Price;
import me.cuenca.price.domain.model.eod.Prices;
import me.cuenca.price.domain.model.eod.PricesRepo;
import me.cuenca.price.domain.service.SymbolMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Path("symbols")
public class PriceResource {
  private static final CacheControl FIVE_MN_CACHE = CacheControl.valueOf("max-age=300");
  private static Logger logger = LoggerFactory.getLogger("me.cuenca.price.adapter");

  private Gson serializer;

  private PricesRepo pricesRepo;
  private SymbolMapper mapper;

  @Autowired
  public PriceResource(PricesRepo pricesRepo, SymbolMapper mapper) {
    this.pricesRepo = pricesRepo;
    this.mapper = mapper;

    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
      @Override
      public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
      }
    });
    serializer = builder.create();
  }

  @Path("{symbol}/prices/{year}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public Response eods(@PathParam("symbol") String symbol, @PathParam("year") int year, @HeaderParam("If-None-Match") String etag) {
    logger.info("GET " + symbol + "/" + year);

    try {
      Optional<Prices> prices = pricesRepo.of(mapper.map(symbol), year, etag);
      if (prices.isEmpty()) {
        return Response.notModified().header("etag", etag).build();
      }

      StreamingOutput res = new StreamingOutput() {
        @Override
        public void write(OutputStream output) throws IOException, WebApplicationException {
          JsonWriter out = new JsonWriter(new OutputStreamWriter(output, Charsets.UTF_8));
          out.beginObject();
          out.name("symbol").value(symbol);
          out.name("prices").beginArray();
          prices.get().prices().forEach(price -> serializer.toJson(price, Price.class, out));
          out.endArray();
          out.endObject();
          out.close();
        }
      };
      return Response.ok(res).header("ETag", prices.get().version())
              .cacheControl(FIVE_MN_CACHE)
              .build();
    } catch(NoSuchElementException e) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @Path("{symbol}/adj-prices/{year}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public Response adjEods(@PathParam("symbol") String symbol, @PathParam("year") int year, @HeaderParam("If-None-Match") String etag) {
    logger.info("GET " + symbol + "/" + year);

    try {
      Prices prices = pricesRepo.of(mapper.map(symbol), year);
      AdjustementCurve aCurve = null;
      StreamingOutput res = new StreamingOutput() {
        @Override
        public void write(OutputStream output) throws IOException, WebApplicationException {
          JsonWriter out = new JsonWriter(new OutputStreamWriter(output, Charsets.UTF_8));
          out.beginObject();
          out.name("symbol").value(symbol);
          out.name("prices").beginArray();
          prices.prices().stream()
                               .map(aCurve::adjust)
                               .forEach(price -> serializer.toJson(price, Price.class, out));
          out.endArray();
          out.endObject();
          out.close();
        }
      };
      return Response.ok(res).header("ETag", prices.version())
              .cacheControl(FIVE_MN_CACHE)
              .build();
    } catch(NoSuchElementException e) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @Path("{symbol}/prices/{year}")
  @Consumes(MediaType.APPLICATION_JSON)
  @PUT
  public Response setPrices(@PathParam("symbol") String symbol, @PathParam("year") int year, PricesPayload prices) {
    logger.info("PUT");
    Prices newPrices = new Prices(prices.prices);
    if (!pricesRepo.addYear(mapper.map(symbol), year, newPrices)) {
      return Response.noContent().header("etag", newPrices.version()).build();
    } else {
      return Response.created(getLocation(symbol, year))
              .header("etag", newPrices.version()).build();
    }
  }

  private static URI getLocation(@PathParam("symbol") String symbol, @PathParam("year") int year) {
    return URI.create("/symbols/" + symbol.toLowerCase() + "/prices/" + year);
  }

  @Path("{symbol}/prices/{year}")
  @Consumes(MediaType.APPLICATION_JSON)
  @PATCH
  public Response addPrices(@PathParam("symbol") String symbol, @PathParam("year") int year, Price price) {
    logger.info("PATCH");

    price.update(mapper.map(symbol));
    pricesRepo.add(price);

    return Response.noContent().header("Location", getLocation(symbol, year)).build();
  }
}
