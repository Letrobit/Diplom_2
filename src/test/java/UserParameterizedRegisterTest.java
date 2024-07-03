import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.practikum.register.GenerateUser;
import ru.practikum.steps.UserSteps;

import static io.restassured.RestAssured.baseURI;
import static org.hamcrest.Matchers.is;
import static ru.practikum.constants.ConstantURI.STELLAR_BURGERS_URI;

@RunWith(Parameterized.class)
public class UserParameterizedRegisterTest {
    private final UserSteps userSteps = new UserSteps();
    private static GenerateUser generateUser;

    public UserParameterizedRegisterTest(String name, String password, String email) {
        generateUser = new GenerateUser(name, password, email);
    }

    @Before
    public void setup() {
        //RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        baseURI = STELLAR_BURGERS_URI;
    }


    @Parameterized.Parameters
    public static Object[][] params() {
        return new Object[][]{
                {RandomStringUtils.randomAlphabetic(10), null, RandomStringUtils.randomAlphabetic(10)},
                {null, RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10)},
                {RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), null},
        };
    }

    @Test
    @DisplayName("User creation without some fields")
    public void createUserTestWrongInfoShouldReturn403() {
        userSteps
                .createUser(generateUser)
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

}
