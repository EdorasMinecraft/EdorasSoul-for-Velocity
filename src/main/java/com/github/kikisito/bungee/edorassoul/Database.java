package com.github.kikisito.bungee.edorassoul;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Clase para gestionar el pool de conexiones a la base de datos
 */
public class Database {

    private final HikariDataSource dataSource;

    public Database() {
        final String host = Main.config.getString("database.host");
        final String port = Main.config.getString("database.port");
        final String database = Main.config.getString("database.dbname");
        final String username = Main.config.getString("database.username");
        final String password = Main.config.getString("database.password");

        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");

        // Iniciar el DataSource
        this.dataSource = new HikariDataSource(hikariConfig);
    }

    /**
     * Devuelve una conexión a la base de datos del pool
     * @return Connection
     * @throws SQLException si la conexión está cerrada
     */
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }
}
