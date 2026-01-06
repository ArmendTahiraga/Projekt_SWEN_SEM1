package at.technikum.application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection connection;

    public static void connect(){
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mrp", "mrp_user", "mrp_pass");
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static Connection getConnection(){
        return connection;
    }
}
