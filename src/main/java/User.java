import java.time.LocalDateTime;

public class User {
    private String name;
    private String password;
    private String email;

    public User(String login, String password, String email) {
        this.name = login;
        this.password = password;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    // Метод для создания случайного пользователя
    static User randomUser() {
        return new User(
                "Vladimir" + LocalDateTime.now().getNano(),
                "P@ssword123",
                "mail" + LocalDateTime.now().getNano() + "@mail.ru"
        );
    }
}