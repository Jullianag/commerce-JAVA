package com.meusprojetos.commerce.controllers;

import com.meusprojetos.commerce.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class ProductControllerRA {

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;
    private Long existingProductId, nonExistingProductId, dependentProductId;
    private String productName;

    private Map<String, Object> postProductInstance;
    private Map<String, Object> putProductInstance;

    @BeforeEach
    void setUp() throws Exception {

        baseURI = "http://localhost:8080";

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";

        adminUsername = "alex@gmail.com";
        adminPassword = "123456";

        postProductInstance = new HashMap<>();
        postProductInstance.put("name", "Meu novo produto");
        postProductInstance.put("description", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim");
        postProductInstance.put("imgUrl", "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
        postProductInstance.put("price", 200.0);

        putProductInstance = new HashMap<>();
        putProductInstance.put("name", "Produto atualizado");
        putProductInstance.put("description", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim");
        putProductInstance.put("imgUrl", "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
        putProductInstance.put("price", 200.0);


        List<Map<String, Object>> categories = new ArrayList<>();

        Map<String, Object> category1 = new HashMap<>();
        category1.put("id", 2);
        Map<String, Object> category2 = new HashMap<>();
        category2.put("id", 3);

        categories.add(category1);
        categories.add(category2);

        postProductInstance.put("categories", categories);
        putProductInstance.put("categories", categories);

        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        invalidToken = adminToken + "xpto";

    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() {

        existingProductId = 4L;

        given()
                .get("/products/{id}", existingProductId)
                .then()
                .statusCode(200)
                .body("id", is(4))
                .body("name", equalTo("PC Gamer"))
                .body("description", equalTo("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
                .body("price", is(1200.0F))
                .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/4-big.jpg"))
                .body("categories.id", hasItem(3))
                .body("categories.name", hasItem("Computadores"));
    }

    @Test
    public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist() {

        nonExistingProductId = 100L;

        given()
                .get("/products/{id}", nonExistingProductId)
                .then()
                .statusCode(404)
                .body("status", is(404))
                .body("error", equalTo(" Resource not found"))
                .body("message", equalTo("Id não encontrado!"))
                .body("path", equalTo("/products/100"));
    }

    @Test
    public void findAllShouldReturnPageProductsWhenProductNameIsEmpty() {

        given()
                .get("/products?page=0")
                .then()
                .statusCode(200)
                .body("content.name", hasItems("Macbook Pro", "PC Gamer Tera"));
    }

    @Test
    public void findAllShouldReturnPageProductsWhenProductNameIsNotEmpty() {

        given()
                .get("/products?name=Macbook Pro")
                .then()
                .statusCode(200)
                .body("content.id[0]", is(3))
                .body("content.name[0]", equalTo("Macbook Pro"))
                .body("content.price[0]", is(1250.0F))
                .body("content.imgUrl[0]", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
    }

    @Test
    public void findAllShouldReturnPageProductsWithPriceGreaterThan2000() {

        given()
                .get("/products?size=25")
                .then()
                .statusCode(200)
                .body("content.findAll {it.price>2000}.name", hasItems("Smart TV", "PC Gamer Hera"));
    }

    @Test
    public void insertShouldReturnProductCreatedWhenAdminLogged() throws JSONException {

        JSONObject newProduct =new JSONObject(postProductInstance);

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .body("name", equalTo("Meu novo produto"))
                .body("price", is(200.0F))
                .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"))
                .body("categories.id", hasItems(2, 3));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName() throws JSONException {

        postProductInstance.put("name", "me");

        JSONObject newProduct =new JSONObject(postProductInstance);

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("O nome precisa ter de 3 a 80 caracteres."))
                .body("errors.fieldName[0]", equalTo("name"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidDescription() throws JSONException {

        postProductInstance.put("description", "la la");

        JSONObject newProduct =new JSONObject(postProductInstance);

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("Descrição deve ter no mínimo 10 caracteres!"))
                .body("errors.fieldName[0]", equalTo("description"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceIsNegative() throws JSONException {

        postProductInstance.put("price", -200.0);

        JSONObject newProduct =new JSONObject(postProductInstance);

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("O preço deve ser positivo!"))
                .body("errors.fieldName[0]", equalTo("price"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndProductHasNoCategory() throws JSONException {

        postProductInstance.put("categories", null);

        JSONObject newProduct =new JSONObject(postProductInstance);

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("Não pode ser vazio!"))
                .body("errors.fieldName[0]", equalTo("categories"));
    }

    @Test
    public void insertShouldReturnForbiddenWhenClientLogged() throws JSONException {

        JSONObject newProduct =new JSONObject(postProductInstance);

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(403);
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() throws JSONException {

        JSONObject newProduct =new JSONObject(postProductInstance);

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(401);
    }

    @Test
    public void updateShouldReturnProductWhenIdExistsAndAdminLogged() throws JSONException {

        JSONObject product = new JSONObject(putProductInstance);
        existingProductId = 1L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Produto atualizado"))
                .body("description", equalTo("Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim"))
                .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"))
                .body("price", is(200F))
                .body("categories.id", hasItems(2, 3))
                .body("categories.name", hasItems("Eletrônicos", "Computadores"));
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() throws JSONException {

        JSONObject product = new JSONObject(putProductInstance);
        nonExistingProductId = 100L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/{id}", nonExistingProductId)
                .then()
                .statusCode(404)
                .body("error", equalTo(" Resource not found"))
                .body("path", equalTo("/products/100"))
                .body("message", equalTo("Id não encontrado!"));
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndInvalidName() throws JSONException {

        putProductInstance.put("name", "oi");

        JSONObject product = new JSONObject(putProductInstance);
        existingProductId = 1L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(422)
                .body("errors.fieldName[0]", equalTo("name"))
                .body("errors.message[0]", equalTo("O nome precisa ter de 3 a 80 caracteres."));
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidDescription() throws JSONException {

        putProductInstance.put("description", "invalid");

        JSONObject product = new JSONObject(putProductInstance);
        existingProductId = 1L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(422)
                .body("errors.fieldName[0]", equalTo("description"))
                .body("errors.message[0]", equalTo("Descrição deve ter no mínimo 10 caracteres!"));
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceIsNegative() throws JSONException {

        putProductInstance.put("price", -200.0);

        JSONObject product = new JSONObject(putProductInstance);
        existingProductId = 1L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(422)
                .body("errors.fieldName[0]", equalTo("price"))
                .body("errors.message[0]", equalTo("O preço deve ser positivo!"));
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndProductsHasNoCategory() throws JSONException {

        putProductInstance.put("categories", null);

        JSONObject product = new JSONObject(putProductInstance);
        existingProductId = 1L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(422)
                .body("errors.fieldName[0]", equalTo("categories"))
                .body("errors.message[0]", equalTo("Não pode ser vazio!"));
    }

    @Test
    public void updateShouldReturnForbiddenWhenIdExistsAndClientLogged() throws JSONException {

        JSONObject product = new JSONObject(putProductInstance);
        existingProductId = 1L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(403);
    }

    @Test
    public void updateShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() throws JSONException {

        JSONObject product = new JSONObject(putProductInstance);
        existingProductId = 1L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/{id}", existingProductId)
                .then()
                .statusCode(401);
    }

    @Test
    public void deleteShouldReturnNoContentWhenAdminLoggedAndIdExists() throws JSONException {

        existingProductId = 25L;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/products/{id}", existingProductId)
                .then()
                .statusCode(204);
    }

    @Test
    public void deleteShouldReturnNotFoundWhenAdminLoggedAndIdDoesNotExist() throws JSONException {

        nonExistingProductId = 100L;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/products/{id}", nonExistingProductId)
                .then()
                .statusCode(404)
                .body("status", is(404))
                .body("error", equalTo(" Resource not found"))
                .body("message", equalTo("Id não encontrado!"))
                .body("path", equalTo("/products/100"));
    }

    @Test
    public void deleteShouldReturnBadRequestWhenAdminLoggedAndDependentId() throws JSONException {

        dependentProductId = 1L;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/products/{id}", dependentProductId)
                .then()
                .statusCode(400)
                .body("status", is(400))
                .body("error", equalTo("Database exception"))
                .body("message", equalTo( "Falha de integridade referencial"))
                .body("path", equalTo("/products/1"));
    }

    @Test
    public void deleteShouldReturnForbiddenWhenClientLogged() {

        existingProductId = 2L;

        given()
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .delete("/products/{id}", existingProductId)
                .then()
                .statusCode(403);
    }

    @Test
    public void deleteShouldReturnUnauthorizedWhenInvalidToken() throws JSONException {

        existingProductId = 2L;

        given()
                .header("Authorization", "Bearer " + invalidToken)
                .when()
                .delete("/products/{id}", existingProductId)
                .then()
                .statusCode(401);
    }
}
