#
# Build
#
FROM maven:3.9.7-eclipse-temurin-21-alpine AS build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN mvn -f $HOME/pom.xml clean compile assembly:single


#
# Package
#
FROM eclipse-temurin:21-jre-alpine

LABEL authors="B00tLoad_"
ARG JAR_FILE=/usr/app/target/*.jar
RUN mkdir -p /opt/app
RUN mkdir -p /data/.bdu/watchlist
RUN apk add --no-cache python3 py3-pip curl
RUN mkdir -p ~/.local/bin/
RUN curl -L https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -o ~/.local/bin/yt-dlp
RUN chmod a+rx ~/.local/bin/yt-dlp  # Make executable

COPY --from=build $JAR_FILE /opt/app
CMD ["java", "-jar", "/opt/app/WatchlistHelper-jar-with-dependencies.jar", "--docker"]