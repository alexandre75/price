package me.cuenca.price.domain.model.eod;

import com.google.common.base.Preconditions;
import io.reactivex.Flowable;
import me.cuenca.price.domain.service.Symbol;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class Prices {
  private Symbol symbol;
  private int year;
  private List<Price> prices;
  private String version;


  public Prices(Symbol sym, int year, List<Price> prices, String version) {
    symbol = sym;
    this.year = year;
    this.version = version;
    this.prices = requireNonNull(prices);
  }

  public Prices(List<Price> prices) {
    this.prices = prices;
  }

  public List<Price> prices(){
    Preconditions.checkState(prices != null);

    return prices;
  }

  public String version(){
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
