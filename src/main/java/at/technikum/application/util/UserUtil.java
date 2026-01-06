package at.technikum.application.util;

import at.technikum.application.models.User;
import com.google.gson.JsonObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserUtil {
    public static JsonObject turnUserToJson(User user){
        JsonObject userJson = new JsonObject();
        userJson.addProperty("id", user.getUserID());
        userJson.addProperty("username", user.getUsername());
        userJson.addProperty("email", user.getEmail());
        userJson.addProperty("favoriteGenre", user.getFavoriteGenre());

        return userJson;
    }

    public static User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User(
                resultSet.getInt("user_id"),
                resultSet.getString("username"),
                resultSet.getString("password_hash"),
                resultSet.getString("email"),
                resultSet.getString("favorite_genre")
        );

        return user;
    }
}
