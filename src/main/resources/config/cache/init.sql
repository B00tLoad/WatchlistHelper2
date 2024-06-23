CREATE TABLE IF NOT EXISTS `discordstates`
(
    `websession`   varchar(120),
    `state`        varchar(255),
    `target_url`   varchar(255),
    `requested_at` timestamp DEFAULT (now())
);