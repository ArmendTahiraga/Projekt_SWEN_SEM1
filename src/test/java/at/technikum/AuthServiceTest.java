package at.technikum;

import at.technikum.application.database.Database;
import at.technikum.application.models.User;
import at.technikum.application.repository.UserRepository;
import at.technikum.application.services.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthServiceTest {
    private AuthService authService;
    private UserRepository userRepository;
    private Connection connection;

    @BeforeAll
    void setup() throws SQLException {
        Database.connect();
        connection = Database.getConnection();

        userRepository = new UserRepository();
        authService = new AuthService(userRepository);
    }

    @AfterEach
    void cleanup() throws SQLException {
        deleteUserIfExists("test_register_user");
        deleteUserIfExists("test_login_user");
    }

    private void deleteUserIfExists(String username) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM \"user\" WHERE username = ?")) {
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        }
    }

    @Test
    void registerTest() throws SQLException {
        String username = "test_register_user";
        String email = "reg_" + UUID.randomUUID() + "@test.com";
        String password = "secret123";

        User created = authService.register(username, email, password);

        assertNotNull(created);
        assertEquals(username, created.getUsername());
        assertEquals(username + "-mrpToken", created.getToken());
        assertTrue(created.getUserID() > 0);
    }

    @Test
    void loginTest() throws SQLException {
        String username = "test_login_user";
        String email = "login_" + UUID.randomUUID() + "@test.com";
        String password = "secret123";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        userRepository.create(username, email, hash);

        User loggedIn = authService.login(username, password);

        assertNotNull(loggedIn);
        assertEquals(username, loggedIn.getUsername());
        assertEquals(username + "-mrpToken", loggedIn.getToken());
    }
}