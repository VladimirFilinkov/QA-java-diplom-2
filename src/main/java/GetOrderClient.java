import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.qameta.allure.model.Parameter.Mode.HIDDEN;
import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

public class GetOrderClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private static final String GET_USER_ORDERS_PATH = "/api/orders";

    // Метод для получения заказов пользователя с токеном
    @Step("Получаем зазаз. Ожидаем код 200 ОК")
    public static ValidatableResponse getUserOrdersWithAuth(@Param(mode = HIDDEN)String accessToken) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .when()
                .get(BASE_URL + GET_USER_ORDERS_PATH)
                .then()
                .log().all();
    }

    // Метод для получения заказов пользователя без токена
    @Step("Получаем зазаз. Ожидаем код 401 Unauthorized")
    public static ValidatableResponse getUserOrdersWithoutAuth() {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .when()
                .get(BASE_URL + GET_USER_ORDERS_PATH)
                .then()
                .log().all();
    }

    @Step("Проверяем сообщение об ошибке: You should be authorised")
    public static void validateUnauthorizedError(ValidatableResponse response) {
        response.statusCode(HTTP_UNAUTHORIZED);
        String errorMessage = response.extract().path("message");
        assertEquals("You should be authorised", errorMessage);
    }
}