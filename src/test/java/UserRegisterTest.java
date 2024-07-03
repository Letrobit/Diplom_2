import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import ru.practikum.register.GenerateUser;
import ru.practikum.steps.UserSteps;

import static ru.practikum.constants.ConstantURI.STELLAR_BURGERS_URI;

import static io.restassured.RestAssured.baseURI;
import static org.hamcrest.Matchers.*;

public class UserRegisterTest {
    private final UserSteps userSteps = new UserSteps();
    private String accessToken;
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
    @DisplayName("User creation with name, password and email")
    public void createUserTestShouldReturn200() {
        accessToken = userSteps
                .createUser(generateUser)
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", is(generateUser.getEmail().toLowerCase()))
                .body("user.name", is(generateUser.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .extract().body().path("accessToken");
    }

    @Test
    @DisplayName("Duplicate user creation attempt - expected to not create")
    public void createUserDuplicateShouldReturn403() {
        accessToken = userSteps
                .createUser(generateUser)
                .statusCode(200)
                .extract().body().path("accessToken");
        userSteps
                .createUser(generateUser)
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }
}
