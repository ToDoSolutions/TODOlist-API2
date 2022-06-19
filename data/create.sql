-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         10.6.7-MariaDB - mariadb.org binary distribution
-- SO del servidor:              Win64
-- HeidiSQL Versión:             11.3.0.6295
-- --------------------------------------------------------

-- Volcando estructura de base de datos para todolist-api2
CREATE DATABASE IF NOT EXISTS `todolist` ;
USE `todolist`;

-- Volcando estructura para tabla todolist-api2.group
CREATE TABLE IF NOT EXISTS `group` (
    `id_group` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_date` varchar(255) DEFAULT NULL,
    `description` varchar(500) DEFAULT NULL,
    `name` varchar(50) DEFAULT NULL,
    PRIMARY KEY (`id_group`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- Volcando estructura para tabla todolist-api2.group_user
CREATE TABLE IF NOT EXISTS `group_user`(
    `id_group` bigint(20) NOT NULL,
    `id_user` bigint(20) NOT NULL,
    KEY `FKknx9oe9a7m5gl5rxuy53eklfq`(`id_user`),
    KEY `FKgp71sm3cw5s1n4f31f56dalep`(`id_group`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- Volcando estructura para tabla todolist-api2.task
CREATE TABLE IF NOT EXISTS `task`(
    `id_task` bigint(20) NOT NULL AUTO_INCREMENT,
    `annotation` varchar(255) DEFAULT NULL,
    `description` varchar(255) DEFAULT NULL,
    `difficulty` varchar(255) DEFAULT NULL,
    `finished_date` varchar(255) DEFAULT NULL,
    `priority` int(11) DEFAULT NULL,
    `start_date` varchar(255) DEFAULT NULL,
    `status` varchar(255) DEFAULT NULL,
    `title` varchar(255) DEFAULT NULL,
    PRIMARY KEY(`id_task`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

-- Volcando estructura para tabla todolist-api2.user
CREATE TABLE IF NOT EXISTS `user`(
    `id_user` bigint(20) NOT NULL AUTO_INCREMENT,
    `avatar` varchar(255) DEFAULT NULL,
    `bio` varchar(500) DEFAULT NULL,
    `email` varchar(255) DEFAULT NULL,
    `location` varchar(50) DEFAULT NULL,
    `name` varchar(50) DEFAULT NULL,
    `surname` varchar(50) DEFAULT NULL,
    PRIMARY KEY(`id_user`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

-- Volcando estructura para tabla todolist-api2.user_task
CREATE TABLE IF NOT EXISTS `user_task` (
  `id_user` bigint(20) NOT NULL,
  `id_task` bigint(20) NOT NULL,
  KEY `FK3cp1voq6ityl160qxer45ifmt` (`id_task`),
  KEY `FK1x8e4dsmtgnk4b10s6i7lwrhg` (`id_user`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

