import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.qameta.allure.model.Parameter.Mode.HIDDEN;
import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.assertEquals;

public class UserCreateClient {
    static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/";
    public static final String REGISTER_PATH = "auth/register";
    public static final String DELETE_PATH = "auth/user";

    @Step("Создаем пользователя и извлекаем токен. Ожидаем код 200 ОК")
    public static String createUser(User user) {
        String requestBody = "{"
                + "\"email\": \"" + user.getEmail() + "\","
                + "\"password\": \"" + user.getPassword() + "\","
                + "\"name\": \"" + user.getName() + "\""
                + "}";

        ValidatableResponse response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(requestBody)
                .when()
                .post(REGISTER_PATH)
                .then()
                .statusCode(HTTP_OK);
        return response.extract().path("accessToken");
    }

    @Step("Невозможно создать пользователя, который уже зарегистрирован. Ожидаем код 403 Forbidden")
    public static ValidatableResponse verifyDuplicateUserRegistration(User user) {
        String requestBodyFromPostman = "{"
                + "\"email\": \"" + user.getEmail() + "\","
                + "\"password\": \"" + user.getPassword() + "\","
                + "\"name\": \"" + user.getName() + "\""
                + "}";

        // Выполняем POST запрос для регистрации пользователя
        return given()
                .log()
                .all()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(requestBodyFromPostman)
                .when()
                .post(REGISTER_PATH)
                .then()
                .statusCode(HTTP_FORBIDDEN)
                .log().all();
    }

    @Step("Проверяем сообщение об ошибке: User with such email already exists")
    public static void verifyErrorMessage(ValidatableResponse response, String expectedMessage) {
        String actualMessage = response.extract().path("message");
        assertEquals(expectedMessage, actualMessage);
    }

    @Step("Создание пользователя без имени невозможно. Ожидаем код 403 Forbidden")
    public static ValidatableResponse createUserWithoutLogin(User user) {
        String requestBody = "{"
                + "\"email\": \"" + user.getEmail() + "\","
                + "\"password\": \"" + user.getPassword() + "\""
                + "}";
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(requestBody)
                .when()
                .post(REGISTER_PATH)
                .then();
    }

    @Step("Создание пользователя без email невозможно. Ожидаем код 403 Forbidden")
    public static ValidatableResponse createUserWithoutEmail(User user) {
        String requestBody = "{"
                + "\"password\": \"" + user.getPassword() + "\","
                + "\"name\": \"" + user.getName() + "\""
                + "}";
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(requestBody)
                .when()
                .post(REGISTER_PATH)
                .then();
    }

    @Step("Создание пользователя без пароля не возможно. Ожидмем код 403 Forbidden")
    public static ValidatableResponse createUserWithoutPassword(User user) {
        String requestBody = "{"
                + "\"email\": \"" + user.getEmail() + "\","
                + "\"name\": \"" + user.getName() + "\""
                + "}";
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(requestBody)
                .when()
                .post(REGISTER_PATH)
                .then();
    }

    @Step("Удаляем пользователя (токен скрыт)")
    public static ValidatableResponse deleteUser(@Param(mode = HIDDEN) String accessToken) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .when()
                .delete(BASE_URL + DELETE_PATH)
                .then()
                .log().all();
    }
}