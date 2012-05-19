-- phpMyAdmin SQL Dump
-- version 3.3.7deb6
-- http://www.phpmyadmin.net
--
-- Serveur: localhost
-- Généré le : Dim 22 Avril 2012 à 20:42
-- Version du serveur: 5.1.49
-- Version de PHP: 5.3.3-7+squeeze3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données: `ktlc`
--

-- --------------------------------------------------------

--
-- Structure de la table `KTLCEdition`
--

CREATE TABLE IF NOT EXISTS `KTLCEdition` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` datetime DEFAULT NULL,
  `number` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Structure de la table `KTLCRace`
--

CREATE TABLE IF NOT EXISTS `KTLCRace` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ktlc_id` bigint(20) DEFAULT NULL,
  `map_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK500ACB916AB6C27D` (`map_id`),
  KEY `FK500ACB91E2FFD894` (`ktlc_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Structure de la table `KTLCRaceResult`
--

CREATE TABLE IF NOT EXISTS `KTLCRaceResult` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rank` int(11) DEFAULT NULL,
  `roundsCount` int(11) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `login_id` bigint(20) DEFAULT NULL,
  `race_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKB9A0CF0E235B3C76` (`login_id`),
  KEY `FKB9A0CF0E215A53DE` (`race_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Structure de la table `KTLCResult`
--

CREATE TABLE IF NOT EXISTS `KTLCResult` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nbRaces` int(11) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `rankAvg` double DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `ktlc_id` bigint(20) DEFAULT NULL,
  `login_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK78C618DD235B3C76` (`login_id`),
  KEY `FK78C618DDE2FFD894` (`ktlc_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Structure de la table `Login`
--

CREATE TABLE IF NOT EXISTS `Login` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `player_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK462FF494592F63E` (`player_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


--
-- Structure de la table `Player`
--

CREATE TABLE IF NOT EXISTS `Player` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


--
-- Structure de la table `TMMap`
--

CREATE TABLE IF NOT EXISTS `TMMap` (
  `id` varchar(255) NOT NULL,
  `environment` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `login_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4C3E423235B3C76` (`login_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `KTLCRace`
--
ALTER TABLE `KTLCRace`
  ADD CONSTRAINT `FK500ACB916AB6C27D` FOREIGN KEY (`map_id`) REFERENCES `TMMap` (`id`),
  ADD CONSTRAINT `FK500ACB91E2FFD894` FOREIGN KEY (`ktlc_id`) REFERENCES `KTLCEdition` (`id`);

--
-- Contraintes pour la table `KTLCRaceResult`
--
ALTER TABLE `KTLCRaceResult`
  ADD CONSTRAINT `FKB9A0CF0E215A53DE` FOREIGN KEY (`race_id`) REFERENCES `KTLCRace` (`id`),
  ADD CONSTRAINT `FKB9A0CF0E235B3C76` FOREIGN KEY (`login_id`) REFERENCES `Login` (`id`);

--
-- Contraintes pour la table `KTLCResult`
--
ALTER TABLE `KTLCResult`
  ADD CONSTRAINT `FK78C618DD235B3C76` FOREIGN KEY (`login_id`) REFERENCES `Login` (`id`),
  ADD CONSTRAINT `FK78C618DDE2FFD894` FOREIGN KEY (`ktlc_id`) REFERENCES `KTLCEdition` (`id`);

--
-- Contraintes pour la table `Login`
--
ALTER TABLE `Login`
  ADD CONSTRAINT `FK462FF494592F63E` FOREIGN KEY (`player_id`) REFERENCES `Player` (`id`);

--
-- Contraintes pour la table `TMMap`
--
ALTER TABLE `TMMap`
  ADD CONSTRAINT `FK4C3E423235B3C76` FOREIGN KEY (`login_id`) REFERENCES `Login` (`id`);
