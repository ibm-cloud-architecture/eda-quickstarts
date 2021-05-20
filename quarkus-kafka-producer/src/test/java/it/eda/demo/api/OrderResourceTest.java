package it.eda.demo.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.eda.BasicIT;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class OrderResourceTest extends BasicIT { 

    String basicURL = "/api/orders/v1";

    @Test
    @Order(1)
    public void validateVersionIsSet(){
        given()
          .when().get( basicURL + "/version")
          .then()
             .statusCode(200)
             .body(containsString("0.0"));
    }
    
    @Test
    public void testOrdersEndpoint() {
       Response rep = given()
          .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
          .when().get(basicURL)
          .then()
             .statusCode(200)
             .contentType(ContentType.JSON)
            .extract()
            .response();
            System.out.println(rep.jsonPath().prettyPrint());
            Order[] orders = rep.body().as(Order[].class);
            Assertions.assertTrue(orders.length > 2);
    }

}