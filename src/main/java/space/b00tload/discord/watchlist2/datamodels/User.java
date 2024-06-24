package space.b00tload.discord.watchlist2.datamodels;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.UnauthorizedResponse;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.b00tload.discord.watchlist2.WatchlistHelperBot;
import space.b00tload.discord.watchlist2.apiconnectors.DiscordOauthConnector;
import space.b00tload.discord.watchlist2.apiconnectors.SnowflakeConnector;
import space.b00tload.discord.watchlist2.config.ConfigurationValues;
import space.b00tload.discord.watchlist2.database.DatabaseConnector;
import space.b00tload.utils.configuration.Configuration;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class User {
    private static final Logger LOGGER = LoggerFactory.getLogger(User.class);
    private final long snowflake;
    private final long discordId;
    private final Path avatar;
    private final LocalDateTime registeredSince;
    private DiscordAuthData discordAuthData;
    private final LocalDateTime lastSeen;
    private final boolean isMember;
    private final boolean isAdmin;

    public User(DiscordAuthData authData) {
        discordId = getDiscordIdFromApi();
        if(isUserRegistered(discordId)) {
            JsonObject data = fetchUserDataFromDb(discordId);
            snowflake = data.get("snowflake").getAsLong();
            avatar = Path.of(data.get("avatar").getAsString());
            registeredSince = LocalDateTime.ofEpochSecond(data.get("registeredSince").getAsLong()/1000,
                    (int) (data.get("registeredSince").getAsLong()%1000), ZoneOffset.of(ZoneId.systemDefault().getId()));
            lastSeen = LocalDateTime.ofEpochSecond(data.get("lastSeen").getAsLong()/1000,
                    (int) (data.get("lastSeen").getAsLong()%1000), ZoneOffset.of(ZoneId.systemDefault().getId()));
            discordAuthData = new Gson().fromJson(data.get("discordAuthData"), DiscordAuthData.class);
            isMember = data.get("isMember").getAsBoolean();
            isAdmin = data.get("isAdmin").getAsBoolean();
        } else {
            Guild homeGuild = WatchlistHelperBot.jda.getGuildById(Configuration.getInstance().get(ConfigurationValues.DISCORD_HOME_GUILD));
            Role accessRole = homeGuild.getRoleById(Configuration.getInstance().get(ConfigurationValues.DISCORD_ACCESS_ROLE));
            if (homeGuild.isMember(homeGuild.getJDA().getUserById(discordId))) {
                snowflake = SnowflakeConnector.generateSnowflakeId();
                avatar = Path.of("/");
                registeredSince = LocalDateTime.now();
                lastSeen = LocalDateTime.now();
                discordAuthData = authData;
                isMember = homeGuild.getMemberById(discordId).getRoles().contains(accessRole);
                isAdmin = homeGuild.getMemberById(discordId).getRoles().contains(homeGuild.getRoleById(Configuration.getInstance().get(ConfigurationValues.DISCORD_ADMIN_ROLE)));
            } else throw new UnauthorizedResponse("You are not allowed to access this site. You must be a member of the Discord Server " + homeGuild.getName() + " and have the role " + accessRole.getName() + ".");
        }

    }


    private long getDiscordIdFromApi() {
        if (discordAuthData.needsRefresh()) discordAuthData = DiscordOauthConnector.refresh(discordAuthData);
        return DiscordOauthConnector.identifyUser(discordAuthData).get("id").getAsLong();
    }

    private boolean isUserRegistered(long discordId) {
        try (Connection con = DatabaseConnector.Persistent.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(
                "SELECT count(*) AS count FROM `" + Configuration.getInstance().get(ConfigurationValues.DATABASE_SCHEMA) + "`users WHERE discord_id=?",
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            ps.setLong(1, discordId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.first()) return false;
                return rs.getInt("count") == 1;
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    private JsonObject fetchUserDataFromDb(long discordId){
        try (Connection con = DatabaseConnector.Persistent.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM `" + Configuration.getInstance().get(ConfigurationValues.DATABASE_SCHEMA) + "`users WHERE discord_id=?",
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ps.setLong(1, discordId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.first()) return null;
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("snowflake", rs.getLong("snowflake"));
                jsonObject.addProperty("avatar", rs.getString("avatar"));
                jsonObject.addProperty("registered_since", rs.getTimestamp("registered_since").getTime());
                jsonObject.addProperty("last_seen", rs.getTimestamp("last_seen").getTime());
                jsonObject.addProperty("is_member", rs.getBoolean("is_member"));
                jsonObject.addProperty("is_admin", rs.getBoolean("is_admin"));
                jsonObject.add("discordAuth", JsonParser.parseString(new Gson().toJson(rs.getObject("discorddata"), DiscordAuthData.class)));
                return jsonObject;
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}
