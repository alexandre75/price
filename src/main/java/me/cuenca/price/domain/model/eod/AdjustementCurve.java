package me.cuenca.price.domain.model.eod;

import me.cuenca.price.domain.model.adjust.CorpEvent;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Comparator.*;

@Immutable
public final class AdjustementCurve {
  private List<CorpEvent> events;

  private AdjustementCurve(List<CorpEvent> events) {
    assert  events != null;
    this.events = events;
  }

  public static AdjustementCurve from(Collection<CorpEvent> events) {
    List<CorpEvent> evts = new ArrayList<>(events);
    evts.sort(comparing((CorpEvent corpEvent) -> corpEvent.date()).reversed());
    return new AdjustementCurve(evts);
  }

  public Price adjust(Price price) {
    Quote.MutableQuote mutQuote = price.getQuote().newMutableQuote();
    for (CorpEvent corpEvent : events) {
      if (price.getTimepoint().isAfter(corpEvent.date())) {
        break;
      }

      mutQuote.adjust(corpEvent);
    }
    return price.withQuote(mutQuote.toQuote());
  }
}
