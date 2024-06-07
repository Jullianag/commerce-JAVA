package com.meusprojetos.commerce.controllers;


import com.meusprojetos.commerce.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class OrderControllerRA {

    private String clientUser, clientPassword, adminUser, adminPassword;
    private String clientToken, adminToken, invalidToken;

    private Long existingOrderId, nonExistingOrderId;

    @BeforeEach
    public void setUp() throws Exception {

        baseURI = "http://localhost";

        existingOrderId = 1L;
        nonExistingOrderId = 100L;

        clientUser = "maria@gmail.com";
        clientPassword = "123456";

        adminUser = "alex@gmail.com";
        adminPassword = "123456";

        clientToken = TokenUtil.obtainAccessToken(clientUser, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUser, adminPassword);
        invalidToken = adminToken + "xpto";
    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndAdminLogged() {

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("moment", equalTo("2022-07-25T13:00:00Z"))
                .body("status", equalTo("PAID"))
                .body("client.id", is(1))
                .body("client.name", equalTo("Maria Brown"))
                .body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
                .body("items.name", hasItems("The Lord of the Rings", "Macbook Pro"))
                .body("items.price", hasItems(90.5F, 1250.0F))
                .body("total", is(1431.0F));
    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndAClientLogged() {

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("moment", equalTo("2022-07-25T13:00:00Z"))
                .body("status", equalTo("PAID"))
                .body("client.id", is(1))
                .body("client.name", equalTo("Maria Brown"))
                .body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
                .body("items.name", hasItems("The Lord of the Rings", "Macbook Pro"))
                .body("items.price", hasItems(90.5F, 1250.0F))
                .body("total", is(1431.0F));

    }

    @Test
    public void findByIdShouldReturnForbiddenWhenIdExistsAndClientLoggedAndOrderDoesNotBelongUser() {

        Long otherOrderId = 2L;

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}", otherOrderId)
                .then()
                .statusCode(403);
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() {

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}", nonExistingOrderId)
                .then()
                .statusCode(404);
    }

    @Test
    public void findByIdShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() {

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(401);
    }

}
