import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practikum.register.GenerateUser;
import ru.practikum.steps.UserSteps;

import static io.restassured.RestAssured.baseURI;
import static org.hamcrest.Matchers.*;
import static ru.practikum.constants.ConstantURI.STELLAR_BURGERS_URI;

public class UserInfoResetTest {
    private final UserSteps userSteps = new UserSteps();
    String accessToken;
    private GenerateUser generateUser;

    @Before
    public void setup() {
        //RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        baseURI = STELLAR_BURGERS_URI;
        generateUser = new GenerateUser();
    }

    @After
    public void teardown() {
        if (accessToken != null) {
            userSteps
                    .deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("User email reset with authorization")
    public void resetUserEmailWithAuthorizationTestShouldReturn200() {
        accessToken = userSteps
                .createUser(generateUser)
                .extract().body().path("accessToken");

        String newEmail = RandomStringUtils.randomAlphabetic(6) + "@" + "megaBorgerLove.com";
        userSteps
                .userResetInfo(generateUser.getName(), newEmail, accessToken)
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", is(newEmail.toLowerCase()))
                .body("user.name", is(notNullValue()));

    }

    @Test
    @DisplayName("User name reset with authorization")
    public void resetUserNameWithAuthorizationTestShouldReturn200() {
        accessToken = userSteps
                .createUser(generateUser)
                .extract().body().path("accessToken");

        String newName = RandomStringUtils.randomAlphabetic(10);
        userSteps
                .userResetInfo(newName, generateUser.getEmail(), accessToken)
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", is(notNullValue()))
                .body("user.name", is(newName));

    }

    @Test
    @DisplayName("User email reset without authorization")
    public void resetUserEmailWithoutAuthorizationTestShouldReturn401() {
        accessToken = userSteps
                .createUser(generateUser)
                .extract().body().path("accessToken");

        String newEmail = RandomStringUtils.randomAlphabetic(6) + "@" + "megaBorgerLove.com";
        userSteps
                .userResetInfoNoAccessToken(generateUser.getName(), newEmail)
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("You should be authorised"));

    }

    @Test
    @DisplayName("User name reset without authorization")
    public void resetUserNameWithoutAuthorizationTestShouldReturn401() {
        accessToken = userSteps
                .createUser(generateUser)
                .extract().body().path("accessToken");

        String newName = RandomStringUtils.randomAlphabetic(10);
        userSteps
                .userResetInfoNoAccessToken(newName, generateUser.getEmail())
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("You should be authorised"));

    }
}
