import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.*;
import static org.junit.Assert.assertEquals;

public class UserUpdateTest {
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
    @DisplayName("Получаем данные пользователя (авторизация пройдена)")
    public void getUserDataWithAuthorizationResponse200() {
        testUser = User.randomUser();
        UserCreateClient.createUser(testUser);
        accessToken = UserAuthorizationClient.logInAndGetToken(testUser);

        // Отправляем запрос для получения данных пользователя
        ValidatableResponse response = UserUpdateClient.getUserData(accessToken);
        response.statusCode(HTTP_OK);
    }

    @Test
    @DisplayName("Изменение имени пользователя (авторизация пройдена)")
    public void updateUserNameResponse200() {
        // Создаём и регистрируем пользователя
        testUser = User.randomUser();
        UserCreateClient.createUser(testUser);

        //Проходим авторизацию и получаем токен
        accessToken = UserAuthorizationClient.logInAndGetToken(testUser);

        //Генерируем новое имя для изменения
        String newUserName = User.randomUser().getName();

        //Отправляем PATCH запрос для изменения имени
        ValidatableResponse updateResponse = UserUpdateClient.updateUserName(accessToken, newUserName);
        updateResponse.statusCode(HTTP_OK);

        //Gолучаем данные пользователя
        ValidatableResponse updatedResponse = UserUpdateClient.getUserData(accessToken);
        updatedResponse.statusCode(HTTP_OK);

        //Извлекаем имя из ответа
        String updatedUserName = updatedResponse.extract().path("user.name");

        //Сравниваем новое имя с именем, полученным в ответе
        assertEquals("Имя пользователя не совпадает с ожидаемым значением", newUserName, updatedUserName);
    }

    @Test
    @DisplayName("Изменение email пользователя (авторизация пройдена)")
    public void updateUserEmailResponse200() {
        // Создаём и регистрируем пользователя
        testUser = User.randomUser();
        UserCreateClient.createUser(testUser);

        // Проходим авторизацию и получаем токен
        accessToken = UserAuthorizationClient.logInAndGetToken(testUser);

        // Генерируем новый email для изменения
        String newEmail = User.randomUser().getEmail();

        // Отправляем PATCH запрос для изменения email
        ValidatableResponse updateResponse = UserUpdateClient.updateUserEmail(accessToken, newEmail);
        updateResponse.statusCode(HTTP_OK);

        // Получаем обновленные данные пользователя
        ValidatableResponse updatedResponse = UserUpdateClient.getUserData(accessToken);
        updatedResponse.statusCode(HTTP_OK);

        // Извлекаем email из ответа
        String updatedUserEmail = updatedResponse.extract().path("user.email");

        // Сравниваем новый email с email, полученным в ответе
        assertEquals("Email пользователя не совпадает с ожидаемым значением", newEmail, updatedUserEmail);
    }

    @Test
    @DisplayName("Изменение имени и email пользователя (авторизация пройдена)")
    public void updateUserNameAndEmailResponse200() {
        // Создаём и регистрируем пользователя
        testUser = User.randomUser();
        UserCreateClient.createUser(testUser);

        // Проходим авторизацию и получаем токен
        accessToken = UserAuthorizationClient.logInAndGetToken(testUser);

        // Генерируем новое имя и email для изменения
        String newUserName = User.randomUser().getName();
        String newEmail = User.randomUser().getEmail();

        // Отправляем PATCH запрос для изменения имени и email
        ValidatableResponse updateResponse = UserUpdateClient.updateUserNameAndEmail(accessToken, newUserName, newEmail);
        updateResponse.statusCode(HTTP_OK);

        // Получаем обновленные данные пользователя
        ValidatableResponse updatedResponse = UserUpdateClient.getUserData(accessToken);
        updatedResponse.statusCode(HTTP_OK);

        // Извлекаем имя и email из ответа
        String updatedUserName = updatedResponse.extract().path("user.name");
        String updatedUserEmail = updatedResponse.extract().path("user.email");

        // Сравниваем новое имя и email с данными, полученными в ответе
        assertEquals("Имя пользователя не совпадает с ожидаемым значением", newUserName, updatedUserName);
        assertEquals("Email пользователя не совпадает с ожидаемым значением", newEmail, updatedUserEmail);
    }

    @Test
    @DisplayName("Изменение email на уже существующий (авторизация пройдена)")
    public void updateUserEmailToExistingEmailShouldReturn403() {
        testUser = User.randomUser();
        UserCreateClient.createUser(testUser);

        // Авторизуем первого пользователя и сохраняем токен и email
        String token1 = UserAuthorizationClient.logInAndGetToken(testUser);
        String email1 = testUser.getEmail();

        // Создаем и регистрируем второго пользователя
        User secondUser = User.randomUser();
        UserCreateClient.createUser(secondUser);

        // Авторизуем второго пользователя и сохраняем токен
        String token2 = UserAuthorizationClient.logInAndGetToken(secondUser);

        // Отправляем PATCH запрос от второго пользователя на смену email на email первого пользователя
        ValidatableResponse updateResponse = UserUpdateClient.updateUserEmail(token2, email1);

        // Проверяем, что статус код 403
        updateResponse.statusCode(HTTP_FORBIDDEN);

        // Проверяем сообщение об ошибке
        UserUpdateClient.validateEmailAlreadyExistsError(updateResponse);

        // Удаляем обоих пользователей
        UserCreateClient.deleteUser(token1).statusCode(HTTP_ACCEPTED);
        UserCreateClient.deleteUser(token2).statusCode(HTTP_ACCEPTED);
    }

    @Test
    @DisplayName("Получение данных пользователя (без авторизации)")
    public void getUserDataWithoutAuthorizationResponse401() {
        // Создаём и регистрируем пользователя
        testUser = User.randomUser();
        accessToken = UserCreateClient.createUser(testUser);

        // Отправляем запрос без авторизации
        ValidatableResponse response = UserUpdateClient.getUserData("");
        response.statusCode(HTTP_UNAUTHORIZED); // Проверяем статус код

        // Проверяем сообщение об ошибке
        UserUpdateClient.validateUnauthorizedError(response);
    }

    @Test
    @DisplayName("Изменение имени пользователя (без авторизации)")
    public void updateUserNameWithoutAuthorizationResponse401() {
        // Создаём и регистрируем пользователя
        testUser = User.randomUser();
        accessToken = UserCreateClient.createUser(testUser);

        // Генерируем новое имя для изменения
        String newUserName = User.randomUser().getName();

        // Отправляем PATCH запрос без авторизации
        ValidatableResponse updateResponse = UserUpdateClient.updateUserName("", newUserName);
        updateResponse.statusCode(HTTP_UNAUTHORIZED);

        // Проверяем сообщение об ошибке
        UserUpdateClient.validateUnauthorizedError(updateResponse);
    }

    @Test
    @DisplayName("Изменение email пользователя (без авторизации)")
    public void updateUserEmailWithoutAuthorizationResponse401() {
        // Создаём и регистрируем пользователя
        testUser = User.randomUser();
        accessToken = UserCreateClient.createUser(testUser);

        // Генерируем новый email для изменения
        String newEmail = User.randomUser().getEmail();

        // Отправляем PATCH запрос без авторизации
        ValidatableResponse updateResponse = UserUpdateClient.updateUserEmail("", newEmail); //
        updateResponse.statusCode(HTTP_UNAUTHORIZED);

        // Проверяем сообщение об ошибке
        UserUpdateClient.validateUnauthorizedError(updateResponse);

    }

    @Test
    @DisplayName("Изменение имени и email пользователя (без авторизации)")
    public void updateUserNameAndEmailWithoutAuthorizationResponse401() {
        // Создаём и регистрируем пользователя
        testUser = User.randomUser();
        UserCreateClient.createUser(testUser);

        // Генерируем новое имя и email для изменения
        String newUserName = User.randomUser().getName();
        String newEmail = User.randomUser().getEmail();

        // Отправляем PATCH запрос без авторизации
        ValidatableResponse updateResponse = UserUpdateClient.updateUserNameAndEmail("", newUserName, newEmail);
        updateResponse.statusCode(HTTP_UNAUTHORIZED);

        // Проверяем сообщение об ошибке
        UserUpdateClient.validateUnauthorizedError(updateResponse);
    }

    @Test
    @DisplayName("Попытка изменить email на уже существующий (без авторизации)")
    public void updateUserEmailToExistingEmailWithoutAuthorizationShouldReturn401() {
        // Создаём первого пользователя и сохраняем его email
        testUser = User.randomUser();
        String token1 = UserCreateClient.createUser(testUser);
        String email1 = testUser.getEmail();

        // Создаём второго пользователя и пытаемся изменить его email на email первого пользователя без авторизации
        User secondUser = User.randomUser();
        String token2 = UserCreateClient.createUser(secondUser);

        // Отправляем PATCH запрос для изменения email без авторизации
        ValidatableResponse updateResponse = UserUpdateClient.updateUserEmail("", email1);
        updateResponse.statusCode(HTTP_UNAUTHORIZED);

        // Проверяем сообщение об ошибке
        UserUpdateClient.validateUnauthorizedError(updateResponse);

        // Удаляем обоих пользователей
        UserCreateClient.deleteUser(token1).statusCode(HTTP_ACCEPTED);
        UserCreateClient.deleteUser(token2).statusCode(HTTP_ACCEPTED);
    }
}






