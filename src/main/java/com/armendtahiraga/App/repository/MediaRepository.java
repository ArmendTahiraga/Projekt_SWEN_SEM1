package com.armendtahiraga.App.repository;

import com.armendtahiraga.App.database.Database;
import com.armendtahiraga.App.models.Media;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MediaRepository {
    Connection connection;

    public MediaRepository(){
        this.connection = Database.getConnection();
    }

    public Optional<List<Media>> getMedias() throws SQLException {
        String statement = "select media_id, creator_user_id, title, description, media_type, release_year, age_restriction, genres from media";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Media> medias = new ArrayList<>();
            while (resultSet.next()){
                medias.add(mapMedia(resultSet));
            }

            return medias.isEmpty() ? Optional.empty() : Optional.of(medias);
        } catch (SQLException exception){
            throw new SQLException("Error getting medias", exception);
        }
    }

    public Optional<Media> getMediaById(int mediaId) throws SQLException {
        String statement = "select media_id, creator_user_id, title, description, media_type, release_year, age_restriction, genres from media where media_id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, mediaId);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? Optional.of(mapMedia(resultSet)) : Optional.empty();
        } catch (SQLException exception){
            throw new SQLException("Error getting media by ID", exception);
        }
    }

    public Optional<Media> createMedia(int creatorUserId, String title, String description, String mediaType, int releaseYear, int ageRestriction, List<String> genres) throws SQLException {
        String statement = "insert into media (creator_user_id, title, description, media_type, release_year, age_restriction, genres) values (?, ?, ?, ?, ?, ?, ?) returning media_id, creator_user_id, title, description, media_type, release_year, age_restriction, genres";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, creatorUserId);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, mediaType);
            preparedStatement.setInt(5, releaseYear);
            preparedStatement.setInt(6, ageRestriction);
            preparedStatement.setString(7, genres.toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? Optional.of(mapMedia(resultSet)) : Optional.empty();
        } catch (SQLException exception){
            throw new SQLException("Error creating media", exception);
        }
    }

    private Media mapMedia(ResultSet resultSet) throws SQLException {
        int mediaId = resultSet.getInt("media_id");
        int creatorUserId = resultSet.getInt("creator_user_id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        String mediaType = resultSet.getString("media_type");
        int releaseYear = resultSet.getInt("release_year");
        int ageRestriction = resultSet.getInt("age_restriction");
        List<String> genres = turnGenresStringToList(resultSet.getString("genres"));

        return new Media(mediaId, creatorUserId, title, description, mediaType, releaseYear, ageRestriction, genres);
    }

    private List<String> turnGenresStringToList(String genresString){
        List<String> genres = new ArrayList<>();

        if (genresString == null || genresString.equals("[]")) {
            return genres;
        }

        genresString = genresString.substring(1, genresString.length() - 1);
        String[] genresArray = genresString.split(", ");
        for (String genre : genresArray) {
            genres.add(genre);
        }

        return genres;
    }
}
