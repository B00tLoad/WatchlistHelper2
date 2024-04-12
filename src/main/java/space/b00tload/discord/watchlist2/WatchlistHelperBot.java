package space.b00tload.discord.watchlist2;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import space.b00tload.discord.watchlist2.config.TomlConfiguration;

import java.nio.file.Paths;
import java.util.*;

public class WatchlistHelperBot {

    public static final String LINE_SEPERATOR = System.lineSeparator();
    public static String USER_HOME = System.getProperty("user.home");
    public static String SOFTWARE_VERSION;
    public static String USER_AGENT;
    public static String CONFIG_BASE;

    static {
        SOFTWARE_VERSION = WatchlistHelperBot.class.getPackage().getImplementationVersion();
        if(Objects.isNull(SOFTWARE_VERSION)){
            SOFTWARE_VERSION = "2.0.0-alpha1-indev";
        }
        USER_AGENT = "WatchlistHelper " + SOFTWARE_VERSION + "(" + System.getProperty("os.name") + "; " + System.getProperty("os.arch") + ") Java/" + System.getProperty("java.version");
    }


    public static void main(String[] args) throws InterruptedException {
        CONFIG_BASE = List.of(args).contains("--docker") ?  Paths.get("data", ".bdu", "watchlist").toString() :  Paths.get(USER_HOME, ".bdu", "watchlist").toString();;


        TomlConfiguration.validate(List.of("discord.token"));

        JDA jda = JDABuilder
                .create(TomlConfiguration.getString("discord.token"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.watching("your watchlist."))
//                .addEventListeners(new SetupCommand(), new GuildJoinListener(), new RecommendButtonInteractionListener())
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS, CacheFlag.SCHEDULED_EVENTS)
                .build().awaitReady();

        if(List.of(args).contains("--dev")) jda.getPresence().setPresence(OnlineStatus.IDLE, Activity.competing("a release candidate battle royale."));

        jda.getGuilds().forEach(guild -> {
            TomlConfiguration.setString("guilds."+guild.getId(), guild.getName());
//            guild.upsertCommand(SETUP_COMMAND).queue();
        });
    }

}
