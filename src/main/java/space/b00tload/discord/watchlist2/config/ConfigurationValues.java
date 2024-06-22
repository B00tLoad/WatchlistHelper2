package space.b00tload.discord.watchlist2.config;

import space.b00tload.utils.configuration.ConfigValues;

public enum ConfigurationValues implements ConfigValues {

    DISCORD_TOKEN("discord-token", "db", "DISCORD_BOT_TOKEN", "discord.token.bot", null),
    DISCORD_APP_ID("discord-app-id", "da", "DISCORD_APPLICATION_ID", "discord.token.applicationid", null),
    DISCORD_PUB_KEY("discord-public-key", "dp", "DISCORD_PUBLIC_KEY", "discord.token.publickey", null),
    W2G_TOKEN("w2g-token", "w", "W2G_API_TOKEN", "w2g.token", null),
    DATABASE_SCHEMA("database-schema", "Ds", "DATABASE_SCHEMA", "db.schema", "watchlist"),
    DATABASE_URL("database-url", "DU", "DATABASE_URL", "db.url", "db"),
    DATABASE_PORT("database-port", "DP", "DATABASE_PORT", "db.port", "3306"),
    DATABASE_USER("database-user", "Du", "DATABASE_USER", "db.user", "watchlist"),
    DATABASE_PASSWORD("database-password", "Dp", "DATABASE_PASSWORD", "db.password", "watchlist"),
    ;

    private final String flag, flagAlias, env, toml, defaultValue;

    ConfigurationValues(String flag, String flagAlias, String env, String toml, String defaultValue) {
        this.flag = flag;
        this.flagAlias = flagAlias;
        this.env = env;
        this.toml = toml;
        this.defaultValue = defaultValue;
    }

    /**
     * The command line flag for this config value.
     *
     * @return the command line flag
     * @example --discord-token
     */
    public String getFlag() {
        return "--" + flag;
    }

    /**
     * The command line alias for this config value.
     *
     * @return the command line alias
     * @example -d
     */
    public String getFlagAlias() {
        return "-" + flagAlias;
    }

    /**
     * The environment variable name for this config value.
     *
     * @return the environment variable name
     * @example DISCORD_APP_TOKEN
     */
    public String getEnvironmentVariable() {
        return env;
    }

    /**
     * The path to the config value in a toml file.
     *
     * @return the toml path.
     * @example discord.token
     */
    public String getTomlPath() {
        return toml;
    }

    /**
     * The default value used if not set via other configuration means.
     *
     * @return the default value.
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return "ConfigValues{" +
                ", defaultValue='" + defaultValue + '\'' +
                "tomlPath='" + toml + '\'' +
                ", environmentVarName='" + env + '\'' +
                ", flagAlias='" + flagAlias + '\'' +
                ", flag='" + flag + '\'' +
                '}';
    }
}
