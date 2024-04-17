package space.b00tload.discord.watchlist2.config;


import space.b00tload.discord.watchlist2.exceptions.ConfigException;
import space.b00tload.discord.watchlist2.exceptions.ConfigIncompleteException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Configuration {

    private static Configuration INSTANCE;
    private final HashMap<ConfigValues, String> values = new HashMap<>();

    private Configuration(String[] args) throws ConfigIncompleteException {
        for (int i = 0; i < args.length; i++) {
            for (ConfigValues v : ConfigValues.values()) {
                if (v.getFlag().equalsIgnoreCase(args[i]) || v.getFlagAlias().equals(args[i])) {
                    if (args[i + 1].startsWith("-")) continue;
                    values.put(v, args[i + 1]);
                    i++;
                }
            }
        }
        for (ConfigValues v : ConfigValues.values()) {
            if (values.containsKey(v)) continue;
            if (System.getenv().containsKey(v.getEnvironmentVariable()))
                values.put(v, System.getenv(v.getEnvironmentVariable()));
        }
        try (TomlConfiguration tomlConfig = new TomlConfiguration()) {
            for (ConfigValues v : ConfigValues.values()) {
                if (values.containsKey(v)) continue;
                if (tomlConfig.contains(v.getTomlPath())) values.put(v, tomlConfig.getString(v.getTomlPath()));
            }
        }

        for (ConfigValues v : ConfigValues.values()) {
            if (values.containsKey(v)) continue;
            values.put(v, v.getDefaultValue());
        }

        List<ConfigValues> missingValues = new ArrayList<>();
        for (ConfigValues v : values.keySet()) {
            if (values.get(v) == null) missingValues.add(v);
        }

        throw new ConfigIncompleteException("Missing values: ", missingValues);
    }

    public static Configuration getInstance() {
        if (INSTANCE == null) throw new ConfigException("Configuration not initialized");
        return INSTANCE;
    }

    public static void init(String[] args) throws ConfigIncompleteException {
        INSTANCE = new Configuration(args);
    }

    public String get(ConfigValues v) {
        return values.get(v);
    }
}
