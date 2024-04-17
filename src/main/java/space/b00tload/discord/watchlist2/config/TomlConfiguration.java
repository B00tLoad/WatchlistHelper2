package space.b00tload.discord.watchlist2.config;

import com.electronwill.nightconfig.core.file.FileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.b00tload.discord.watchlist2.WatchlistHelperBot;
import space.b00tload.discord.watchlist2.exceptions.ConfigException;

import java.nio.file.Path;
import java.nio.file.Paths;

import static space.b00tload.discord.watchlist2.WatchlistHelperBot.APPLICATION_BASE;

public class TomlConfiguration implements AutoCloseable {


    private static FileConfig config;
    private static Logger LOGGER;


    TomlConfiguration() {
        LOGGER = LoggerFactory.getLogger(TomlConfiguration.class);
        Path configBase = Paths.get(APPLICATION_BASE, "config");
        if (!configBase.toFile().exists()) //noinspection ResultOfMethodCallIgnored
            configBase.toFile().mkdirs();
        config = FileConfig.builder(Paths.get(String.valueOf(configBase), "config.toml")).defaultResource("/config/app/empty.toml").autosave().autoreload().build();
        config.load();
        checkVersion();
    }

    public void close() {
        config.save();
        config.close();
    }

    public void checkVersion() {
        String currentVersion = WatchlistHelperBot.SOFTWARE_VERSION;
        String configVersion = getString("file.version");
        if (currentVersion == null) {
            LOGGER.error("Error: Failed to retrieve current version. Assuming 0.0.1");
            currentVersion = "0.0.1";
        }
        if (configVersion == null) {
            throw new ConfigException("Invalid config. \"file.version\" is not set.");
        } else if (configVersion.equals("0.0.0")) {
            LOGGER.info("New installation detected. Creating config file.");
            setString("file.version", currentVersion);
        } else if (currentVersion.compareTo(configVersion) < 0) {
            LOGGER.warn("Software updated. Please check wiki for migration help.");
            setString("file.version", currentVersion);
        }
    }

    public boolean contains(String key) {
        return config.contains(key);
    }

    public String getString(String key) {
        return config.get(key);
    }

    public void setString(String key, String value) {
        config.set(key, value);
    }


}
