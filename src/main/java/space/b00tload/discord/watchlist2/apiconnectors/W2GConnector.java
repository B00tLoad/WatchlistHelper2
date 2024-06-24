package space.b00tload.discord.watchlist2.apiconnectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import space.b00tload.discord.watchlist2.WatchlistHelperBot;
import space.b00tload.discord.watchlist2.config.ConfigurationValues;
import space.b00tload.discord.watchlist2.exceptions.W2GException;
import space.b00tload.discord.watchlist2.workers.WebRequestThreadPool;
import space.b00tload.utils.configuration.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Interfaces with the W2G-API
 *
 * @author B00tLoad_
 * @version 2.0.0
 * @since 2.0.0
 */
public class W2GConnector {

    private static W2GConnector INSTANCE = null;
    private final String API_KEY;

    private W2GConnector(String apiKey) {
        this.API_KEY = apiKey;
    }

    /**
     * Returns and (if necessary creates) the singleton instance.
     *
     * @return the singleton instance
     */
    public synchronized static W2GConnector getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new W2GConnector(Configuration.getInstance().get(ConfigurationValues.W2G_TOKEN));
        }
        return INSTANCE;
    }

    /**
     * Creates an empty W2G-Room.
     *
     * @return the roomID (streamkey)
     * @throws InterruptedException if the worker thread gets interrupted during execution.
     * @throws ExecutionException   if an error occurs during execution.
     * @throws TimeoutException     if the worker thread has not responded after 5 seconds.
     */
    public String createRoom() throws InterruptedException, ExecutionException, TimeoutException {
        return createRoom(null);
    }

    /**
     * Creates a W2G-Room with a pre-opened video.
     *
     * @param item_url The link to the video
     * @return the roomID (streamkey)
     * @throws InterruptedException if the worker thread gets interrupted during execution.
     * @throws ExecutionException   if an error occurs during execution.
     * @throws TimeoutException     if the worker thread has not responded after 5 seconds.
     */
    public String createRoom(String item_url) throws InterruptedException, ExecutionException, TimeoutException {
        Future<String> f = WebRequestThreadPool.getInstance().submit(() -> {
            OkHttpClient http = new OkHttpClient(new OkHttpClient.Builder().protocols(List.of(Protocol.HTTP_2, Protocol.HTTP_1_1)));
            JsonObject bodyJson = new JsonObject();
            bodyJson.addProperty("w2g_api_key", API_KEY);
            if (Objects.nonNull(item_url)) bodyJson.addProperty("share", item_url);
            bodyJson.addProperty("bg_color", "#764494");
            bodyJson.addProperty("bg_opacity", 40);
            try (Response response = http.newCall(
                            new Request.Builder()
                                    .url("https://api.w2g.tv/rooms/create.json")
                                    .method("POST", RequestBody.create(bodyJson.toString().getBytes(StandardCharsets.UTF_8)))
                                    .addHeader("Content-Type", "application/json")
                                    .addHeader("Accept", "application/json")
                                    .addHeader("User-Agent", WatchlistHelperBot.USER_AGENT).build())
                    .execute()) {
                if (Objects.isNull(response.body()) || response.body().contentLength() == 0)
                    throw new W2GException("Failed to create room. Empty response. HTTP-Code: " + response.code());
                if (response.code() != 200) {
                    throw new W2GException(response.body().source().readUtf8());
                }
                JsonObject responseJson = JsonParser.parseString(response.body().source().readUtf8()).getAsJsonObject();
                return responseJson.get("streamkey").getAsString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return f.get(5, TimeUnit.SECONDS);
    }

    /**
     * Immediately plays a video in a room.
     *
     * @param streamkey The ID of the room
     * @param item_url  The link of the video to be played
     */
    public void playVideo(String streamkey, String item_url) {
        WebRequestThreadPool.getInstance().submit(() -> {

            OkHttpClient http = new OkHttpClient(new OkHttpClient.Builder().protocols(List.of(Protocol.HTTP_2, Protocol.HTTP_1_1)));
            JsonObject bodyJson = new JsonObject();
            bodyJson.addProperty("w2g_api_key", API_KEY);
            bodyJson.addProperty("item_url", item_url);
            try (Response response = http.newCall(
                            new Request.Builder()
                                    .url("https://api.w2g.tv/rooms/" + streamkey + "/sync_update")
                                    .method("POST", RequestBody.create(bodyJson.toString().getBytes(StandardCharsets.UTF_8)))
                                    .addHeader("Content-Type", "application/json")
                                    .addHeader("Accept", "application/json")
                                    .addHeader("User-Agent", WatchlistHelperBot.USER_AGENT).build())
                    .execute()) {
                if (response.code() != 200) {
                    if (response.body() == null)
                        throw new W2GException("Failed to queue video. Empty response. HTTP-Code: " + response.code());
                    throw new W2GException(response.body().source().readUtf8());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Queues a video to be played in a room.
     *
     * @param streamkey The ID of the room.
     * @param item_url  The link of the video to be queued.
     * @param title     The title of the video to be queued.
     */
    public void queueVideo(String streamkey, String item_url, String title) {
        WebRequestThreadPool.getInstance().submit(() -> {

            OkHttpClient http = new OkHttpClient(new OkHttpClient.Builder().protocols(List.of(Protocol.HTTP_2, Protocol.HTTP_1_1)));
            JsonObject bodyJson = new JsonObject();
            bodyJson.addProperty("w2g_api_key", API_KEY);
            JsonArray add_items = new JsonArray();
            JsonObject add_item = new JsonObject();
            add_item.addProperty("url", item_url);
            add_item.addProperty("title", title);
            add_items.add(add_item);
            bodyJson.add("add_items", add_items);
            try (Response response = http.newCall(
                            new Request.Builder()
                                    .url("https://api.w2g.tv/rooms/" + streamkey + "/playlists/current/playlist_items/sync_update")
                                    .method("POST", RequestBody.create(bodyJson.toString().getBytes(StandardCharsets.UTF_8)))
                                    .addHeader("Content-Type", "application/json")
                                    .addHeader("Accept", "application/json")
                                    .addHeader("User-Agent", WatchlistHelperBot.USER_AGENT).build())
                    .execute()) {
                if (response.code() != 200) {
                    if (response.body() == null)
                        throw new W2GException("Failed to queue video. Empty response. HTTP-Code: " + response.code());
                    throw new W2GException(response.body().source().readUtf8());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
