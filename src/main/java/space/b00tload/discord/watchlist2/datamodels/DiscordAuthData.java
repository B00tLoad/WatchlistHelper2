package space.b00tload.discord.watchlist2.datamodels;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record DiscordAuthData(String accessToken, LocalDateTime expiresAt, String refreshToken, List<String> scope) implements Serializable {

    public boolean needsRefresh() {
        return LocalDateTime.now().isAfter(expiresAt.minusHours(1L));
    }

}
