FROM eclipse-temurin:21.0.2_13-jre-alpine

LABEL authors="B00tLoad_"

RUN mkdir -p /opt/app
RUN mkdir -p /data/.bdu/watchlist
RUN apk add --no-cache python3 py3-pip curl
RUN mkdir -p ~/.local/bin/
RUN curl -L https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -o ~/.local/bin/yt-dlp
RUN chmod a+rx ~/.local/bin/yt-dlp  # Make executable

COPY target/WatchlistHelper-jar-with-dependencies.jar /opt/app
CMD ["java", "-jar", "/opt/app/WatchlistHelper-jar-with-dependencies.jar", "--docker"]