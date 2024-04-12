package space.b00tload.discord.watchlist2.workers;

import org.slf4j.simple.SimpleLoggerFactory;

import java.util.Objects;
import java.util.concurrent.*;

public class WebRequestThreadPool extends ThreadPoolExecutor {


    private static WebRequestThreadPool INSTANCE;

    private WebRequestThreadPool() {
        super(20, 40, 150, TimeUnit.SECONDS, new ArrayBlockingQueue<>(300), new WebRequestThreadFactory());
    }

    @Override
    public void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        // If submit() method is called instead of execute()
        if (t == null && r instanceof Future<?>) {
            try {
                Object result = ((Future<?>) r).get();
            } catch (CancellationException e) {
                t = e;
            } catch (ExecutionException e) {
                t = e.getCause();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (t != null) {
            // Exception occurred
            new SimpleLoggerFactory().getLogger("space.b00tload.discord.watchlist2.webrequests").error("Uncaught exception detected:\n\t\t\t{}", t.getMessage(), t);
            // ... Handle the exception

            if(t instanceof Error){
                System.exit(500);
            }

            // Restart the runnable again
//            execute(r);
        }
        // ... Perform cleanup actions
    }

    private static void init(){
        INSTANCE = new WebRequestThreadPool();
    }

    public static WebRequestThreadPool getInstance() {
        if(Objects.isNull(INSTANCE)) init();
        return INSTANCE;
    }
}
