package space.b00tload.discord.watchlist2.apiconnectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.Context;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.b00tload.discord.watchlist2.config.ConfigurationValues;
import space.b00tload.discord.watchlist2.database.DiscordStateCache;
import space.b00tload.discord.watchlist2.datamodels.DiscordAuthData;
import space.b00tload.discord.watchlist2.exceptions.DiscordOauthException;
import space.b00tload.discord.watchlist2.workers.WebRequestThreadPool;
import space.b00tload.utils.configuration.Configuration;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DiscordOauthConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordOauthConnector.class);

    public static String getAuthUrl(Context context, String targetUrl, boolean discordMail) {
        String url = "https://discord.com/oauth2/authorize?client_id=${clientid}&state=${state}&response_type=code&redirect_uri=${callback}&scope=identify";
        String state = DiscordStateCache.createState(context.req().getSession().getId(), targetUrl);
        if (discordMail) {
            url += "+email";
        }
        url = url.replace("${callback}", URLEncoder.encode(Configuration.getInstance().get(ConfigurationValues.DISCORD_CALLBACK_URL), Charset.defaultCharset()));
        url = url.replace("${state}", URLEncoder.encode(state, Charset.defaultCharset()));
        url = url.replace("${clientid}", URLEncoder.encode(Configuration.getInstance().get(ConfigurationValues.DISCORD_APP_ID), Charset.defaultCharset()));

        return url;
    }

    public static DiscordAuthData codeExchange(Context context, String code) {
        try {
            String callbackUrl = Configuration.getInstance().get(ConfigurationValues.DISCORD_CALLBACK_URL);
            String discordAppId = Configuration.getInstance().get(ConfigurationValues.DISCORD_APP_ID);
            String discordAppSecret = Configuration.getInstance().get(ConfigurationValues.DISCORD_APP_SECRET);

            String tokenUrl = "https://discord.com/api/oauth2/token";
            String redirectUri = URLEncoder.encode(callbackUrl, Charset.defaultCharset());
            String grantType = "authorization_code";
            String clientId = URLEncoder.encode(discordAppId, Charset.defaultCharset());
            String clientSecret = URLEncoder.encode(discordAppSecret, Charset.defaultCharset());

            String requestBody = "grant_type=" + grantType + "&code=" + code + "&redirect_uri=" + redirectUri;
            String credential = Credentials.basic(clientId, clientSecret, Charset.defaultCharset());

            Future<DiscordAuthData> f = WebRequestThreadPool.getInstance().submit(() -> {
                OkHttpClient http = new OkHttpClient(new OkHttpClient.Builder().protocols(List.of(Protocol.HTTP_2, Protocol.HTTP_1_1)));
                try (Response response = http.newCall(
                        new Request.Builder()
                                .url(tokenUrl)
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .addHeader("Authorization", credential)
                                .method("POST", RequestBody.create(requestBody.getBytes(StandardCharsets.UTF_8)))
                                .build()
                ).execute()) {
                    if (Objects.isNull(response.body()) || response.body().contentLength() == 0)
                        throw new DiscordOauthException("Failed to exchange code for token. Empty response. HTTP-Code: " + response.code());
                    if (response.code() != 200) {
                        throw new DiscordOauthException(response.code() + ": " + response.body().source().readUtf8());
                    }
                    JsonObject responseJson = JsonParser.parseString(response.body().source().readUtf8()).getAsJsonObject();
                    DiscordAuthData ret = new DiscordAuthData(
                            responseJson.get("access_token").getAsString(),
                            LocalDateTime.now().plusSeconds(responseJson.get("expires_in").getAsLong()),
                            responseJson.get("refresh_token").getAsString(),
                            List.of(responseJson.get("scope").getAsString().split(" ")));
                    return ret;
                }
            });
            return f.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    public static DiscordAuthData refresh(DiscordAuthData authData) {
        try {
            String discordAppId = Configuration.getInstance().get(ConfigurationValues.DISCORD_APP_ID);
            String discordAppSecret = Configuration.getInstance().get(ConfigurationValues.DISCORD_APP_SECRET);

            String tokenUrl = "https://discord.com/api/oauth2/token";
            String grantType = "refresh_token";
            String clientId = URLEncoder.encode(discordAppId, Charset.defaultCharset());
            String clientSecret = URLEncoder.encode(discordAppSecret, Charset.defaultCharset());

            String refreshToken = authData.refreshToken();

            String requestBody = "grant_type=" + grantType + "&refresh_token=" + refreshToken;
            String credential = Credentials.basic(clientId, clientSecret, Charset.defaultCharset());

            Future<DiscordAuthData> f = WebRequestThreadPool.getInstance().submit(() -> {
                OkHttpClient http = new OkHttpClient(new OkHttpClient.Builder().protocols(List.of(Protocol.HTTP_2, Protocol.HTTP_1_1)));
                try (Response response = http.newCall(
                        new Request.Builder()
                                .url(tokenUrl)
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .addHeader("Authorization", credential)
                                .method("POST", RequestBody.create(requestBody.getBytes(StandardCharsets.UTF_8)))
                                .build()
                ).execute()) {
                    if (Objects.isNull(response.body()) || response.body().contentLength() == 0)
                        throw new DiscordOauthException("Failed to refresh token. Empty response. HTTP-Code: " + response.code());
                    if (response.code() != 200) {
                        throw new DiscordOauthException(response.code() + ": " + response.body().source().readUtf8());
                    }
                    JsonObject responseJson = JsonParser.parseString(response.body().source().readUtf8()).getAsJsonObject();
                    DiscordAuthData ret = new DiscordAuthData(
                            responseJson.get("access_token").getAsString(),
                            LocalDateTime.now().plusSeconds(responseJson.get("expires_in").getAsLong()),
                            responseJson.get("refresh_token").getAsString(),
                            List.of(responseJson.get("scope").getAsString().split(" ")));
                    return ret;
                }
            });
            return f.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    public static JsonObject identifyUser(DiscordAuthData authData) {
        try {
            String apiEndpoint = "https://discord.com/api/users/@me";

            String authorization = "Bearer " + authData.accessToken();

            Future<JsonObject> f = WebRequestThreadPool.getInstance().submit(() -> {
                JsonObject ret = new JsonObject();
                ret.addProperty("error", "unknown");

                OkHttpClient http = new OkHttpClient(new OkHttpClient.Builder().protocols(List.of(Protocol.HTTP_2, Protocol.HTTP_1_1)));
                try (Response response = http.newCall(
                        new Request.Builder()
                                .url(apiEndpoint)
                                .addHeader("Authorization", authorization)
                                .method("GET", RequestBody.create("".getBytes(StandardCharsets.UTF_8)))
                                .build()
                ).execute()) {
                    if (Objects.isNull(response.body()) || response.body().contentLength() == 0)
                        throw new DiscordOauthException("Failed to identify user. Empty response. HTTP-Code: " + response.code());
                    if (response.code() != 200) {
                        throw new DiscordOauthException(response.code() + ": " + response.body().source().readUtf8());
                    }
                    ret = JsonParser.parseString(response.body().source().readUtf8()).getAsJsonObject();

                }
                return ret;
            });
            return f.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
