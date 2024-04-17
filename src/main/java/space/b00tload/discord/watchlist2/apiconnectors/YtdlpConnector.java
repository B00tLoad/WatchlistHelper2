package space.b00tload.discord.watchlist2.apiconnectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;
import space.b00tload.discord.watchlist2.WatchlistHelperBot;
import space.b00tload.discord.watchlist2.metadata.Metadata;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Provides a connector/wrapper for the yt-dlp cli.
 * Handles downloading metadata for <a href="https://github.com/yt-dlp/yt-dlp/blob/master/supportedsites.md">supported sites</a>.
 *
 * @author B00tLoad_
 * @version 2.0.0
 * @since 2.0.0
 */
public class YtdlpConnector {

    /**
     * The command to be run to download metadata for a video or playlist
     */
    private final String DOWNLOAD_CMD = "yt-dlp --write-info-json --skip-download --no-clean-info-json -o ./data.json $PLAYLIST_LINK";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * @param link The link to extract metadata from
     * @throws IOException          on IO error when running yt-dlp // if no metadata files are found
     * @throws InterruptedException if the ProcessExecutor was interrupted
     */
    public Metadata getMetaData(String link) throws IOException, InterruptedException, TimeoutException {
        try {
            log.info("Downloading metadata for link {}", link);
            File tmp = getTempDirectory();
            ProcessResult pr = new ProcessExecutor().commandSplit(DOWNLOAD_CMD.replace("$PLAYLIST_LINK", link))
                    .redirectOutput(Slf4jStream.ofCaller().asTrace())
                    .redirectError(Slf4jStream.ofCaller().asError())
                    .destroyOnExit()
                    .exitValueNormal()
                    .directory(tmp)
                    .execute();
            log.info("Download complete. Exit value: {}", pr.getExitValue());

            File metaFile = new File(tmp, "data.info.json");
            if (!metaFile.exists()) metaFile = new File(tmp, "data.json.info.json");
            if (!metaFile.exists()) throw new IOException("No metadata files found.");
            FileReader metaReader = new FileReader(metaFile);
            JsonElement meta = JsonParser.parseReader(metaReader);
            return Metadata.parseYtdlpMetadata(meta);
        } catch (InvalidExitValueException ex) {
            log.warn("Invalid exit value: {}", ex.getMessage());
            log.warn("Site may not be supported.");
        }
        return null;
    }

    /**
     * Creates a folder in ${WatchlisHelperBot.APPLICATION_BASE}/.tmp to store metadata files in
     *
     * @return a java.io.File (directory) for temporarily storing files.
     */
    private File getTempDirectory() {
        UUID uuid = UUID.randomUUID();

        Path ret = Path.of(WatchlistHelperBot.APPLICATION_BASE, ".tmp", uuid.toString());
        //noinspection ResultOfMethodCallIgnored
        ret.toFile().mkdirs();

        return ret.toFile();
    }

}
