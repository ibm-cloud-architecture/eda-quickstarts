package it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import ibm.eda.demo.ordermgr.infra.api.dto.OrderDTO;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class OrderResourceTest { 

    String basicURL = "/api/v1/orders";
    
    @Test
    public void testOrdersEndpoint() {
      System.out.println(System.getProperty("KAFKA_BOOTSTRAP_SERVERS"));
       Response rep = given()
          .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
          .when().get(basicURL)
          .then()
             .statusCode(200)
             .contentType(ContentType.JSON)
            .extract()
            .response();
            System.out.println(rep.jsonPath().prettyPrint());
            OrderDTO[] orders = rep.body().as(OrderDTO[].class);
            Assertions.assertTrue(orders.length >= 2);
    }

}