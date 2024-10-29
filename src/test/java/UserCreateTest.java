import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static java.net.HttpURLConnection.*;

@Feature("Создание пользователя")
public class UserCreateTest {
    private User testUser;
    private String accessToken;

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
    @DisplayName("Регистрация нового пользователя (все обязательные поля заполнены)")
    public void createUserWithValidDataResponse200() {
        testUser = User.randomUser();
        accessToken = UserCreateClient.createUser(testUser);
    }

    @Test
    @DisplayName("Попытка зарегистрировать пользователя с одинаковыми данными")
    public void registerUserWithDuplicateDataResponse403() {
        testUser = User.randomUser();
        accessToken = UserCreateClient.createUser(testUser);

        ValidatableResponse response = UserCreateClient.verifyDuplicateUserRegistration(testUser);
        String expectedMessage = "User with such email already exists";
        UserCreateClient.verifyErrorMessage(response, expectedMessage);
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    public void createUserWithoutLoginResponse403() {
        testUser = User.randomUser();
        ValidatableResponse response = UserCreateClient.createUserWithoutLogin(testUser)
                .statusCode(HTTP_FORBIDDEN);
        UserCreateClient.verifyErrorMessage(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void createUserWithoutEmailResponse403() {
        testUser = User.randomUser();
        ValidatableResponse response = UserCreateClient.createUserWithoutEmail(testUser)
                .statusCode(HTTP_FORBIDDEN);
        UserCreateClient.verifyErrorMessage(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    public void createUserWithoutPasswordResponse403() {
        testUser = User.randomUser();
        ValidatableResponse response = UserCreateClient.createUserWithoutPassword(testUser)
                .statusCode(HTTP_FORBIDDEN);
        UserCreateClient.verifyErrorMessage(response, "Email, password and name are required fields");
    }
}