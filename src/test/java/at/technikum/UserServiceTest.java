package at.technikum;

import at.technikum.application.database.Database;
import at.technikum.application.exceptions.NotFoundException;
import at.technikum.application.models.Media;
import at.technikum.application.models.User;
import at.technikum.application.repository.UserRepository;
import at.technikum.application.services.UserService;
import org.junit.jupiter.api.*;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;
    private Connection connection;
    private Integer createdUserId = null;
    private Integer createdMediaId = null;

    @BeforeAll
    void setup() throws SQLException {
        Database.connect();
        connection = Database.getConnection();

        userRepository = new UserRepository();
        userService = new UserService(userRepository);
    }

    @AfterEach
    void cleanup() throws SQLException {
        if (createdUserId != null) {
            deleteFavoritesByUser(createdUserId);
            deleteRatingsByUser(createdUserId);
        }

        if (createdMediaId != null) {
            deleteFavoritesByMedia(createdMediaId);
            deleteRatingsByMedia(createdMediaId);
            deleteMediaById(createdMediaId);
        }

        if (createdUserId != null) {
            deleteUserById(createdUserId);
        }

        createdUserId = null;
        createdMediaId = null;
    }

    private int insertUser(String username, String email, String rawPassword) throws SQLException {
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        User created = userRepository.create(username, email, hash);
        return created.getUserID();
    }

    private int insertMedia(int creatorUserId, String title, String mediaType, String genres, int ageRestriction) throws SQLException {
        String sql = "INSERT INTO media (creator_user_id, title, description, media_type, release_year, genres, age_restriction) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING media_id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, creatorUserId);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, "test desc");
            preparedStatement.setString(4, mediaType);
            preparedStatement.setInt(5, 2025);
            preparedStatement.setString(6, genres);
            preparedStatement.setInt(7, ageRestriction);

            ResultSet resultSet = preparedStatement.executeQuery();
            assertTrue(resultSet.next());
            return resultSet.getInt("media_id");
        }
    }

    private void insertRating(int mediaId, int userId, int stars) throws SQLException {
        String sql = "INSERT INTO rating (media_id, user_id, stars, comment, confirmed, likes, timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, mediaId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, stars);
            preparedStatement.setString(4, "test comment");
            preparedStatement.setBoolean(5, true);
            preparedStatement.setInt(6, 0);
            preparedStatement.setString(7, "now");
            preparedStatement.executeUpdate();
        }
    }

    private void deleteUserById(int userId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM \"user\" WHERE user_id = ?")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        }
    }

    private void deleteMediaById(int mediaId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM media WHERE media_id = ?")) {
            preparedStatement.setInt(1, mediaId);
            preparedStatement.executeUpdate();
        }
    }

    private void deleteRatingsByUser(int userId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM rating WHERE user_id = ?")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        }
    }

    private void deleteRatingsByMedia(int mediaId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM rating WHERE media_id = ?")) {
            preparedStatement.setInt(1, mediaId);
            preparedStatement.executeUpdate();
        }
    }

    private void deleteFavoritesByUser(int userId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM favorite WHERE user_id = ?")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        }
    }

    private void deleteFavoritesByMedia(int mediaId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM favorite WHERE media_id = ?")) {
            preparedStatement.setInt(1, mediaId);
            preparedStatement.executeUpdate();
        }
    }

    @Test
    void getUserByIDTest() throws SQLException {
        String username = "test_user_get_" + UUID.randomUUID();
        String email = "get_" + UUID.randomUUID() + "@test.com";
        createdUserId = insertUser(username, email, "secret123");

        User found = userService.getUserByID(createdUserId);

        assertNotNull(found);
        assertEquals(createdUserId, found.getUserID());
        assertEquals(username, found.getUsername());
        assertEquals(email, found.getEmail());
    }

    @Test
    void updateUserTest() throws SQLException {
        String username = "test_user_update_" + UUID.randomUUID();
        String email = "old_" + UUID.randomUUID() + "@test.com";
        createdUserId = insertUser(username, email, "secret123");
        String newEmail = "new_" + UUID.randomUUID() + "@test.com";
        String newFav = "Action";

        User updated = userService.updateUser(createdUserId, newEmail, newFav);

        assertNotNull(updated);
        assertEquals(createdUserId, updated.getUserID());
        assertEquals(newEmail, updated.getEmail());
        assertEquals(newFav, updated.getFavoriteGenre());
    }

    @Test
    void getLeaderboardTest() throws SQLException {
        String username = "test_user_lb_" + UUID.randomUUID();
        String email = "lb_" + UUID.randomUUID() + "@test.com";
        createdUserId = insertUser(username, email, "secret123");
        createdMediaId = insertMedia(createdUserId, "lb_media_" + UUID.randomUUID(), "movie", "[Action]", 16);
        insertRating(createdMediaId, createdUserId, 5);

        List<User> leaderboard = userService.getLeaderboard();

        assertNotNull(leaderboard);
        assertFalse(leaderboard.isEmpty());
        assertTrue(leaderboard.size() <= 10);
    }

    @Test
    void getRecommendationsTest() throws SQLException {
        String username = "test_user_rec_" + UUID.randomUUID();
        String email = "rec_" + UUID.randomUUID() + "@test.com";
        createdUserId = insertUser(username, email, "secret123");
        int mediaA = insertMedia(createdUserId, "rec_mediaA_" + UUID.randomUUID(), "movie", "[Action]", 16);
        int mediaB = insertMedia(createdUserId, "rec_mediaB_" + UUID.randomUUID(), "movie", "[Action]", 16);
        insertRating(mediaA, createdUserId, 5);
        insertRating(mediaB, createdUserId, 1);

        createdMediaId = mediaB;

        try {
            List<Media> recommendations = userService.getRecommendations(createdUserId, "genre");
            assertNotNull(recommendations);
            assertFalse(recommendations.isEmpty());
        } catch (NotFoundException exception) {
            assertTrue(exception.getMessage().toLowerCase().contains("no recommendations"));
        }
    }
}