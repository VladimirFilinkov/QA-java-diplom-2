import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderCreateTest {

    private User testUser;
    private String accessToken;
    private String bun = "61c0c5a71d1f82001bdaaa6d";
    private String sauce = "61c0c5a71d1f82001bdaaa74";
    private String filling = "61c0c5a71d1f82001bdaaa6f";

    @Before
    public void setUp() {
        testUser = null;
        accessToken = null;
    }

    @After
    @DisplayName("Удаляем пользователя, если он был создан")
    public void tearDown() {
        if (testUser != null && accessToken != null) {
            UserCreateClient.deleteUser(accessToken).statusCode(HTTP_ACCEPTED);
            System.out.println("Тесты окончены, тестовый пользователь удалён");
        }
    }

    @Test
    @DisplayName("Создаём заказ с авторизацией. Хэш ингредиентов верный")
    public void createOrderWithAuthAndHashIsIngredientsIsTrueResponse200() {
        testUser = User.randomUser();
        accessToken = UserCreateClient.createUser(testUser);

        ValidatableResponse response = OrderCreateClient.createOrderWithAuthAndHashIsTrue(accessToken, bun, sauce, filling);
        response
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создаём заказ с авторизацией. Хэш ингредиентов НЕ верный")
    public void createOrderWithAuthAndHashIngredientsIsNotTrueResponse500() {
        testUser = User.randomUser();
        accessToken = UserCreateClient.createUser(testUser);

        ValidatableResponse response = OrderCreateClient.createOrderWithAuthAndHashIsNotTrue(accessToken, bun, sauce, filling);
        response
                .statusCode(HTTP_INTERNAL_ERROR) ;
    }

    @Test
    @DisplayName("Создаём заказ с авторизацией. Ингредиенты не переданы")
    public void createOrderWithAuthAndWithoutIngredientsResponse400() {
        testUser = User.randomUser();
        accessToken = UserCreateClient.createUser(testUser);

        ValidatableResponse response = OrderCreateClient.createOrderWithAuthAndWithoutIngredients(accessToken);
        response.statusCode(HTTP_BAD_REQUEST);

        OrderCreateClient.validateCreateOrderWithoutIngredientsError(response);
    }

    @Test
    @DisplayName("Создаём заказ без авторизации. Хэш ингредиентов верный")
    public void createOrderWithoutAuthAndHashIsIngredientsIsTrueResponse401() {
        ValidatableResponse response = OrderCreateClient.createOrderWithoutAuthAndHashIsTrue(bun, sauce, filling);
        response.statusCode(HTTP_UNAUTHORIZED);

        OrderCreateClient.validateUnauthorizedError(response);
    }

    @Test
    @DisplayName("Создаём заказ без авторизации. Хэш ингредиентов НЕ верный")
    public void createOrderWithoutAuthAndHashIngredientsIsNotTrueResponse401() {
        ValidatableResponse response = OrderCreateClient.createOrderWithoutAuthAndHashIsNotTrue();
        response.statusCode(HTTP_UNAUTHORIZED);

        OrderCreateClient.validateUnauthorizedError(response);
    }

    @Test
    @DisplayName("Создаём заказ без авторизации. Ингредиенты не переданы")
    public void createOrderWithoutAuthAndWithoutIngredientsResponse401() {
        ValidatableResponse response = OrderCreateClient.createOrderWithoutAuthAndWithoutIngredients();
        response.statusCode(HTTP_UNAUTHORIZED);

        OrderCreateClient.validateUnauthorizedError(response);
    }
}