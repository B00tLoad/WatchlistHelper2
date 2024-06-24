package space.b00tload.discord.watchlist2.exceptions;

public class SnowflakeGeneratorRequestException extends RuntimeException {

    public SnowflakeGeneratorRequestException(String message){
        super(message);
    }

    public SnowflakeGeneratorRequestException(String message, Throwable cause){
        super(message, cause);
    }

    public SnowflakeGeneratorRequestException(Throwable cause){
        super(cause);
    }

    public SnowflakeGeneratorRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
