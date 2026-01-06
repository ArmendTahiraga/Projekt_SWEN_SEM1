package at.technikum.application.util;

import at.technikum.application.models.Rating;
import com.google.gson.JsonObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingUtil {
    public static JsonObject ratingToJson(Rating rating){
        JsonObject json = new JsonObject();
        json.addProperty("ratingID", rating.getRatingID());
        json.addProperty("mediaID", rating.getMediaID());
        json.addProperty("userID", rating.getUserID());
        json.addProperty("stars", rating.getStars());
        json.addProperty("comment", rating.getComment());
        json.addProperty("timestamp", rating.getTimestamp());
        json.addProperty("confirmed", rating.isConfirmed());
        json.addProperty("likes", rating.getLikes());

        return json;
    }

    public static Rating mapRating(ResultSet resultSet) throws SQLException {
        int ratingId = resultSet.getInt("rating_id");
        int userId = resultSet.getInt("user_id");
        int mediaId = resultSet.getInt("media_id");
        int stars = resultSet.getInt("stars");
        String timestamp = resultSet.getString("timestamp");
        String comment = resultSet.getString("comment");
        int likes = resultSet.getInt("likes");
        boolean confirmed = resultSet.getBoolean("confirmed");

        return new Rating(ratingId, mediaId, userId, stars, comment, timestamp, likes, confirmed);
    }
}
