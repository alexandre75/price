import driver.EodDriver
import org.junit.Test

class Price {

  @Test
  void shouldStoreAndRetrieveEods() {
    def driver = new EodDriver(server : "pcalex:8080")
    def stock = driver.givenAStock()
    def exchange = driver.givenAnExchange()
    def eods = driver.generateEods(stock, exchange, 2000)

    driver.whenStore(stock, exchange, 2000, eods);

    def retrievedEods = driver.eod(stock, exchange, 2000)

    driver.assertEqualsEods(retrievedEods, eods)
  }
}
