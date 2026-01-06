package at.technikum.application.util;

import at.technikum.application.models.Media;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MediaUtil {
    public static JsonObject mediaToJson(Media media){
        JsonObject json = new JsonObject();
        json.addProperty("mediaID", media.getMediaID());
        json.addProperty("creatorUserId", media.getCreatorUserId());
        json.addProperty("title", media.getTitle());
        json.addProperty("description", media.getDescription());
        json.addProperty("mediaType", media.getMediaType());
        json.addProperty("releaseYear", media.getReleaseYear());
        json.addProperty("ageRestriction", media.getAgeRestriction());

        if (media.getGenres() != null) {
            JsonArray genresArray = new JsonArray();
            for (String genre : media.getGenres()) {
                genresArray.add(genre);
            }
            json.add("genres", genresArray);
        } else {
            json.add("genres", new JsonArray());
        }

        return json;
    }

    public static Media mapMedia(ResultSet resultSet) throws SQLException {
        int mediaID = resultSet.getInt("media_id");
        int creatorUserId = resultSet.getInt("creator_user_id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        String mediaType = resultSet.getString("media_type");
        int releaseYear = resultSet.getInt("release_year");
        int ageRestriction = resultSet.getInt("age_restriction");
        List<String> genres = turnGenresStringToList(resultSet.getString("genres"));
        float avg = resultSet.getFloat("average_rating");

        return new Media(mediaID, creatorUserId, title, description, mediaType, releaseYear, ageRestriction, genres, avg);
    }

    private static List<String> turnGenresStringToList(String genresString){
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
