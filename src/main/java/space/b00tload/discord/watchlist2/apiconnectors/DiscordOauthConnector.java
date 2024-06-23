package space.b00tload.discord.watchlist2.apiconnectors;

import io.javalin.http.Context;
import space.b00tload.discord.watchlist2.config.ConfigurationValues;
import space.b00tload.discord.watchlist2.database.DiscordStateCache;
import space.b00tload.utils.configuration.Configuration;

import java.net.URLEncoder;
import java.nio.charset.Charset;

public class DiscordOauthConnector {

    public static String getAuthUrl(Context context, String targetUrl, boolean discordMail){
        String url = "https://discord.com/oauth2/authorize?client_id=${clientid}&state=${state}&response_type=code&redirect_uri=${callback}&scope=identify";
        String state = DiscordStateCache.createState(context.req().getSession().getId(), targetUrl);
        if(discordMail){
            url += "+email";
        }
        url = url.replace("${callback}", URLEncoder.encode(Configuration.getInstance().get(ConfigurationValues.CALLBACK_URL), Charset.defaultCharset()));
        url=url.replace("${state}", URLEncoder.encode(state, Charset.defaultCharset()));
        url=url.replace("${clientid}", URLEncoder.encode(Configuration.getInstance().get(ConfigurationValues.DISCORD_APP_ID), Charset.defaultCharset()));

        return url;
    }

}
