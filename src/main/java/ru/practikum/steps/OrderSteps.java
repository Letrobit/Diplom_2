package ru.practikum.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import ru.practikum.dto.CreateUserRequest;
import ru.practikum.dto.OrderRequest;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderSteps {
    @Step("Create order authorized")
    public ValidatableResponse createOrderWithAuthorization(List<String> ingredients, String accessToken) {
        OrderRequest orderRequest = new OrderRequest(ingredients);
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(orderRequest)
                .when()
                .post("/api/orders")
                .then();
    }

    @Step("Create order unauthorized")
    public ValidatableResponse createOrderWithoutAuthorization(List<String> ingredients) {
        OrderRequest orderRequest = new OrderRequest(ingredients);
        return given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/orders")
                .then();
    }

    @Step("Get list of ingredient ids")
    public List<String> getIngredients() {
        return given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/ingredients")
                .then()
                .extract()
                .path("data._id");
    }

    @Step("Shuffle a list of ingredients in random order")
    public List<String> shuffleIngredients(List<String> ingredients) {
        Collections.shuffle(ingredients);
        return ingredients;
    }

    @Step("Get orders for authorized user")
    public ValidatableResponse getOrderAuthorizedUser(String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .when()
                .get("/api/orders")
                .then();
    }

    @Step("Get orders for unauthorized user")
    public ValidatableResponse getOrderUnauthorizedUser() {
        return given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/orders")
                .then();
    }

}
