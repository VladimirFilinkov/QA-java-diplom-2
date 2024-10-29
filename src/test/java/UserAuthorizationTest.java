import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.*;
@Feature("Авторизация пользователя")
public class UserAuthorizationTest {
    private User testUser;  // Переменная для хранения созданного пользователя
    private String accessToken;  // Переменная для хранения accessToken

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
    @DisplayName("Авторизация пользователя (валидные данные)")
    public void authorizationUserWithValidDataResponse200() {
        testUser = User.randomUser();
        UserCreateClient.createUser(testUser);
        accessToken = UserAuthorizationClient.logInAndGetToken(testUser);
        System.out.println("Access Token: " + accessToken);
    }

    @Test
    @DisplayName("Авторизация пользователя (с неверным логином)")
    public void authorizationUserWithNotValidLoginResponse401() {
        testUser = User.randomUser();
        UserCreateClient.createUser(testUser);

        // Проверка авторизации с неверным логином
        UserAuthorizationClient.logInWithNotValidLogin(testUser);
        String expectedMessage = "email or password are incorrect";
        UserAuthorizationClient.verifyErrorMessageForUserLogInWithNotValidLogin(testUser, expectedMessage);
    }

    @Test
    @DisplayName("Авторизация пользователя (с неверным паролем)")
    public void authorizationUserWithNotValidPasswordResponse401() {
        testUser = User.randomUser();
        UserCreateClient.createUser(testUser);

        // Проверка авторизации с неверным паролем
        UserAuthorizationClient.logInWithNotValidPassword(testUser);
        String expectedMessage = "email or password are incorrect";
        UserAuthorizationClient.verifyErrorMessageForUserLogInWithNotValidPassword(testUser, expectedMessage);
    }

    @Test
    @DisplayName("Авторизация пользователя (с неверным логином и паролем)")
    public void authorizationUserWithNotValidDataResponse401() {
        testUser = User.randomUser();
        UserCreateClient.createUser(testUser);

        // Проверка авторизации с неверным паролем
        UserAuthorizationClient.logInWithNotValidData(testUser);
        String expectedMessage = "email or password are incorrect";
        UserAuthorizationClient.verifyErrorMessageForUserLogInWithNotValidData(testUser, expectedMessage);
    }
}