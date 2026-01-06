package at.technikum.application.repository;

import at.technikum.application.database.Database;
import at.technikum.application.models.Media;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FavoritesRepository {
    Connection connection;

    public FavoritesRepository(){
        this.connection = Database.getConnection();
    }

    public Optional<List<Media>> getUserFavorites(int userID) throws SQLException{
        String statement = "select media.* from favorite join media on favorite.media_id = media.media_id where user_id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, userID);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Media> favoriteMedias = new ArrayList<>();
            while (resultSet.next()) {
                favoriteMedias.add(mapMedia(resultSet));
            }

            return favoriteMedias.isEmpty() ? Optional.empty() : Optional.of(favoriteMedias);
        } catch (Exception exception){
            throw new SQLException("Error fetching user favorites", exception);
        }
    }

    public boolean addMediaToFavorites(int userID, int mediaID) {
        String statement = "insert into favorite (user_id, media_id) values (?, ?)";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, mediaID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception exception){
            return false;
        }
    }

    public boolean removeMediaFromFavorites(int userID, int mediaID) {
        String statement = "delete from favorite where user_id = ? and media_id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, mediaID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception exception){
            return false;
        }
    }

    private Media mapMedia(ResultSet resultSet) throws SQLException {
        int mediaID = resultSet.getInt("media_id");
        int creatorUserId = resultSet.getInt("creator_user_id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        String mediaType = resultSet.getString("media_type");
        int releaseYear = resultSet.getInt("release_year");
        int ageRestriction = resultSet.getInt("age_restriction");
        List<String> genres = turnGenresStringToList(resultSet.getString("genres"));

        return new Media(mediaID, creatorUserId, title, description, mediaType, releaseYear, ageRestriction, genres);
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
