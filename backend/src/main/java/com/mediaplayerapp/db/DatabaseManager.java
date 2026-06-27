package com.mediaplayerapp.db;

import com.mediaplayerapp.shared.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {}

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void init() {
        try {
            File appDir = new File(AppConstants.APP_DIR);
            if (!appDir.exists()) {
                appDir.mkdirs();
            }

            String url = "jdbc:h2:file:" + AppConstants.DB_PATH + ";AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE";
            connection = DriverManager.getConnection(url, "waveline", "");
            log.info("Connected to H2 database at {}", AppConstants.DB_PATH);
            createSchema();
        } catch (SQLException e) {
            log.error("Failed to initialize database", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private void createSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tracks (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(500) NOT NULL,
                    artist VARCHAR(500) DEFAULT 'Unknown Artist',
                    album VARCHAR(500) DEFAULT 'Unknown Album',
                    genre VARCHAR(200) DEFAULT '',
                    year VARCHAR(10) DEFAULT '',
                    file_path VARCHAR(2000) NOT NULL UNIQUE,
                    duration_millis BIGINT DEFAULT 0,
                    track_number INT DEFAULT 0,
                    file_size BIGINT DEFAULT 0,
                    cover_art_path VARCHAR(2000) DEFAULT '',
                    play_count INT DEFAULT 0,
                    favourite BOOLEAN DEFAULT FALSE,
                    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS playlists (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(500) NOT NULL,
                    description VARCHAR(1000) DEFAULT '',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS playlist_tracks (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    playlist_id BIGINT NOT NULL REFERENCES playlists(id) ON DELETE CASCADE,
                    track_id BIGINT NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
                    position INT NOT NULL DEFAULT 0,
                    UNIQUE(playlist_id, track_id)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS eq_presets (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(200) NOT NULL,
                    gains VARCHAR(500) NOT NULL,
                    built_in BOOLEAN DEFAULT FALSE
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS app_settings (
                    key VARCHAR(200) PRIMARY KEY,
                    value VARCHAR(2000)
                )
            """);

            log.info("Database schema created/verified");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log.info("Database connection closed");
            }
        } catch (SQLException e) {
            log.error("Error closing database connection", e);
        }
    }
}
