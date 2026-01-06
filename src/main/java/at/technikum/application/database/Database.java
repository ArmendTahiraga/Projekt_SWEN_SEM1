package at.technikum.application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection connection;

    public static void connect(){
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://db.puafiywkuaruycrwjdsk.supabase.co:5432/postgres?user=postgres&password=713_ArmendTahiraga_SWEN");
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static Connection getConnection(){
        return connection;
    }
}
