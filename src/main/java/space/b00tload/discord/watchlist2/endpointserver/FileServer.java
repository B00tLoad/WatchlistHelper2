package space.b00tload.discord.watchlist2.endpointserver;

import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.eclipse.jetty.server.Server;

import java.io.IOException;
import java.io.InputStream;

import static space.b00tload.discord.watchlist2.WatchlistHelperBot.SOFTWARE_VERSION;

/**
 * A class for serving static files and LiToChat app.
 */
public class FileServer {

    /**
     * Serves the LiToChat app's HTML file requested.
     *
     * @param ctx  the current context
     * @param path the path to the HTML file
     * @throws IOException if an I/O error occurs
     */
    public static void serveApp(Context ctx, String path) throws IOException {
        InputStream in = Server.class.getResourceAsStream("/app/" + path + "/index.html");
        if (in == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            return;
        }
        String result = new String(in.readAllBytes());
        in.close();
        result = result.replace("%%version%", SOFTWARE_VERSION);
        ctx.result(result).status(HttpStatus.OK).contentType(ContentType.TEXT_HTML);
    }

    /**
     * Serves the static files (CSS and JS) for the app.
     *
     * @param ctx the current context
     * @throws IOException if an I/O error occurs
     */
    public static void serveStatic(Context ctx) throws IOException {
        InputStream in = Server.class.getResourceAsStream(ctx.path());
        if (in == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            return;
        }
        String result = new String(in.readAllBytes());
        in.close();
        result = result.replace("%%version%", SOFTWARE_VERSION);
        if (ctx.path().endsWith(".css")) ctx.result(result).status(HttpStatus.OK).contentType(ContentType.TEXT_CSS);
        if (ctx.path().endsWith(".js"))
            ctx.result(result).status(HttpStatus.OK).contentType(ContentType.TEXT_JS);
    }
}