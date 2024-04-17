package space.b00tload.discord.watchlist2.config;

public enum ConfigValues {

    DISCORD_TOKEN("discord-token", "d", "DISCORD_APP_TOKEN", "discord.token", null),
    W2G_TOKEN("w2g-token", "w", "W2G_API_TOKEN", "w2g.token", null);

    private final String flag, flagAlias, env, toml, defaultValue;

    ConfigValues(String flag, String flagAlias, String env, String toml, String defaultValue) {
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
        return flag;
    }

    /**
     * The command line alias for this config value.
     *
     * @return the command line alias
     * @example -d
     */
    public String getFlagAlias() {
        return flagAlias;
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
