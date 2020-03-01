import driver.EodDriver
import org.junit.Before
import org.junit.Test

class Price {
  EodDriver driver;

  @Before
  void setup() {
    driver = new EodDriver(server : System.getenv("SERVER"))
  }

  @Test
  void shouldStoreAndRetrieveEods() {
    def stock = driver.givenAStock()
    def exchange = driver.givenAnExchange()
    def eods = driver.generateEods(stock, exchange, 2000)

    driver.whenStore(stock, exchange, 2000, eods);

    def retrievedEods = driver.eod(stock, exchange, 2000)

    driver.assertEqualsEods(retrievedEods, eods)
  }

  @Test
  void shouldNotMixEodsFromASymbol() {
    def stock = driver.givenAStock()
    def exchanges = []
    exchanges << driver.givenAnExchange("ex1")
    exchanges << driver.givenAnExchange("ex2")
    exchanges << driver.givenAnExchange("ex3")
    exchanges << driver.givenAnExchange("ex4")

    Map eods = [:]
    int numEod = 200
    for (def exchange : exchanges) {
      for (int year = 1995; year < 2005 ; year++) {
        def eod = driver.generateEods(stock, exchange, year, numEod++)
        eods[[exchange: exchange, year: year]] = eod
        driver.whenStore(stock, exchange, year, eod)
      }
    }

    for (def exchange : exchanges) {
      for (int year = 1995; year < 2005 ; year++) {
        driver.assertEqualsEods(eods[[exchange: exchange, year: year]],  driver.eod(stock, exchange, year))
      }
    }
  }
}
