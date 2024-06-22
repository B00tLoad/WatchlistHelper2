package space.b00tload.discord.watchlist2.endpointserver;

import jakarta.servlet.SessionTrackingMode;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.server.session.*;
import space.b00tload.discord.watchlist2.database.DatabaseConnector;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

/**
 * This class provides helper methods for handling sessions in LiToChat web application.
 */
public class SessionHelper {

    /**
     * Creates a new session handler object to manage sessions in the LiToChat application.
     *
     * @return a new session handler object
     */
    public static SessionHandler sqlSessionHandler() {
        SessionHandler sessionHandler = new SessionHandler();
        SessionCache sessionCache = new DefaultSessionCache(sessionHandler);
        sessionCache.setSessionDataStore(
                jdbcDataStoreFactory().getSessionDataStore(sessionHandler)
        );
        sessionHandler.setSessionCache(sessionCache);
        sessionHandler.setHttpOnly(true);
        sessionHandler.setSecureRequestOnly(true);
        sessionHandler.setSameSite(HttpCookie.SameSite.STRICT);
        sessionHandler.setSessionCookie("WatchlistSession");
        sessionHandler.setMaxInactiveInterval(2 * 24 * 60 * 60); // Session expires after 2 days of inactivity
        sessionHandler.setSessionTrackingModes(Set.of(SessionTrackingMode.COOKIE)); // Use cookies to track sessions
        return sessionHandler;
    }

    /**
     * Creates a new JDBC session data store factory object to store session data in a H2 database.
     *
     * @return a new JDBC session data store factory object
     * @throws RuntimeException if an SQL exception occurs while creating the data store factory object
     */
    private static JDBCSessionDataStoreFactory jdbcDataStoreFactory() {
        try {
            String url = DatabaseConnector.getInstance().getConnection().getMetaData().getURL();
            DatabaseAdaptor databaseAdaptor = new DatabaseAdaptor();
            databaseAdaptor.setDriverInfo(DriverManager.getDriver(url), url);
            JDBCSessionDataStoreFactory jdbcSessionDataStoreFactory = new JDBCSessionDataStoreFactory();
            jdbcSessionDataStoreFactory.setDatabaseAdaptor(databaseAdaptor);
            return jdbcSessionDataStoreFactory;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}