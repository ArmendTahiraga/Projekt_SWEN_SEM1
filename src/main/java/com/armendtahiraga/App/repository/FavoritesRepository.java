package com.armendtahiraga.App.repository;

import com.armendtahiraga.App.database.Database;

import java.sql.Connection;

public class FavoritesRepository {
    Connection connection;

    public FavoritesRepository(){
        this.connection = Database.getConnection();
    }
}
