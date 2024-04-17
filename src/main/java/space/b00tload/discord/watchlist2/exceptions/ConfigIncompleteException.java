package space.b00tload.discord.watchlist2.exceptions;


import space.b00tload.discord.watchlist2.config.ConfigValues;

import java.util.List;
import java.util.stream.Collectors;

public class ConfigIncompleteException extends RuntimeException {

    private final List<ConfigValues> missingValues;

    public ConfigIncompleteException(String message, List<ConfigValues> missingValues){
        super(message + missingValues.stream().map(Object::toString).collect(Collectors.joining(", ")));
        this.missingValues = missingValues;
    }

    public ConfigIncompleteException(String message, Throwable cause, List<ConfigValues> missingValues){
        super(message + missingValues.stream().map(Object::toString).collect(Collectors.joining(", ")), cause);
        this.missingValues = missingValues;
    }

    public ConfigIncompleteException(Throwable cause, List<ConfigValues> missingValues){
        super(cause);
        this.missingValues = missingValues;
    }

    public ConfigIncompleteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<ConfigValues> missingValues){
        super(message + missingValues.stream().map(Object::toString).collect(Collectors.joining(", ")), cause, enableSuppression, writableStackTrace);
        this.missingValues = missingValues;
    }

    public List<ConfigValues> getMissingValues() {
        return missingValues;
    }

}
