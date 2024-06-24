package space.b00tload.discord.watchlist2.exceptions;

public class DiscordOauthException extends RuntimeException {

    public DiscordOauthException(String message){
        super(message);
    }

    public DiscordOauthException(String message, Throwable cause){
        super(message, cause);
    }

    public DiscordOauthException(Throwable cause){
        super(cause);
    }

    public DiscordOauthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
