package driver

import groovy.json.JsonBuilder
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

  String givenAnExchange(String excode) {
    return excode
  }

  Map generateEods(String aStock, String anExchange, int aYear) {
    return generateEods(aStock, anExchange, aYear, 400)
  }

  Map generateEods(String aStock, String anExchange, int aYear, int nb) {
    def eods = [ isin: aStock, prices : [] ]

    LocalDate date = LocalDate.of(aYear, 1, 1)
    double price = 1500
    int volume = 12000
    int count = 0
    while (date.getYear() == aYear && count++ < nb) {
      eods["prices"] << [timepoint: date.format(DateTimeFormatter.ISO_DATE), quote: [open: round(price), high: round(price + 2), low: round(price -1), close: round(price), volume: volume]]

      date = date.plusDays(1)
      price = price / 1.01D
      volume + 100
    }
    return eods
  }

  BigDecimal round(double d) {
    return BigDecimal.valueOf(d).round(2)
  }

  def whenStore(String aStock, String anExchange, int aYear, Map eods) {
    def json = new JsonBuilder(eods).toString()
  //  println json
    HttpURLConnection connection = new URL("http://" + server + "/prices/" + anExchange.toLowerCase() + "/" + aStock.toLowerCase() + "/" + aYear).openConnection()
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
    return jsonSlurper.parse(new URL("http://" + server + "/prices/" + anExchange.toLowerCase() + "/" + aStock.toLowerCase() + "/" + aYear))
  }

  boolean assertEqualsEods(Map eod1, Map eod2) {
    assert eod1.symbol == eod2.symbol
    assert eod1.prices.size == eod2.prices.size

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