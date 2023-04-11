package com.osh.service.impl;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.osh.database.config.DatabaseConfig;
import com.osh.service.IDatabaseService;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseServiceImpl implements IDatabaseService {

    private final ConnectionSource connectionSource;

    public DatabaseServiceImpl(DatabaseConfig config) throws SQLException {
        String databaseUrl = "jdbc:postgresql://" + config.getHost() + ":" + config.getPort() + "/" + config.getName();
        // create a connection source to our database
        connectionSource = new JdbcConnectionSource(databaseUrl, config.getUsername(), config.getPassword());
    }

    @Override
    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }
}
