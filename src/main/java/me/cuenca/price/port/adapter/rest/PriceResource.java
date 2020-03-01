package me.cuenca.price.port.adapter.rest;

import com.google.common.base.Charsets;
import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import me.cuenca.price.domain.model.eod.Price;
import me.cuenca.price.domain.model.eod.Prices;
import me.cuenca.price.domain.model.eod.PricesRepo;
import me.cuenca.price.domain.service.Symbol;
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

/**
 * Services to GET/SET raw prices.
 *
 * Prices's primary key is ISIN,excode because these are the most robust ids
 *
 * Isin could be easily replaced by something else if the Isin is not robust for a provider. For instance TSX
 */
@Service
@Path("prices")
public class PriceResource {
  private static final CacheControl FIVE_MN_CACHE = CacheControl.valueOf("max-age=300");
  private static Logger logger = LoggerFactory.getLogger("me.cuenca.price.adapter");

  private Gson serializer;

  private PricesRepo pricesRepo;

  @Autowired
  public PriceResource(PricesRepo pricesRepo) {
    this.pricesRepo = pricesRepo;

    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
      @Override
      public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
      }
    });
    serializer = builder.create();
  }

  @Path("{excode}/{isin}/{year}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public Response eods(@PathParam("excode") String excode, @PathParam("isin") String isin, @PathParam("year") int year
          , @HeaderParam("If-None-Match") String etag) {
    logger.info("GET /price/" + excode + "/" + isin + "/" + year);

    try {
      Optional<Prices> prices = pricesRepo.of(Symbol.of(excode, isin), year, etag);
      if (prices.isEmpty()) {
        return Response.notModified().header("etag", etag).build();
      }

      StreamingOutput res = new StreamingOutput() {
        @Override
        public void write(OutputStream output) throws IOException, WebApplicationException {
          JsonWriter out = new JsonWriter(new OutputStreamWriter(output, Charsets.UTF_8));
          out.beginObject();
          out.name("isin").value(isin);
          out.name("excode").value(excode);
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

  @Path("{excode}/{isin}/{year}")
  @Consumes(MediaType.APPLICATION_JSON)
  @PUT
  public Response setPrices(@PathParam("excode") String excode, @PathParam("isin") String isin, @PathParam("year") int year, PricesPayload prices) {
    logger.info("PUT /price/" + excode + "/" + isin + "/" + year);
    Prices newPrices = new Prices(prices.prices);
    if (!pricesRepo.addYear(Symbol.of(excode, isin), year, newPrices)) {
      return Response.noContent().header("etag", newPrices.version()).build();
    } else {
      return Response.created(getLocation(excode, isin, year))
              .header("etag", newPrices.version()).build();
    }
  }

  private static URI getLocation(String excode, @PathParam("symbol") String isin, @PathParam("year") int year) {
    return URI.create("/price/" + excode.toLowerCase() + "/" + isin.toLowerCase() + "/" + year);
  }

  @Path("{symbol}/prices/{year}")
  @Consumes(MediaType.APPLICATION_JSON)
  @PATCH
  public Response addPrices(@PathParam("excode") String excode, @PathParam("isin") String isin, @PathParam("year") int year, Price price) {
    logger.info("PATCH");

    price.update(Symbol.of(excode, isin));
    pricesRepo.add(price);

    return Response.noContent().header("Location", getLocation(excode, isin, year)).build();
  }
}
