import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;

import static java.net.HttpURLConnection.HTTP_ACCEPTED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.*;

public class GetOrderTest {
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
    @DisplayName("Получаем заказы пользователя с авторизацией")
    public void getUserOrdersWithAuthorizationResponse200() {
        testUser = User.randomUser();
        accessToken = UserCreateClient.createUser(testUser);

        // Создаем заказ пользователя
        OrderCreateClient.createOrderWithAuthAndHashIsTrue(accessToken, bun, sauce, filling)
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());

        // Запрашиваем заказы пользователя
        ValidatableResponse response = GetOrderClient.getUserOrdersWithAuth(accessToken);
        response
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("orders", not(empty()))
                .body("orders[0].ingredients", hasSize(greaterThan(0)))
                .body("orders[0].status", notNullValue());
    }

    @Test
    @DisplayName("Получаем заказы пользователя без авторизации")
    public void getUserOrdersWithoutAuthorizationResponse401()
    {   testUser = User.randomUser();
        accessToken = UserCreateClient.createUser(testUser);

        // Создаем заказ пользователя
        OrderCreateClient.createOrderWithAuthAndHashIsTrue(accessToken, bun, sauce, filling)
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());

        // Запрашиваем заказы пользователя без авторизации
        ValidatableResponse response = GetOrderClient.getUserOrdersWithoutAuth();

        // Проверяем статус и сообщение об ошибке
        GetOrderClient.validateUnauthorizedError(response);
    }
}