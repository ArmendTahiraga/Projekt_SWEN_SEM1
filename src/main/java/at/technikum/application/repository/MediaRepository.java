package at.technikum.application.repository;

import at.technikum.application.database.Database;
import at.technikum.application.models.Media;
import at.technikum.application.util.MediaUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MediaRepository {
    Connection connection;

    public MediaRepository(){
        this.connection = Database.getConnection();
    }

    public Optional<List<Media>> getMedias(Map<String, ?> filters) throws SQLException {
        StringBuilder statement = new StringBuilder(
                "select media_id, creator_user_id, title, description, media_type, release_year, age_restriction, genres, average_rating from media"
        );

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        String title = filters.get("title").toString();
        if (!title.isBlank()) {
            conditions.add("LOWER(title) LIKE ?");
            params.add("%" + title.toLowerCase() + "%");
        }

        String genre = filters.get("genre").toString();
        if (!genre.isBlank()) {
            conditions.add("LOWER(genres) LIKE ?");
            params.add("%" + genre.toLowerCase() + "%");
        }

        String mediaType = filters.get("mediaType").toString();
        if (!mediaType.isBlank()) {
            conditions.add("media_type = ?");
            params.add(mediaType);
        }

        int releaseYear = Integer.parseInt(filters.get("releaseYear").toString());
        if (releaseYear > 0) {
            conditions.add("release_year = ?");
            params.add(releaseYear);
        }

        int ageRestriction = Integer.parseInt(filters.get("ageRestriction").toString());
        if (ageRestriction >= 0) {
            conditions.add("age_restriction = ?");
            params.add(ageRestriction);
        }

        if (!conditions.isEmpty()) {
            statement.append(" WHERE ");
            statement.append(String.join(" AND ", conditions));
        }

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement.toString());

            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Media> medias = new ArrayList<>();
            while (resultSet.next()) {
                medias.add(MediaUtil.mapMedia(resultSet));
            }

            return medias.isEmpty() ? Optional.empty() : Optional.of(medias);
        } catch (Exception exception){
            System.out.println("Exception in getMedias: " + exception.getMessage());
            throw new SQLException("Error getting medias", exception);
        }
    }

    public Optional<Media> getMediaById(int mediaId) throws SQLException {
        String statement = "select media_id, creator_user_id, title, description, media_type, release_year, age_restriction, genres, average_rating from media where media_id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, mediaId);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? Optional.of(MediaUtil.mapMedia(resultSet)) : Optional.empty();
        } catch (SQLException exception){
            throw new SQLException("Error getting media by ID", exception);
        }
    }

    public Optional<Media> createMedia(Media media) throws SQLException {
        String statement = "insert into media (creator_user_id, title, description, media_type, release_year, age_restriction, genres) values (?, ?, ?, ?, ?, ?, ?) returning media_id, creator_user_id, title, description, media_type, release_year, age_restriction, genres, average_rating";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, media.getCreatorUserId());
            preparedStatement.setString(2, media.getTitle());
            preparedStatement.setString(3, media.getDescription());
            preparedStatement.setString(4, media.getMediaType());
            preparedStatement.setInt(5, media.getReleaseYear());
            preparedStatement.setInt(6, media.getAgeRestriction());
            preparedStatement.setString(7, media.getGenres().toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? Optional.of(MediaUtil.mapMedia(resultSet)) : Optional.empty();
        } catch (SQLException exception){
            throw new SQLException("Error creating media", exception);
        }
    }

    public void deleteMedia(int mediaId) throws SQLException {
        String statement = "delete from media where media_id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, mediaId);
            preparedStatement.executeUpdate();
        } catch (SQLException exception){
            throw new SQLException("Error deleting media", exception);
        }
    }

    public Optional<Media> updateMedia(Media media) throws SQLException {
        String statement = "update media set title = ?, description = ?, media_type = ?, release_year = ?, age_restriction = ?, genres = ? where media_id = ? returning media_id, creator_user_id, title, description, media_type, release_year, age_restriction, genres, average_rating";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1, media.getTitle());
            preparedStatement.setString(2, media.getDescription());
            preparedStatement.setString(3, media.getMediaType());
            preparedStatement.setInt(4, media.getReleaseYear());
            preparedStatement.setInt(5, media.getAgeRestriction());
            preparedStatement.setString(6, media.getGenres().toString());
            preparedStatement.setInt(7, media.getMediaID());

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? Optional.of(MediaUtil.mapMedia(resultSet)) : Optional.empty();
        } catch (SQLException exception){
            throw new SQLException("Error updating media", exception);
        }
    }
}
