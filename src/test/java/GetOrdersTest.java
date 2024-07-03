import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practikum.register.GenerateUser;
import ru.practikum.steps.OrderSteps;
import ru.practikum.steps.UserSteps;

import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static org.hamcrest.Matchers.*;
import static ru.practikum.constants.ConstantURI.STELLAR_BURGERS_URI;

public class GetOrdersTest {
    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();
    private List<String> ingredients;

    private List<String> getIngredientsToOrder() {
        ingredients = orderSteps.shuffleIngredients(orderSteps.getIngredients());
        return List.of(ingredients.get(0), ingredients.get(1));

    }

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
    @DisplayName("Get order info of user with authorization, order exists")
    public void getOrderInfoAuthorizedShouldReturn200() {
        accessToken = userSteps
                .createUser(generateUser)
                .extract().body().path("accessToken");
        orderSteps
                .createOrderWithAuthorization(getIngredientsToOrder(), accessToken);

        orderSteps
                .getOrderAuthorizedUser(accessToken)
                .statusCode(200)
                .body("success", is(true))
                .body("orders.ingredients", is(notNullValue()))
                .body("orders.status", is(notNullValue()))
                .body("orders.number", is(notNullValue()))
                .body("orders.createdAt", is(notNullValue()))
                .body("orders.updatedAt", is(notNullValue()))
                .body("total", is(notNullValue()))
                .body("totalToday", is(notNullValue()));
    }

    @Test
    @DisplayName("Get order info of user with authorization, order does not exist")
    public void getOrderInfoAuthorizedNoOrderShouldReturn200() {
        accessToken = userSteps
                .createUser(generateUser)
                .extract().body().path("accessToken");

        orderSteps
                .getOrderAuthorizedUser(accessToken)
                .statusCode(200)
                .body("orders", is(empty()))
                .body("total", is(notNullValue()))
                .body("totalToday", is(notNullValue()));
    }

    @Test
    @DisplayName("Get order info of user without authorization, order exists")
    public void getOrderInfoUnauthorizedShouldReturn401() {
        orderSteps
                .createOrderWithoutAuthorization(getIngredientsToOrder());

        orderSteps
                .getOrderUnauthorizedUser()
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }
}
