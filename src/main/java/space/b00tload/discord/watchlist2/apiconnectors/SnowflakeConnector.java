package space.b00tload.discord.watchlist2.apiconnectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.b00tload.discord.watchlist2.config.ConfigurationValues;
import space.b00tload.discord.watchlist2.exceptions.SnowflakeGeneratorRequestException;
import space.b00tload.discord.watchlist2.workers.WebRequestThreadPool;
import space.b00tload.utils.configuration.Configuration;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SnowflakeConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeConnector.class);

    public static long generateSnowflakeId() {
        try {
            String url = Configuration.getInstance().get(ConfigurationValues.SNOWFLAKE_URL) + "/generate";


        Future<Long> f = WebRequestThreadPool.getInstance().submit(() -> {
            OkHttpClient httpClient = new OkHttpClient();

            try(Response response = httpClient.newCall(new Request.Builder()
                    .url(url)
                    .build()
            ).execute()){
                if (Objects.isNull(response.body()) || response.body().contentLength() == 0)
                    throw new SnowflakeGeneratorRequestException("Failed to get snowflake. Empty response. HTTP-Code: " + response.code());
                if (response.code() != 200) {
                    throw new SnowflakeGeneratorRequestException(response.code() + ": " + response.body().source().readUtf8());
                }
                JsonObject jo = JsonParser.parseString(response.body().source().readUtf8()).getAsJsonObject();
                return jo.get("id").getAsLong();
            }
        });
            return f.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

}
