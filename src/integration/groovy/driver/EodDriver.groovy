package driver

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.time.LocalDate
import java.time.format.DateTimeFormatter

public class EodDriver {

  String server

  String givenAStock() {
    return "FOO"
  }
  String givenAnExchange() {
    return "EXCH"
  }

//  Eods generateEods(String aStock, String anExchange, int aYear) {
//    Eods eods = new Eods(symbol: aStock)
//
//      LocalDate date = LocalDate.of(aYear, 1, 1)
//    double price = 1500
//    int volume = 12000
//
//    while (date.getYear() == aYear) {
//      eods.prices << new PriceElem(timepoint : date, quote : new Quote(open : price, high : price + 2, low : price -1, close : price, volume : volume))
//      date = date.plusDays(1)
//      price /= 1.01
//      volume + 100
//    }
//    return eods
//  }

  Map generateEods(String aStock, String anExchange, int aYear) {
    def eods = [ symbol: aStock, prices : [] ]

    LocalDate date = LocalDate.of(aYear, 1, 1)
    double price = 1500
    int volume = 12000

    while (date.getYear() == aYear) {
      eods["prices"] << [timepoint: date.format(DateTimeFormatter.ISO_DATE), quote: [open: price, high: price + 2, low: price -1, close: price, volume: volume]]

      date = date.plusDays(1)
      price = Math.round(price / 0.0101) / 100.0
      volume + 100
    }
    return eods
  }

  Map generateEods(String aStock, String anExchange, int aYear, int nb) {
    def eods = [ symbol: aStock, prices : [] ]

    LocalDate date = LocalDate.of(aYear, 1, 1)
    double price = 1500
    int volume = 12000
    int count = 0
    while (date.getYear() == aYear && count++ < nb) {
      eods["prices"] << [timepoint: date.format(DateTimeFormatter.ISO_DATE), quote: [open: price, high: price + 2, low: price -1, close: price, volume: volume]]

      date = date.plusDays(1)
      price = Math.round(price / 0.0101) / 100.0
      volume + 100
    }
    return eods
  }

  def whenStore(String aStock, String anExchange, int aYear, Map eods) {
    def json = new JsonBuilder(eods).toString()
  //  println json
    HttpURLConnection connection = new URL("http://" + server + "/symbols/" + aStock + "/prices/" + aYear).openConnection()
    connection.requestMethod = "PUT"
    connection.setRequestProperty("Content-Type", "application/json")
    connection.doOutput = true
    connection.with {
      outputStream.withWriter { outputStream ->
        outputStream <<  json
      }
    }
    println "Server returned " + connection.responseCode
  }

  def eod(String aStock, String anExchange, int aYear) {
    JsonSlurper jsonSlurper = new JsonSlurper()
    return jsonSlurper.parse(new URL("http://" + server + "/symbols/" + aStock + "/prices/" + aYear))
  }

  boolean assertEqualsEods(Map eod1, Map eod2) {
    assert eod1.symbol == eod2.symbol

    Map<String, Map> perDate = [:]
    for (Map price : eod2.prices) {
      perDate.put(price.timepoint, price)
    }

    for (def price : eod1.prices) {
      try {
        assert price == perDate.get(price["timepoint"])
      } catch(AssertionError e) {
        println price
        println perDate.get(price["timepoint"])
        throw e
      }
    }
  }
}