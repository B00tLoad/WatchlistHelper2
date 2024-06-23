package space.b00tload.discord.watchlist2.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.jdbcx.JdbcDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.b00tload.discord.watchlist2.config.ConfigurationValues;
import space.b00tload.utils.configuration.Configuration;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DatabaseConnector {

    public static class Persistent {
        Logger LOGGER = LoggerFactory.getLogger(this.getClass());

        private static Persistent INSTANCE;
        private HikariDataSource dataSource;
        private final String url = "jdbc:mysql://" + Configuration.getInstance().get(ConfigurationValues.DATABASE_URL) + ":" + Configuration.getInstance().get(ConfigurationValues.DATABASE_PORT) + "/" + Configuration.getInstance().get(ConfigurationValues.DATABASE_SCHEMA);
        private final String username = Configuration.getInstance().get(ConfigurationValues.DATABASE_USER);
        private final String password = Configuration.getInstance().get(ConfigurationValues.DATABASE_PASSWORD);
        private final String schema = Configuration.getInstance().get(ConfigurationValues.DATABASE_SCHEMA);

        private Persistent() throws SQLException {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                HikariConfig hikariConfig = new HikariConfig();
                hikariConfig.setJdbcUrl(url);
                hikariConfig.setUsername(username);
                hikariConfig.setPassword(password);
                hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
                hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
                hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                hikariConfig.setPoolName("watchlistHelperDatabasePool");
                dataSource = new HikariDataSource(hikariConfig);

                try (PreparedStatement pstmt_getTables = prepareStatement("SELECT table_name FROM information_schema.tables WHERE table_schema = ?;")) {
                    pstmt_getTables.setString(1, schema);
                    try (ResultSet rs_getTables = pstmt_getTables.executeQuery()) {
                        List<String> tables = new ArrayList<>();
                        if (rs_getTables.first()) {
                            do {
                                tables.add(rs_getTables.getString("table_name").toLowerCase());
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

        public Connection getConnection() throws SQLException {
            return dataSource.getConnection();
        }

        public static Persistent getInstance() {
            if (INSTANCE == null) {
                throw new UnsupportedOperationException("Database Connector not yet initialized.");
            } else {
                try {
                    if (INSTANCE.getConnection().isClosed()) {
                        throw new UnsupportedOperationException("Database connection is closed.");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return INSTANCE;
        }

        public static void init() throws SQLException {
            INSTANCE = new Persistent();
        }

        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        }

        public Statement createStatement() throws SQLException {
            return getConnection().createStatement();
        }
    }

    public static class Cache {
        Logger LOGGER = LoggerFactory.getLogger(this.getClass());
        static Cache INSTANCE;
        HikariDataSource dataSource;

        public Cache() {
            HikariConfig config = new HikariConfig();
            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL("jdbc:h2:mem:watchlisthelper;DB_CLOSE_DELAY=-1");
            config.setDataSource(ds);
            config.setPoolName("watchlisthelperCachePool");
            dataSource = new HikariDataSource(config);

            try (Connection con = getConnection();
                 PreparedStatement pstmt_getTables = con.prepareStatement(
                         "SELECT table_name FROM information_schema.tables WHERE table_schema = ?;",
                         ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                pstmt_getTables.setString(1, "PUBLIC");
                try (ResultSet rs_getTables = pstmt_getTables.executeQuery()) {
                    List<String> tables = new ArrayList<>();
                    if (rs_getTables.first()) {
                        do {
                            tables.add(rs_getTables.getString("table_name").toLowerCase());
                        } while (rs_getTables.next());
                    }
                    LOGGER.trace("Found database tables: {}", tables);
                    DiscordStateCache.init(tables.contains("discordstates"));
                }
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }


        }

        public static Cache getInstance() {
            if (INSTANCE == null) {
                throw new UnsupportedOperationException("Database Connector not yet initialized.");
            } else {
                try {
                    if (INSTANCE.getConnection().isClosed()) {
                        throw new UnsupportedOperationException("Database connection is closed.");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return INSTANCE;
        }

        public static void init() {
            INSTANCE = new Cache();
        }

        public Connection getConnection() throws SQLException {
            return dataSource.getConnection();
        }
    }

}
