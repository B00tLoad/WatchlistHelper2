package space.b00tload.discord.watchlist2;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.b00tload.discord.watchlist2.config.ConfigurationValues;
import space.b00tload.discord.watchlist2.database.DatabaseConnector;
import space.b00tload.utils.configuration.Configuration;
import space.b00tload.utils.configuration.exceptions.ConfigIncompleteException;

import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class WatchlistHelperBot {

    public static final String LINE_SEPERATOR = System.lineSeparator();
    private static final Logger log = LoggerFactory.getLogger(WatchlistHelperBot.class);
    public static final String USER_HOME = System.getProperty("user.home");
    public static String SOFTWARE_VERSION;
    public static String USER_AGENT;
    public static String APPLICATION_BASE;
    public static JDA jda;


    static {
        SOFTWARE_VERSION = WatchlistHelperBot.class.getPackage().getImplementationVersion();
        if (Objects.isNull(SOFTWARE_VERSION)) {
            SOFTWARE_VERSION = "2.0.0-alpha1-indev";
        }
        USER_AGENT = "WatchlistHelper " + SOFTWARE_VERSION + "(" + System.getProperty("os.name") + "; " + System.getProperty("os.arch") + ") Java/" + System.getProperty("java.version");
        APPLICATION_BASE = Paths.get(USER_HOME, ".bdu", "watchlist").toString();
    }


    public static void main(String[] args) throws InterruptedException {
        //Set app base directory depending on whether app is run in docker or not.
        APPLICATION_BASE = List.of(args).contains("--docker") ? Paths.get("data", ".bdu", "watchlist").toString() : Paths.get(USER_HOME, ".bdu", "watchlist").toString();

        //Set up logger
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            loggerContext.reset();
            if (List.of(args).contains("--docker")) {
                configurator.doConfigure(Objects.requireNonNull(WatchlistHelperBot.class.getResource("/config/logback/logback-docker.xml")));
            } else if (List.of(args).contains("--dev")) {
                configurator.doConfigure(Objects.requireNonNull(WatchlistHelperBot.class.getResource("/config/logback/logback-dev.xml")));
            } else {
                configurator.doConfigure(Objects.requireNonNull(WatchlistHelperBot.class.getResource("/config/logback/logback-bare.xml")));
            }
        } catch (JoranException ignored) {
        }
        (new StatusPrinter2()).printInCaseOfErrorsOrWarnings(loggerContext);

        try {
            Configuration.init(args, SOFTWARE_VERSION, APPLICATION_BASE, ConfigurationValues.values());
        } catch (ConfigIncompleteException e) {
            log.error("Config incomplete. Missing: {}", e.getMessage());
            System.exit(2);
        }

        try {
            DatabaseConnector.Persistent.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        DatabaseConnector.Cache.init();

        jda = JDABuilder
                .create(Configuration.getInstance().get(ConfigurationValues.DISCORD_TOKEN), GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.watching("your watchlist."))
//                .addEventListeners(new SetupCommand(), new GuildJoinListener(), new RecommendButtonInteractionListener())
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS, CacheFlag.SCHEDULED_EVENTS)
                .build().awaitReady();

        if (List.of(args).contains("--dev"))
            jda.getPresence().setPresence(OnlineStatus.IDLE, Activity.competing("a release candidate battle royale."));
    }

}
