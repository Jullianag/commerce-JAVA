package com.meusprojetos.commerce.controllers;


import com.meusprojetos.commerce.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class OrderControllerRA {

    private String clientUser, clientPassword, adminUser, adminPassword;
    private String clientToken, adminToken, invalidToken;

    private Long existingOrderId, nonExistingOrderId;

    private Map<String, List<Map<String, Object>>> postOrderInstance;

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

        Map<String, Object> item1 = new HashMap<>();
        item1.put("productId", 1);
        item1.put("quantity", 2);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("productId", 5);
        item2.put("quantity", 1);

        List<Map<String, Object>> itemInstance = new ArrayList<>();
        itemInstance.add(item1);
        itemInstance.add(item2);

        postOrderInstance = new HashMap<>();
        postOrderInstance.put("items", itemInstance);
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
    public void findByIdShouldReturnForbiddenWhenIdExistsAndClientLoggedAndOrderDoesNotBelongUser() throws JSONException {

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
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() throws JSONException {

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
    public void findByIdShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() throws JSONException {

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(401);
    }

    @Test
    public void insertShouldReturnOrderCreatedWhenClientLogged() throws JSONException {

        JSONObject newOrder = new JSONObject(postOrderInstance);

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newOrder)
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .body("status", equalTo("WAITING_PAYMENT"))
                .body("client.name", equalTo("Maria Brown"))
                .body("items.name", hasItems("The Lord of the Rings", "Rails for Dummies"))
                .body("items.price", hasItems(90.5F, 100.99F))
                .body("total", is(281.99F));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenClientLoggedAndOrderHasNoItem() throws JSONException {

        postOrderInstance.put("items", null);
        JSONObject newOrder = new JSONObject(postOrderInstance);

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newOrder)
                .when()
                .post("/orders")
                .then()
                .statusCode(422)
                .body("errors.fieldName[0]", equalTo("items"))
                .body("errors.message[0]", equalTo("A lista não pode ser vazia"));
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() throws JSONException {

        JSONObject newOrder = new JSONObject(postOrderInstance);

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newOrder)
                .when()
                .post("/orders")
                .then()
                .statusCode(401);

    }

}
