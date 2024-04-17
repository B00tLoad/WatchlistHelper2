package space.b00tload.discord.watchlist2.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class VideoMetadata extends Metadata {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoMetadata.class);
    private String title;
    private String description;
    private String channel;
    private URL url;
    private String thumbnail;
    private URL thumbnailUrl;
    private int duration;
    private String durationString;
    private int season;
    private int episode;
    private String episodeString;
    private List<String> genre; // TODO: Fetch from TMDB
    private List<String> availableLanguages;
    private List<String> languages; // TODO: Fetch from TMDB
    private Date releaseDate;
    private Provider provider;
    private String id;
    private UUID internalUID;

    VideoMetadata(String json) throws MalformedURLException, URISyntaxException {
        this(JsonParser.parseString(json).getAsJsonObject());
    }

    VideoMetadata(JsonObject json) throws URISyntaxException, MalformedURLException {
        this.title = json.get("title").getAsString();
        this.description = json.get("description").getAsString();
        this.channel = json.get("channel").getAsString();
        this.url = new URI(json.get("webpage_url").getAsString()).toURL();
        this.thumbnailUrl = new URI(json.get("thumbnails").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString()).toURL();
        this.duration = json.get("duration").getAsInt();
        this.durationString = json.get("duration_string").getAsString();
        this.season = json.get("season_number").getAsInt();
        this.episode = json.get("episode_number").getAsInt();
        this.episodeString = this.season+"x"+this.episode;
        availableLanguages = new ArrayList<>();
        for(JsonElement e : json.get("formats").getAsJsonArray()){
            if(!availableLanguages.contains(e.getAsJsonObject().get("language").getAsString())) availableLanguages.add(e.getAsJsonObject().get("language").getAsString());
        }
        this.releaseDate = new Date(json.get("timestamp").getAsLong());
        this.provider = new Provider(json.get("extractor"));
        this.id = json.get("id").getAsString();
        this.internalUID = UUID.randomUUID();
    }



}
