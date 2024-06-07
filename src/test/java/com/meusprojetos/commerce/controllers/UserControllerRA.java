package com.meusprojetos.commerce.controllers;

import com.meusprojetos.commerce.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasItem;

public class UserControllerRA {

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String adminToken, clientToken, invalidToken;

    private Map<String, Object> postUserInstance;
    private Map<String, Object> putUserInstance;

    private Long existingUserId, nonExistingUserId;

    @BeforeEach
    public void setUp()  throws Exception {

        baseURI = "http://localhost:8080";

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";

        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        invalidToken = adminToken + "xpto";


        postUserInstance = new HashMap<>();
        postUserInstance.put("name", "novo usuario");
        postUserInstance.put("email", "bob@gmail.com");
        postUserInstance.put("phone", "999999991");
        postUserInstance.put("password", "bob123456");
        postUserInstance.put("birthDate", "1994-07-20");

        putUserInstance = new HashMap<>();
        putUserInstance.put("name", "novo usuario");
        putUserInstance.put("email", "bob@gmail.com");
        putUserInstance.put("phone", "999999991");
        putUserInstance.put("password", "bob123456");
        putUserInstance.put("birthDate", "1994-07-20");

        List<Map<String, Object>> roles = new ArrayList<>();

        Map<String, Object> role1 = new HashMap<>();
        role1.put("id", 1);

        roles.add(role1);
        postUserInstance.put("roles", roles);
    }

    @Test
    public void getMeShouldReturnUserWhenAdminLogged() throws Exception {

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .accept(ContentType.JSON)
                .when()
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("id", is(2))
                .body("name", equalTo("Alex Green"))
                .body("email", equalTo("alex@gmail.com"))
                .body("phone", equalTo("977777777"))
                .body("birthDate", equalTo("1987-12-13"))
                .body("roles.authority", hasItems("ROLE_CLIENT", "ROLE_ADMIN"));
    }

    @Test
    public void getMeShouldReturnUserWhenClientLogged() throws Exception {

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
                .when()
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", equalTo("Maria Brown"))
                .body("email", equalTo("maria@gmail.com"))
                .body("phone", equalTo("988888888"))
                .body("birthDate", equalTo("2001-07-25"))
                .body("roles.authority", hasItems("ROLE_CLIENT"));
    }

    @Test
    public void getMeShouldReturnUnauthorizedWhenInvalidToken() throws Exception {

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .accept(ContentType.JSON)
                .when()
                .get("/users/me")
                .then()
                .statusCode(401);
    }

    @Test
    public void findByIdShouldReturnUserWhenIdExists() throws Exception {

        existingUserId = 1L;

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/users/{id}", existingUserId)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", equalTo("Maria Brown"))
                .body("email", equalTo("maria@gmail.com"))
                .body("phone", is("988888888"))
                .body("birthDate", equalTo("2001-07-25"))
                .body("roles.authority", hasItem("ROLE_CLIENT"));
    }

    @Test
    public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {

        nonExistingUserId = 100L;

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/users/{id}", nonExistingUserId)
                .then()
                .statusCode(404);
    }

    @Test
    public void findByIdShouldThrowsForbiddenWhenClientLoggedAndAccessOtherUser() throws Exception {

        existingUserId = 2L;

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/users/{id}", existingUserId)
                .then()
                .statusCode(403);
    }

    @Test
    public void findAllShouldReturnPagedUsersWhenAdminLogged() throws Exception {


        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("content.name", hasItems("Maria Brown", "Alex Green"))
                .body("content.id", hasItems(1, 2));
    }

    @Test
    public void findAllShouldThrowsForbiddenWhenClientLogged() throws Exception {

        given()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/users")
                .then()
                .statusCode(403);
    }
}
