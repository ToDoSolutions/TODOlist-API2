SET FOREIGN_KEY_CHECKS = 0;

-- En la nube
-- USE `b8iyr7xai8wk75ismpbt`;

-- En local
USE `todolist-api2`;

CREATE TABLE IF NOT EXISTS `group`
(
    `id_group`     bigint(20)   NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `created_date` varchar(255) NOT NULL,
    `description`  varchar(500) DEFAULT NULL,
    `name`         varchar(50)  NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `group_user`
(
    `id_group_user` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `id_group`      bigint(20),
    `id_user`       bigint(20),
    FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
    FOREIGN KEY (`id_group`) REFERENCES `group` (`id_group`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `task`
(
    `id_task`       bigint(20)   NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `annotation`    varchar(255) DEFAULT NULL,
    `description`   varchar(255) DEFAULT NULL,
    `difficulty`    varchar(255) DEFAULT NULL,
    `finished_date` varchar(255) NOT NULL,
    `priority`      int(11)      DEFAULT NULL,
    `start_date`    varchar(255) NOT NULL,
    `status`        varchar(255) DEFAULT 'DRAFT',
    `title`         varchar(255) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `user`
(
    `id_user`  bigint(20)   NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `avatar`   varchar(255) DEFAULT NULL,
    `bio`      varchar(500) DEFAULT NULL,
    `email`    varchar(255) NOT NULL,
    `location` varchar(50)  DEFAULT NULL,
    `name`     varchar(50)  NOT NULL,
    `surname`  varchar(50)  NOT NULL,
    `username` varchar(50)  NOT NULL,
    `password` varchar(255) NOT NULL,
    `token`    varchar(255) DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `user_task`
(
    `id_user_task` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `id_user`      bigint(20),
    `id_task`      bigint(20),
    FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
    FOREIGN KEY (`id_task`) REFERENCES `task` (`id_task`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

SET FOREIGN_KEY_CHECKS = 1;
