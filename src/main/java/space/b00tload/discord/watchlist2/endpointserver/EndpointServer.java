package space.b00tload.discord.watchlist2.endpointserver;

import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.http.HttpStatus;
import io.javalin.micrometer.MicrometerPlugin;
import io.javalin.plugin.bundled.RouteOverviewPlugin;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import space.b00tload.discord.watchlist2.config.ConfigValues;
import space.b00tload.discord.watchlist2.config.Configuration;


/**
 * This class represents an endpoint server for the LiToChat application.
 * It is responsible for setting up and managing the HTTP and WebSocket endpoints used by the application.
 */
public class EndpointServer {
    private static Javalin server;

    /**
     * Initializes the endpoint server.
     */
    public static void init() {
        //Create metrics
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

//        MicrometerPlugin micrometerPlugin = MicrometerPlugin.Companion.create(micrometerConfig -> micrometerConfig.registry = registry);

        // Create the Javalin server and configure it with plugins and handlers
        server = Javalin.create(config -> {
            // Register the RouteOverviewPlugin to provide a list of available routes
            config.bundledPlugins.enableRouteOverview("/routeOverview");
            config.bundledPlugins.enableCors(corsPluginConfig -> {
                corsPluginConfig.addRule(corsRule -> {
                    corsRule.allowHost("http://localhost");
                    corsRule.defaultScheme = "https";
                });
            });
            config.bundledPlugins.enableDevLogging();
            // Set up the session handler to use SQL
            config.jetty.modifyServletContextHandler(servletContextHandler -> servletContextHandler.setSessionHandler(SessionHelper.sqlSessionHandler()));
            // Enable serving of webjars
            config.staticFiles.enableWebjars();
            // Enable GZIP compression
            config.http.brotliAndGzipCompression();
            // Set prefer405over404 to true to return 405 status codes instead of 404 for method not allowed errors
            config.http.prefer405over404 = true;
            // Set up a request logger to log requests
            config.requestLogger.http((ctx, ms) -> {
                //TODO: log
            });
        }).start(Integer.parseInt(Configuration.getInstance().get(ConfigValues.ENDPOINT_PORT)));

        // Set up exception and error handling
        server.exception(NullPointerException.class, (e, context) -> e.printStackTrace());
        server.exception(Exception.class, (e, context) -> {
            e.printStackTrace();
            context.status(500);
        });
        server.error(404, context -> context.result("404"));

        // Set up the routes for the API and the app
        routeSetup();
    }

    /**
     * Sets up the routes for the API and the app.
     */
    private static void routeSetup() {
        /*server.routes(() -> {
            // Redirect HTTP requests to HTTPS
            ApiBuilder.before(ctx -> {
                if (ctx.port() == 8080 && ctx.protocol().equals("HTTP/1.1")) {
                    ctx.redirect("https://" + ctx.url().split(":")[1].replace("//", "").replace("https://", "") + ":" + 8443 + ctx.path(), HttpStatus.MOVED_PERMANENTLY);
                }
            });
            // Set up the root route to redirect to the app
            ApiBuilder.get("/", ctx -> {
                ctx.redirect("/app");
                ctx.status(HttpStatus.PERMANENT_REDIRECT);
            });
            // Set up the API routes
            ApiBuilder.path("/api", new ApiHandler());

            // Application routes
            ApiBuilder.path("/app", () -> {
                ApiBuilder.get("/", ctx -> FileServer.serveApp(ctx, "home"), BMAccessManager.Roles.USER);
                ApiBuilder.get("/login", ctx -> FileServer.serveApp(ctx, "login"));
                ApiBuilder.get("/signup", ctx -> FileServer.serveApp(ctx, "signup"));
                ApiBuilder.get("/resetPassword", ctx -> FileServer.serveApp(ctx, "reset"));
            });

            // Serve static files
            ApiBuilder.get("/static/*", FileServer::serveStatic);

        });

        // Set up WebSocket
        server.ws("/chat", ws -> {
            ws.onConnect(new WsConnectWorker());
            ws.onClose(new WsCloseWorker());
            ws.onMessage(new WsMessageWorker());
        });*/
    }

    /**
     * Stops the server.
     */
    public static void stop() {
        server.stop();
        server = null;
    }
}