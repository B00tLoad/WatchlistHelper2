package space.b00tload.discord.watchlist2.exceptions;

public class W2GException extends RuntimeException {

    public W2GException(String message){
        super(message);
    }

    public W2GException(String message, Throwable cause){
        super(message, cause);
    }

    public W2GException(Throwable cause){
        super(cause);
    }

    public W2GException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
