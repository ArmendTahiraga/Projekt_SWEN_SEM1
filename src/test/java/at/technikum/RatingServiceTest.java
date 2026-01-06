package at.technikum;

import at.technikum.application.database.Database;
import at.technikum.application.models.Rating;
import at.technikum.application.repository.MediaRepository;
import at.technikum.application.repository.RatingRepository;
import at.technikum.application.repository.UserRepository;
import at.technikum.application.services.RatingService;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RatingServiceTest {
    private RatingService ratingService;
    private RatingRepository ratingRepository;
    private UserRepository userRepository;
    private MediaRepository mediaRepository;
    private Connection connection;
    private Integer testUserId = null;
    private Integer testMediaId = null;
    private Integer testRatingId = null;

    @BeforeAll
    void setup() throws SQLException {
        Database.connect();
        connection = Database.getConnection();

        userRepository = new UserRepository();
        mediaRepository = new MediaRepository();
        ratingRepository = new RatingRepository();
        ratingService = new RatingService(ratingRepository);

        String username = "test_rating_user_" + UUID.randomUUID();
        String email = "rating_" + UUID.randomUUID() + "@test.com";
        String hash = BCrypt.hashpw("secret123", BCrypt.gensalt());
        testUserId = userRepository.create(username, email, hash).getUserID();
        testMediaId = insertMediaDirect(testUserId, "rating_media_" + UUID.randomUUID());
    }

    @AfterEach
    void cleanup() throws SQLException {
        if (testRatingId != null) {
            deleteRatingById(testRatingId);
            testRatingId = null;
        }
    }

    @AfterAll
    void cleanupAll() throws SQLException {
        if (testMediaId != null) {
            deleteRatingsByMedia(testMediaId);
            deleteFavoritesByMedia(testMediaId);
            deleteMediaById(testMediaId);
            testMediaId = null;
        }

        if (testUserId != null) {
            deleteRatingsByUser(testUserId);
            deleteFavoritesByUser(testUserId);
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

    private int insertRatingDirect(int userId, int mediaId, int stars, String comment) throws SQLException {
        String sql = "INSERT INTO rating (media_id, user_id, stars, comment, confirmed, likes, timestamp) " +
                "VALUES (?, ?, ?, ?, false, 0, ?) RETURNING rating_id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, mediaId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, stars);
            preparedStatement.setString(4, comment);
            preparedStatement.setString(5, "now");
            ResultSet resultSet = preparedStatement.executeQuery();
            assertTrue(resultSet.next());
            return resultSet.getInt("rating_id");
        }
    }

    private int getLikes(int ratingId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT likes FROM rating WHERE rating_id = ?")) {
            preparedStatement.setInt(1, ratingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            assertTrue(resultSet.next());
            return resultSet.getInt("likes");
        }
    }

    private boolean isConfirmed(int ratingId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT confirmed FROM rating WHERE rating_id = ?")) {
            preparedStatement.setInt(1, ratingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            assertTrue(resultSet.next());
            return resultSet.getBoolean("confirmed");
        }
    }

    private Rating getRatingRow(int ratingId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM rating WHERE rating_id = ?")) {
            preparedStatement.setInt(1, ratingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            assertTrue(resultSet.next());
            return new Rating(
                    resultSet.getInt("rating_id"),
                    resultSet.getInt("media_id"),
                    resultSet.getInt("user_id"),
                    resultSet.getInt("stars"),
                    resultSet.getString("comment"),
                    resultSet.getString("timestamp"),
                    resultSet.getInt("likes"),
                    resultSet.getBoolean("confirmed")
            );
        }
    }

    private void deleteRatingById(int ratingId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM rating WHERE rating_id = ?")) {
            preparedStatement.setInt(1, ratingId);
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
    void rateMediaTest() {
        boolean success = ratingService.rateMedia(testUserId, testMediaId, 5, "nice");
        assertTrue(success);

        testRatingId = getLatestRatingId(testUserId, testMediaId);
        assertNotNull(testRatingId);
        assertTrue(testRatingId > 0);
    }

    @Test
    void likeRatingTest() throws SQLException {
        testRatingId = insertRatingDirect(testUserId, testMediaId, 4, "good");

        int before = getLikes(testRatingId);
        boolean success = ratingService.likeRating(testRatingId);
        int after = getLikes(testRatingId);

        assertTrue(success);
        assertEquals(before + 1, after);
    }

    @Test
    void updateRatingTest() throws SQLException {
        testRatingId = insertRatingDirect(testUserId, testMediaId, 2, "meh");

        boolean success = ratingService.updateRating(testRatingId, testUserId, 5, "changed my mind");
        assertTrue(success);

        Rating row = getRatingRow(testRatingId);
        assertEquals(5, row.getStars());
        assertEquals("changed my mind", row.getComment());
    }

    @Test
    void deleteRatingTest() throws SQLException {
        testRatingId = insertRatingDirect(testUserId, testMediaId, 3, "ok");

        boolean success = ratingService.deleteRating(testRatingId, testUserId);
        assertTrue(success);

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT 1 FROM rating WHERE rating_id = ?")) {
            preparedStatement.setInt(1, testRatingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            assertFalse(resultSet.next());
        }

        testRatingId = null;
    }

    @Test
    void confirmRatingCommentTest() throws SQLException {
        testRatingId = insertRatingDirect(testUserId, testMediaId, 4, "confirm me");

        assertFalse(isConfirmed(testRatingId));

        boolean success = ratingService.confirmRatingComment(testRatingId, testUserId);
        assertTrue(success);

        assertTrue(isConfirmed(testRatingId));
    }

    @Test
    void getUserRatingsTest() throws SQLException {
        testRatingId = insertRatingDirect(testUserId, testMediaId, 5, "history");

        List<Rating> ratings = ratingService.getUserRatings(testUserId);

        assertNotNull(ratings);
        assertFalse(ratings.isEmpty());
        assertTrue(ratings.stream().anyMatch(r -> r.getRatingID() == testRatingId));
    }

    private Integer getLatestRatingId(int userId, int mediaId) {
        String sql = "SELECT rating_id FROM rating WHERE user_id = ? AND media_id = ? ORDER BY rating_id DESC LIMIT 1";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, mediaId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? resultSet.getInt("rating_id") : null;
        } catch (SQLException exception) {
            return null;
        }
    }
}