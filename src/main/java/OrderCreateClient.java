import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.qameta.allure.model.Parameter.Mode.HIDDEN;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

public class OrderCreateClient {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/";
    private static final String CREATE_ORDER_PATH = "orders";

    @Step("Созднаие закза. Ожидаем 200 ОК")
    public static ValidatableResponse createOrderWithAuthAndHashIsTrue(@Param(mode = HIDDEN)String accessToken, String bun, String sauce, String filling) {
        return RestAssured.given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .log().all()
                .body("{\"ingredients\": [\"" + bun + "\", \"" + sauce + "\", \"" + filling + "\"]}")
                .post(BASE_URL + CREATE_ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Созднаие закза. Ожидаем код 500 Internal Server Error")
    public static ValidatableResponse createOrderWithAuthAndHashIsNotTrue(@Param(mode = HIDDEN)String accessToken, String bun, String sauce, String filling) {
        return RestAssured.given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .log().all()
                .body("{\"ingredients\": [\"" + "bun" + "\", \"" + "sauce" + "\", \"" + "filling" + "\"]}")
                .post(BASE_URL + CREATE_ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Созднаие закза. Ожидаем код 400 Bad Request")
    public static ValidatableResponse createOrderWithAuthAndWithoutIngredients(@Param(mode = HIDDEN)String accessToken) {
        return RestAssured.given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body("{\"ingredients\": []}")
                .post(BASE_URL + CREATE_ORDER_PATH)
                .then();
    }

    @Step("Проверяем сообщение об ошибке: Ingredient ids must be provided")
    public static void validateCreateOrderWithoutIngredientsError(ValidatableResponse response) {
        String errorMessage = response.extract().path("message");
        assertEquals("Ingredient ids must be provided", errorMessage);
    }

    @Step("Создание заказа. Ожидаем код 401 Unauthorized")
    public static ValidatableResponse createOrderWithoutAuthAndHashIsTrue(String bun, String sauce, String filling) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .log().all()
                .body("{\"ingredients\": [\"" + bun + "\", \"" + sauce + "\", \"" + filling + "\"]}")
                .post(BASE_URL + CREATE_ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Создание заказа. Ожидаем код 401 Unauthorized")
    public static ValidatableResponse createOrderWithoutAuthAndHashIsNotTrue() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .log().all()
                .body("{\"ingredients\": [\"" + "bun" + "\", \"" + "sauce" + "\", \"" + "filling" + "\"]}")
                .post(BASE_URL + CREATE_ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Создание заказа. Ожидаем код 401 Unauthorized")
    public static ValidatableResponse createOrderWithoutAuthAndWithoutIngredients() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .log().all()
                .body("{\"ingredients\": []}")
                .post(BASE_URL + CREATE_ORDER_PATH)
                .then();
    }

    @Step("Проверяем сообщение об ошибке: You should be authorised")
    public static void validateUnauthorizedError(ValidatableResponse response) {
        response.statusCode(HTTP_UNAUTHORIZED); // Используем код 401
        String errorMessage = response.extract().path("message");
        assertEquals("You should be authorised", errorMessage);
    }
}