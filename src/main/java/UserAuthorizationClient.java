import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.assertEquals;

public class UserAuthorizationClient {
    static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/";
    public static final String AUTH_PATH = "auth/login";

    // Метод для логина и получения токена
    @Step("Логинимся созданным пользователем и извлекаем токен. Ожидаем код 200 ОК")
    public static String logInAndGetToken(User user) {
        String requestBodyFromPostman = "{"
                + "\"email\": \"" + user.getEmail() + "\","
                + "\"password\": \"" + user.getPassword() + "\""
                + "}";

        // Получаем ответ и извлекаем accessToken
        return given()
                .log()
                .all()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(requestBodyFromPostman)
                .when()
                .post(AUTH_PATH)
                .then()
                .log().all()
                .statusCode(HTTP_OK)
                .extract()
                .path("accessToken");
    }

    //Авторизация пользователя с неверным логином
    @Step("Невозможно авторизоваться с неверным email")
    public static ValidatableResponse logInWithNotValidLogin(User user) {
        String requestBodyFromPostman = "{"
                + "\"email\": \"" + "False@Email" + "\","
                + "\"password\": \"" + user.getPassword() + "\""
                + "}";

        return given()
                .log()
                .all()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(requestBodyFromPostman)
                .when()
                .post(AUTH_PATH)
                .then()
                .log().all()
                .statusCode(HTTP_UNAUTHORIZED);
    }

    // Проверяем сообщение об ошибке при авторизации пользователя с неверным логином
    @Step("Проверяем сообщение об ошибке для входа пользователя с неверным email")
    public static void verifyErrorMessageForUserLogInWithNotValidLogin(User user, String expectedMessage) {
        String actualMessage = logInWithNotValidLogin(user)
                .extract()
                .path("message");

        assertEquals(expectedMessage, actualMessage);
    }

    @Step("Невозможно авторизоваться с неверным паролем")
    public static ValidatableResponse logInWithNotValidPassword(User user) {
        String requestBodyFromPostman = "{"
                + "\"email\": \"" + user.getEmail() + "\","
                + "\"password\": \"" + "FalsePassword" + "\""
                + "}";

        return given()
                .log()
                .all()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(requestBodyFromPostman)
                .when()
                .post(AUTH_PATH)
                .then()
                .log().all()
                .statusCode(HTTP_UNAUTHORIZED);
    }

    // Проверяем сообщение об ошибке при авторизации пользователя с неверным паролем
    @Step("Проверяем сообщение об ошибке для входа пользователя с неверным паролем")
    public static void verifyErrorMessageForUserLogInWithNotValidPassword(User user, String expectedMessage) {
        String actualMessage = logInWithNotValidPassword(user)
                .extract()
                .path("message");

        assertEquals(expectedMessage, actualMessage);
    }

    @Step("Невозможно авторизоваться с неверным логином и паролем")
    public static ValidatableResponse logInWithNotValidData(User user) {
        String requestBodyFromPostman = "{"
                + "\"email\": \"" + "FalseEmail" + "\","
                + "\"password\": \"" + "FalsePassword" + "\""
                + "}";

        return given()
                .log()
                .all()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(requestBodyFromPostman)
                .when()
                .post(AUTH_PATH)
                .then()
                .log().all()
                .statusCode(HTTP_UNAUTHORIZED);
    }

    // Проверяем сообщение об ошибке при авторизации пользователя с неверным логином и паролем
    @Step("Проверяем сообщение об ошибке для входа пользователя с неверным паролем")
    public static void verifyErrorMessageForUserLogInWithNotValidData(User user, String expectedMessage) {
        String actualMessage = logInWithNotValidPassword(user)
                .extract()
                .path("message");

        assertEquals(expectedMessage, actualMessage);
    }
}