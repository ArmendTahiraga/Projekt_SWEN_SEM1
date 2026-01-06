package at.technikum;

import at.technikum.application.database.Database;
import at.technikum.application.models.Media;
import at.technikum.application.repository.MediaRepository;
import at.technikum.application.repository.UserRepository;
import at.technikum.application.services.MediaService;
import org.junit.jupiter.api.*;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MediaServiceTest {
    private MediaService mediaService;
    private MediaRepository mediaRepository;
    private UserRepository userRepository;
    private Connection connection;
    private Integer testUserId = null;
    private Integer testMediaId = null;

    @BeforeAll
    void setup() throws SQLException {
        Database.connect();
        connection = Database.getConnection();

        userRepository = new UserRepository();
        mediaRepository = new MediaRepository();
        mediaService = new MediaService(mediaRepository);

        String username = "test_media_user_" + UUID.randomUUID();
        String email = "media_" + UUID.randomUUID() + "@test.com";
        String hash = BCrypt.hashpw("secret123", BCrypt.gensalt());
        testUserId = userRepository.create(username, email, hash).getUserID();
    }

    @AfterEach
    void cleanup() throws SQLException {
        if (testMediaId != null) {
            deleteFavoritesByMedia(testMediaId);
            deleteRatingsByMedia(testMediaId);
            deleteMediaById(testMediaId);
            testMediaId = null;
        }
    }

    @AfterAll
    void cleanupAll() throws SQLException {
        if (testUserId != null) {
            deleteFavoritesByUser(testUserId);
            deleteRatingsByUser(testUserId);
            deleteUserById(testUserId);
            testUserId = null;
        }
    }

    private void deleteMediaById(int mediaId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM media WHERE media_id = ?")) {
            preparedStatement.setInt(1, mediaId);
            preparedStatement.executeUpdate();
        }
    }

    private void deleteRatingsByMedia(int mediaId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM rating WHERE media_id = ?")) {
            preparedStatement.setInt(1, mediaId);
            preparedStatement.executeUpdate();
        }
    }

    private void deleteFavoritesByMedia(int mediaId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM favorite WHERE media_id = ?")) {
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

    private void deleteFavoritesByUser(int userId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM favorite WHERE user_id = ?")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        }
    }

    private void deleteUserById(int userId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM \"user\" WHERE user_id = ?")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        }
    }

    // Helper for creating a media directly (sometimes useful)
    private int insertMediaDirect(int creatorUserId, String title, String mediaType, String genres) throws SQLException {
        String sql = "INSERT INTO media (creator_user_id, title, description, media_type, release_year, age_restriction, genres) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING media_id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, creatorUserId);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, "desc");
            preparedStatement.setString(4, mediaType);
            preparedStatement.setInt(5, 2025);
            preparedStatement.setInt(6, 16);
            preparedStatement.setString(7, genres);

            ResultSet resultSet = preparedStatement.executeQuery();
            assertTrue(resultSet.next());
            return resultSet.getInt("media_id");
        }
    }

    @Test
    void createMediaTest() {
        String title = "test_media_" + UUID.randomUUID();
        List<String> genres = List.of("Action", "Drama");

        Media created = mediaService.createMedia(testUserId, title, "Some description", "movie", 2024, 16, genres);

        assertNotNull(created);
        assertTrue(created.getMediaID() > 0);
        assertEquals(testUserId.intValue(), created.getCreatorUserId());
        assertEquals(title, created.getTitle());

        testMediaId = created.getMediaID();
    }

    @Test
    void getMediaByIdTest() throws SQLException {
        testMediaId = insertMediaDirect(testUserId, "get_media_" + UUID.randomUUID(), "movie", "[Action]");

        Media found = mediaService.getMediaById(testMediaId);

        assertNotNull(found);
        assertEquals(testMediaId.intValue(), found.getMediaID());
        assertEquals(testUserId.intValue(), found.getCreatorUserId());
    }

    @Test
    void updateMediaTest() throws SQLException {
        testMediaId = insertMediaDirect(testUserId, "old_title_" + UUID.randomUUID(), "movie", "[Action]");

        String newTitle = "new_title_" + UUID.randomUUID();
        List<String> newGenres = List.of("Action", "Thriller");

        Media updated = mediaService.updateMedia(testMediaId,  newTitle,  "updated desc",  "movie",  2023,  18,  newGenres);

        assertNotNull(updated);
        assertEquals(testMediaId.intValue(), updated.getMediaID());
        assertEquals(newTitle, updated.getTitle());
        assertEquals(18, updated.getAgeRestriction());
    }

    @Test
    void getMediasTest() throws SQLException {
        testMediaId = insertMediaDirect(testUserId,  "genre_filter_" + UUID.randomUUID(),  "movie",  "[Action, Drama]");

        Map<String, Object> filters = Map.of(
        "title", "",
        "genre", "Action",
        "mediaType", "",
        "releaseYear", -1,
        "ageRestriction", -1,
        "rating", -1
        );

        List<Media> medias = mediaService.getMedias(filters);

        assertNotNull(medias);
        assertFalse(medias.isEmpty());
        assertTrue(medias.stream().anyMatch(media -> media.getGenres().contains("Action")));
    }

    @Test
    void deleteMediaTest() throws SQLException {
        testMediaId = insertMediaDirect(testUserId, "delete_me_" + UUID.randomUUID(), "movie", "[Action]");

        mediaService.deleteMedia(testMediaId);

        assertTrue(mediaRepository.getMediaById(testMediaId).isEmpty());

        testMediaId = null;
    }
}