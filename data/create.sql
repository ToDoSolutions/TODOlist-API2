-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         10.6.7-MariaDB - mariadb.org binary distribution
-- SO del servidor:              Win64
-- HeidiSQL Versión:             11.3.0.6295
-- --------------------------------------------------------

SET FOREIGN_KEY_CHECKS=0;

-- Volcando estructura de base de datos para todolist-api2
CREATE DATABASE IF NOT EXISTS `todolist` ;
USE `todolist`;
DROP TABLE IF EXISTS `user_task`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS  `task`;
DROP TABLE IF EXISTS  `group_user`;
DROP TABLE IF EXISTS  `user`;

-- Volcando estructura para tabla todolist-api2.group
CREATE TABLE IF NOT EXISTS `group` (
    `id_group` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `created_date` varchar(255) DEFAULT NULL,
    `description` varchar(500) DEFAULT NULL,
    `name` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Volcando estructura para tabla todolist-api2.group_user
CREATE TABLE IF NOT EXISTS `group_user`(
    `id_group_user` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `id_group` bigint(20),
    `id_user` bigint(20),
    FOREIGN KEY (`id_user`) REFERENCES `user`(`id_user`) ON DELETE CASCADE,
    FOREIGN KEY (`id_group`) REFERENCES `group`(`id_group`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Volcando estructura para tabla todolist-api2.task
CREATE TABLE IF NOT EXISTS `task`(
    `id_task` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `annotation` varchar(255) DEFAULT NULL,
    `description` varchar(255) DEFAULT NULL,
    `difficulty` varchar(255) DEFAULT NULL,
    `finished_date` varchar(255) DEFAULT NULL,
    `priority` int(11) DEFAULT NULL,
    `start_date` varchar(255) DEFAULT NULL,
    `status` varchar(255) DEFAULT NULL,
    `title` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Volcando estructura para tabla todolist-api2.user
CREATE TABLE IF NOT EXISTS `user`(
    `id_user` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `avatar` varchar(255) DEFAULT NULL,
    `bio` varchar(500) DEFAULT NULL,
    `email` varchar(255) DEFAULT NULL,
    `location` varchar(50) DEFAULT NULL,
    `name` varchar(50) DEFAULT NULL,
    `surname` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Volcando estructura para tabla todolist-api2.user_task
CREATE TABLE IF NOT EXISTS `user_task` (
    `id_user_task` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `id_user` bigint(20),
    `id_task` bigint(20),
    FOREIGN KEY (`id_user`) REFERENCES `user`(`id_user`) ON DELETE CASCADE,
    FOREIGN KEY (`id_task`) REFERENCES `task`(`id_task`) ON DELETE CASCADE	
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS=1; -- to disable them
