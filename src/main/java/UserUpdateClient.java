import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

import static io.qameta.allure.model.Parameter.Mode.HIDDEN;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;


public class UserUpdateClient {

    static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/";
    public static final String USER_DATA_PATH = "auth/user";

    @Step("Получаем данные пользователя (токен скрыт)")
    public static ValidatableResponse getUserData(@Param(mode = HIDDEN) String token) {
        return RestAssured.given()
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .log().all()
                .when()
                .get(BASE_URL + USER_DATA_PATH)
                .then()
                .log().all();
    }

    @Step("Обновляем имя пользователя")
    public static ValidatableResponse updateUserName(@Param(mode = HIDDEN) String token, String newName) {
        return RestAssured.given()
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .body("{\"name\":\"" + newName + "\"}")
                .log().all()
                .when()
                .patch(BASE_URL + USER_DATA_PATH)
                .then()
                .log().all();
    }

    @Step("Обновляем email пользователя")
    public static ValidatableResponse updateUserEmail(@Param(mode = HIDDEN) String token, String newEmail) {
        return RestAssured.given()
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .body("{\"email\":\"" + newEmail + "\"}")
                .log().all()
                .when()
                .patch(BASE_URL + USER_DATA_PATH)
                .then()
                .log().all();
    }

    @Step("Обновляем имя и email пользователя")
    public static ValidatableResponse updateUserNameAndEmail(@Param(mode = HIDDEN) String token, String newName, String newEmail) {
        return RestAssured.given()
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .body("{\"name\":\"" + newName + "\", \"email\":\"" + newEmail + "\"}")
                .log().all()
                .when()
                .patch(BASE_URL + USER_DATA_PATH)
                .then()
                .log().all();
    }

    @Step("Проверяем сообщение об ошибке: email уже используется")
    public static void validateEmailAlreadyExistsError(ValidatableResponse response) {
        response.statusCode(HTTP_FORBIDDEN);
        String errorMessage = response.extract().path("message");
        assertEquals("User with such email already exists", errorMessage);
    }

    @Step("Проверяем сообщение об ошибке: вы должны быть авторизированы")
    public static void validateUnauthorizedError(ValidatableResponse response) {
        response.statusCode(HTTP_UNAUTHORIZED);
        String errorMessage = response.extract().path("message");
        assertEquals("You should be authorised", errorMessage);
    }
}