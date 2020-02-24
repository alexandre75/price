package me.cuenca.price.domain.model.adjust;

import java.time.LocalDate;

public interface CorpEvent {
  LocalDate date();

  double adjustPrice(double high);

  double adjustVolume(double volume);
}
