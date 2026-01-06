package at.technikum;

import at.technikum.application.database.Database;
import at.technikum.application.models.Media;
import at.technikum.application.repository.FavoritesRepository;
import at.technikum.application.repository.UserRepository;
import at.technikum.application.services.FavoritesService;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FavoritesServiceTest {
    private FavoritesService favoritesService;
    private FavoritesRepository favoritesRepository;
    private UserRepository userRepository;
    private Connection connection;
    private Integer testUserId = null;
    private Integer testMediaId = null;

    @BeforeAll
    void setup() throws SQLException {
        Database.connect();
        connection = Database.getConnection();

        userRepository = new UserRepository();
        favoritesRepository = new FavoritesRepository();
        favoritesService = new FavoritesService(favoritesRepository);

        String username = "test_fav_user_" + UUID.randomUUID();
        String email = "fav_" + UUID.randomUUID() + "@test.com";
        String hash = BCrypt.hashpw("secret123", BCrypt.gensalt());
        testUserId = userRepository.create(username, email, hash).getUserID();
        testMediaId = insertMediaDirect(testUserId, "fav_media_" + UUID.randomUUID());
    }

    @AfterEach
    void cleanup() throws SQLException {
        deleteFavorite(testUserId, testMediaId);
    }

    @AfterAll
    void cleanupAll() throws SQLException {
        if (testMediaId != null) {
            deleteFavoritesByMedia(testMediaId);
            deleteRatingsByMedia(testMediaId);
            deleteMediaById(testMediaId);
            testMediaId = null;
        }
        if (testUserId != null) {
            deleteFavoritesByUser(testUserId);
            deleteRatingsByUser(testUserId);
            deleteUserById(testUserId);
            testUserId = null;
        }
    }
    private int insertMediaDirect(int creatorUserId, String title) throws SQLException {
        String sql = "INSERT INTO media (creator_user_id, title, description, media_type, release_year, age_restriction, genres) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING media_id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, creatorUserId);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, "desc");
            preparedStatement.setString(4, "movie");
            preparedStatement.setInt(5, 2025);
            preparedStatement.setInt(6, 16);
            preparedStatement.setString(7, "[Action]");
            ResultSet resultSet = preparedStatement.executeQuery();
            assertTrue(resultSet.next());
            return resultSet.getInt("media_id");
        }
    }

    private void deleteFavorite(int userId, int mediaId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM favorite WHERE user_id = ? AND media_id = ?")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, mediaId);
            preparedStatement.executeUpdate();
        }
    }

    private void deleteFavoritesByMedia(int mediaId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM favorite WHERE media_id = ?")) {
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

    private void deleteRatingsByMedia(int mediaId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM rating WHERE media_id = ?")) {
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

    private void deleteMediaById(int mediaId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM media WHERE media_id = ?")) {
            preparedStatement.setInt(1, mediaId);
            preparedStatement.executeUpdate();
        }
    }

    private void deleteUserById(int userId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM \"user\" WHERE user_id = ?")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        }
    }

    @Test
    void addMediaToFavoritesTest() {
        boolean success = favoritesService.addMediaToFavorites(testUserId, testMediaId);
        assertTrue(success);
    }

    @Test
    void getUserFavoritesTest() throws SQLException {
        assertTrue(favoritesService.addMediaToFavorites(testUserId, testMediaId));

        List<Media> favorites = favoritesService.getUserFavorites(testUserId);

        assertNotNull(favorites);
        assertFalse(favorites.isEmpty());
        assertTrue(favorites.stream().anyMatch(m -> m.getMediaID() == testMediaId));
    }

    @Test
    void removeMediaFromFavoritesTest() {
        assertTrue(favoritesService.addMediaToFavorites(testUserId, testMediaId));
        boolean removed = favoritesService.removeMediaFromFavorites(testUserId, testMediaId);
        assertTrue(removed);
    }
}