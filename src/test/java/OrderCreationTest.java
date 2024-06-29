import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.practikum.register.GenerateUser;
import ru.practikum.steps.OrderSteps;
import ru.practikum.steps.UserSteps;

import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static ru.practikum.constants.ConstantURI.STELLAR_BURGERS_URI;

public class OrderCreationTest {
    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();
    private List<String> ingredients;

    private List<String> getIngredientsToOrder() {
        ingredients = orderSteps.shuffleIngredients(orderSteps.getIngredients());
        return List.of(ingredients.get(0), ingredients.get(1));

    }

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
    @DisplayName("Create order with authorization")
    public void createOrderWithAuthorizationShouldReturn200() {
        accessToken = userSteps
                .createUser(generateUser)
                .extract().body().path("accessToken");
        orderSteps
                .createOrderWithAuthorization(getIngredientsToOrder(), accessToken)
                .statusCode(200)
                .body("name", is(notNullValue()))
                .body("order.number", is(notNullValue()))
                .body("success", is(true));
    }

    @Test
    @DisplayName("Create order without authorization")
    public void createOrderWithoutAuthorizationShouldReturn200() {
        orderSteps
                .createOrderWithoutAuthorization(getIngredientsToOrder())
                .statusCode(200)
                .body("name", is(notNullValue()))
                .body("order.number", is(notNullValue()))
                .body("success", is(true));
    }

    @Test
    @DisplayName("Create order with authorization and check ingredients")
    public void createOrderWithoutAuthorizationWithIngredientsShouldReturn200() {
        accessToken = userSteps
                .createUser(generateUser)
                .extract().body().path("accessToken");

        List<String> expectedIngredients = getIngredientsToOrder();
        orderSteps
                .createOrderWithAuthorization(expectedIngredients, accessToken);
        List<String> actualIngredients = orderSteps
                .getOrderAuthorizedUser(accessToken)
                .statusCode(200)
                .extract().body().path("orders.ingredients");

        String actualComparableIngredients = actualIngredients.toString().replace("[", "").replace("]", ""); //jank
        String expectedComparableIngredients = expectedIngredients.toString().replace("[", "").replace("]", "");

        Assert.assertEquals(expectedComparableIngredients, actualComparableIngredients);
    }


    @Test
    @DisplayName("Create order with authorization and without ingredients")
    public void createOrderWithoutIngredientsShouldReturn400() {
        accessToken = userSteps
                .createUser(generateUser)
                .extract().body().path("accessToken");
        orderSteps
                .createOrderWithAuthorization(null, accessToken)
                .statusCode(400)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }


    @Test
    @DisplayName("Create order with authorization and with incorrect ingredients")
    public void createOrderWithIncorrectIngredientHashShouldReturn500() {
        accessToken = userSteps
                .createUser(generateUser)
                .extract().body().path("accessToken");
        orderSteps
                .createOrderWithoutAuthorization(List.of("I'll have two number 9's, a number 9 large, " +
                                                                "a number 6 with extra dip, a number 7, two number 45's," +
                                                                 " one with cheese, and a large soda."))
                .statusCode(500);
    }

}
