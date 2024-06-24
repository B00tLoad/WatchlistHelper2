package space.b00tload.discord.watchlist2.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordStateCache {

    private final static Logger LOGGER = LoggerFactory.getLogger(DiscordStateCache.class);


    public static String createState(String session, String targetUrl){
        String state = generateStateString();
        try (Connection con = DatabaseConnector.Cache.getInstance().getConnection(); PreparedStatement pstmt_insertState = con.prepareStatement("INSERT INTO discordstates (websession, state, target_url) VALUES ( ?, ?, ? )")){
            pstmt_insertState.setString(1, session);
            pstmt_insertState.setString(2, state);
            pstmt_insertState.setString(3, targetUrl);
            if(pstmt_insertState.executeUpdate() != 1){
                throw new UnsupportedOperationException("error while inserting state.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return state;
    }

    private static String generateStateString(){
        String charSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++){
            sb.append(charSet.charAt(random.nextInt(charSet.length())));
        }
        return sb.toString();
    }

    public static void init(boolean foundTable){

        if(!foundTable){
            try (Connection con = DatabaseConnector.Cache.dataSource.getConnection(); Statement stmt_discordStateTable = con.createStatement()) {
                LOGGER.debug("Creating discord api state table");
                stmt_discordStateTable.execute("CREATE TABLE IF NOT EXISTS `discordstates` (" +
                        "    `websession` varchar(120)," +
                        "    `state` varchar(255)," +
                        "    `target_url` varchar(255)," +
                        "    `requested_at` timestamp DEFAULT (now())" +
                        ");");
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        Timer timer = new Timer("DiscordStateCache housekeeper");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try(Connection con = DatabaseConnector.Cache.getInstance().getConnection(); PreparedStatement pstmt_purgeStatements = con.prepareStatement("DELETE FROM discordstates WHERE REQUESTED_AT < ?")){
                    pstmt_purgeStatements.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().minusMinutes(15)));
                    int updateCount = pstmt_purgeStatements.executeUpdate();
                    LOGGER.info("Purged {} entries from discord state cache.", updateCount);
                } catch (SQLException e){
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }, 120000L, 60000L);
    }

}
