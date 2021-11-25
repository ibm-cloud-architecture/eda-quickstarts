package it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TestVersionResource {
    String basicURL = "/api/v1/orders";


    @Test
    @Order(1)
    public void validateVersionIsSet(){
        given()
          .when().get( basicURL + "/version")
          .then()
             .statusCode(200)
             .body(containsString("0.0"));
    }
}
