import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practikum.register.GenerateUser;
import ru.practikum.steps.UserSteps;

import static io.restassured.RestAssured.baseURI;
import static org.hamcrest.Matchers.*;
import static ru.practikum.constants.ConstantURI.STELLAR_BURGERS_URI;

public class UserLoginTest {
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
    @DisplayName("User login with correct info")
    public void loginUserTestShouldReturn200() {
        userSteps
                .createUser(generateUser);


        accessToken = userSteps
                .loginUserLogopass(generateUser.getEmail(), generateUser.getPassword())
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", is(generateUser.getEmail().toLowerCase()))
                .body("user.name", is(generateUser.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .extract().body().path("accessToken");
    }

    @Test
    @DisplayName("User login with incorrect password")
    public void loginUserTestWrongPasswordShouldReturn401() {
        accessToken = userSteps
                .createUser(generateUser)
                .extract().body().path("accessToken");


        userSteps
                .loginUserLogopass(generateUser.getEmail(), "password12345")
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));

    }

    @Test
    @DisplayName("User login with incorrect email")
    public void loginUserTestWrongEmailShouldReturn401() {
        accessToken = userSteps
                .createUser(generateUser)
                .extract().body().path("accessToken");


        userSteps
                .loginUserLogopass("kebab_enjoyer@burger-hater.org", generateUser.getPassword())
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));

    }


}
