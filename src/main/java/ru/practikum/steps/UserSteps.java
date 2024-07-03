package ru.practikum.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import ru.practikum.dto.CreateUserRequest;
import ru.practikum.dto.LoginUserRequest;
import ru.practikum.dto.ResetInfoUserRequest;
import ru.practikum.register.GenerateUser;

import static io.restassured.RestAssured.given;

public class UserSteps {
    @Step("Send post to register user with info")
    public ValidatableResponse createUser(GenerateUser generateUser) {
        CreateUserRequest createUserRequest = new CreateUserRequest(generateUser.getName(), generateUser.getPassword(), generateUser.getEmail());
        return given()
                .contentType(ContentType.JSON)
                .body(createUserRequest)
                .when()
                .post("/api/auth/register")
                .then();
    }


    @Step("Login user with access token")
    public ValidatableResponse loginUserAuthToken(String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .auth().oauth2(accessToken)
                .when()
                .post("/api/auth/login")
                .then();
    }

    @Step("Delete user with access token")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .when()
                .delete("/api/auth/user")
                .then();
    }

    @Step("Login user with email and password")
    public ValidatableResponse loginUserLogopass(String email, String password) {
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setEmail(email);
        loginUserRequest.setPassword(password);
        return given()
                .contentType(ContentType.JSON)
                .body(loginUserRequest)
                .when()
                .post("/api/auth/login")
                .then();
    }

    @Step("User reset info with authorization")
    public ValidatableResponse userResetInfo(String name, String email, String accessToken) {
        ResetInfoUserRequest resetInfoUserRequest = new ResetInfoUserRequest();
        resetInfoUserRequest.setName(name);
        resetInfoUserRequest.setEmail(email);
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(resetInfoUserRequest)
                .when()
                .patch("/api/auth/user")
                .then();
    }

    @Step("User reset info without authorization")
    public ValidatableResponse userResetInfoNoAccessToken(String name, String email) {
        ResetInfoUserRequest resetInfoUserRequest = new ResetInfoUserRequest();
        resetInfoUserRequest.setName(name);
        resetInfoUserRequest.setEmail(email);
        return given()
                .contentType(ContentType.JSON)
                .body(resetInfoUserRequest)
                .when()
                .patch("/api/auth/user")
                .then();
    }
}
