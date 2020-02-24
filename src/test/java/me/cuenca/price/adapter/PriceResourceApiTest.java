package me.cuenca.price.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import java.io.InputStreamReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PriceResourceApiTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @BeforeAll
  public static void setUp() throws Exception {
    //Gson gson = new Gson();

    //PricesPayload payload = gson.fromJson(new InputStreamReader(ClassLoader.getSystemResourceAsStream("prices.json")), PricesPayload.class);

    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    mapper.registerModule(module);

    PricesPayload readValue = mapper.readValue(ClassLoader.getSystemResourceAsStream("prices.json"), PricesPayload.class);
  }

  @Test
  void testEods() {
    ResponseEntity<String> entity = this.restTemplate.getForEntity("/symbols/bb-ca/prices/2019", String.class);

    assertThat(entity.getStatusCode(), is(HttpStatus.OK));
  }

  @Test
  void test() {
    ResponseEntity<String> entity = this.restTemplate.getForEntity("/hello", String.class);

    assertThat(entity.getStatusCode(), is(HttpStatus.OK));
  }
}