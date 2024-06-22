package space.b00tload.discord.watchlist2.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.b00tload.discord.watchlist2.config.ConfigurationValues;
import space.b00tload.utils.configuration.Configuration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {

    Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static DatabaseConnector instance;
    private Connection connection;
    private final String url = "jdbc:mysql://" + Configuration.getInstance().get(ConfigurationValues.DATABASE_URL) + ":" + Configuration.getInstance().get(ConfigurationValues.DATABASE_PORT) + "/" + Configuration.getInstance().get(ConfigurationValues.DATABASE_SCHEMA);
    private final String username = Configuration.getInstance().get(ConfigurationValues.DATABASE_USER);
    private final String password = Configuration.getInstance().get(ConfigurationValues.DATABASE_PASSWORD);
    private final String schema = Configuration.getInstance().get(ConfigurationValues.DATABASE_SCHEMA);

    private DatabaseConnector() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);

            try (PreparedStatement pstmt_getTables = prepareStatement("SELECT table_name FROM information_schema.tables WHERE table_schema = ?;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                pstmt_getTables.setString(1, schema);
                try (ResultSet rs_getTables = pstmt_getTables.executeQuery()) {
                    List<String> tables = new ArrayList<>();
                    if (rs_getTables.first()) {
                        do {
                            tables.add(rs_getTables.getString("table_name"));
                        } while (rs_getTables.next());
                    }
                    LOGGER.trace("Found database tables: {}", tables);
                    if (!tables.contains("user")) {
                        LOGGER.debug("creating users table");
                        try (PreparedStatement pstmt_createUserTable = prepareStatement(
                                "CREATE TABLE `" + schema + "`.`user` (`snowflake` bigint PRIMARY KEY NOT NULL, " +
                                        "`username` varchar(255) NOT NULL, " +
                                        "`avatar` varchar(255), " +
                                        "`discorddata` blob NOT NULL, " +
                                        "`registered_since` timestamp NOT NULL, `last_seen` timestamp);")) {
                            pstmt_createUserTable.execute();
                        }
                    }
                    if (!tables.contains("entry")) {
                        LOGGER.debug("creating entry table");
                        try (PreparedStatement pstmt_createUserTable = prepareStatement(
                                "CREATE TABLE `" + schema + "`.`entry` ( " +
                                        "`snowflake` bigint PRIMARY KEY NOT NULL, " +
                                        "`title` varchar(255) NOT NULL, " +
                                        "`hash` varchar(255) UNIQUE NOT NULL, " +
                                        "`type` ENUM ('video', 'show', 'season', 'episode', 'movie') NOT NULL DEFAULT ('video'), " +
                                        "`thumbnail` varchar(255) NOT NULL, " +
                                        "`entered_at` timestamp NOT NULL DEFAULT (now()), " +
                                        "`entered_by` bigint NOT NULL, " +
                                        "`watched` bool NOT NULL DEFAULT false, " +
                                        "`index` int NOT NULL DEFAULT 0, " +
                                        "`parent` bigint DEFAULT null " +
                                        ");");
                             PreparedStatement pstmt_entryForeignKeyUser = prepareStatement(
                                     "ALTER TABLE `" + schema + "`.`entry` ADD FOREIGN KEY (`entered_by`) REFERENCES `" + schema + "`.`user` (`snowflake`);");
                             PreparedStatement pstmt_entryForeignKeyEntry = prepareStatement(
                                     "ALTER TABLE `" + schema + "`.`entry` ADD FOREIGN KEY (`parent`) REFERENCES `" + schema + "`.`entry` (`snowflake`);")) {
                            pstmt_createUserTable.execute();
                            pstmt_entryForeignKeyUser.execute();
                            pstmt_entryForeignKeyEntry.execute();
                        }
                    }
                    if (!tables.contains("providers")) {
                        LOGGER.debug("creating providers table");
                        try (PreparedStatement pstmt_createUserTable = prepareStatement(
                                "CREATE TABLE `" + schema + "`.`providers` ( " +
                                        "`id` integer PRIMARY KEY NOT NULL AUTO_INCREMENT, " +
                                        "`name` varchar(255) NOT NULL, " +
                                        "`url` varchar(255) NOT NULL, " +
                                        "`requires_subscription` bool DEFAULT false " +
                                        ");")) {
                            pstmt_createUserTable.execute();
                        }
                    }
                    if (!tables.contains("likes")) {
                        LOGGER.debug("creating likes table");
                        try (PreparedStatement pstmt_createUserTable = prepareStatement(
                                "CREATE TABLE `" + schema + "`.`likes` ( " +
                                        "`user_id` bigint NOT NULL, " +
                                        "`entry_id` bigint NOT NULL, " +
                                        "`level` ENUM ('not_interested', 'interested', 'interested_watched', 'interested_required') NOT NULL DEFAULT ('not_interested'), " +
                                        "PRIMARY KEY (`user_id`, `entry_id`) " +
                                        ");");
                             PreparedStatement pstmt_likesForeignKeyUser = prepareStatement(
                                     "ALTER TABLE `" + schema + "`.`likes` ADD FOREIGN KEY (`user_id`) REFERENCES `" + schema + "`.`user` (`snowflake`);");
                             PreparedStatement pstmt_likesForeignKeyEntry = prepareStatement(
                                     "ALTER TABLE `" + schema + "`.`likes` ADD FOREIGN KEY (`entry_id`) REFERENCES `" + schema + "`.`entry` (`snowflake`);")) {
                            pstmt_createUserTable.execute();
                            pstmt_likesForeignKeyUser.execute();
                            pstmt_likesForeignKeyEntry.execute();
                        }
                    }
                    if (!tables.contains("availability")) {
                        LOGGER.debug("creating availability table");
                        try (PreparedStatement pstmt_createAvailabilityTable = prepareStatement(
                                "CREATE TABLE `" + schema + "`.`availability` ( " +
                                        "`entry_id` bigint NOT NULL, " +
                                        "`provider_id` integer NOT NULL, " +
                                        "`url` varchar(255) NOT NULL, " +
                                        "PRIMARY KEY (`provider_id`, `entry_id`) " +
                                        ");");
                             PreparedStatement pstmt_availabilityForeignKeyEntry = prepareStatement(
                                     "ALTER TABLE `" + schema + "`.`availability` ADD FOREIGN KEY (`entry_id`) REFERENCES `" + schema + "`.`entry` (`snowflake`);");
                             PreparedStatement pstmt_availibilityForeignKeyProvider = prepareStatement(
                                     "ALTER TABLE `" + schema + "`.`availability` ADD FOREIGN KEY (`provider_id`) REFERENCES `" + schema + "`.`providers` (`id`);")) {
                            pstmt_createAvailabilityTable.execute();
                            pstmt_availabilityForeignKeyEntry.execute();
                            pstmt_availibilityForeignKeyProvider.execute();

                        }
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.error("Database Connection Creation Failed : {}", ex.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static DatabaseConnector getInstance() {
        if (instance == null) {
            throw new UnsupportedOperationException("Database Connector not yet initialized.");
        } else {
            try {
                if (instance.getConnection().isClosed()) {
                    throw new UnsupportedOperationException("Database connection is closed.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public static void init() throws SQLException {
        instance = new DatabaseConnector();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetTime, int resultSetConcurrency) throws SQLException {
        return connection.prepareStatement(sql, resultSetTime, resultSetConcurrency);
    }

    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }
}
