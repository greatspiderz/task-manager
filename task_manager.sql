# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: 127.0.0.1 (MySQL 5.6.23)
# Database: task_manager
# Generation Time: 2015-11-09 03:21:15 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table task
# ------------------------------------------------------------

DROP TABLE IF EXISTS `task`;

CREATE TABLE `task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `actor_id` bigint(20) DEFAULT NULL,
  `actor_type` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `eta` bigint(20) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `status` enum('NEW','COMPLETED','CANCELLED','IN_PROGRESS') DEFAULT NULL,
  `subject_id` bigint(20) DEFAULT NULL,
  `subject_type` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `task_group_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_lyb4goemrhk0lboedxxwa1q9t` (`task_group_id`),
  CONSTRAINT `FK_lyb4goemrhk0lboedxxwa1q9t` FOREIGN KEY (`task_group_id`) REFERENCES `task_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `task` WRITE;
/*!40000 ALTER TABLE `task` DISABLE KEYS */;

INSERT INTO `task` (`id`, `created_at`, `updated_at`, `version`, `actor_id`, `actor_type`, `description`, `end_time`, `eta`, `start_time`, `status`, `subject_id`, `subject_type`, `type`, `task_group_id`)
VALUES
	(193,'2015-11-08 12:04:01','2015-11-08 12:04:01',0,NULL,NULL,NULL,'2015-10-09 00:00:00',NULL,'2015-10-09 00:00:00','NEW',NULL,NULL,'PICK',193);

/*!40000 ALTER TABLE `task` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table task_attributes
# ------------------------------------------------------------

DROP TABLE IF EXISTS `task_attributes`;

CREATE TABLE `task_attributes` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20) NOT NULL,
  `attribute_name` varchar(255) DEFAULT NULL,
  `attribute_value` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `task_attributes` WRITE;
/*!40000 ALTER TABLE `task_attributes` DISABLE KEYS */;

INSERT INTO `task_attributes` (`id`, `task_id`, `attribute_name`, `attribute_value`, `created_at`, `updated_at`, `version`)
VALUES
	(180,193,'test_attribute','test_value','2015-11-08 12:04:01','2015-11-08 12:04:01',0);

/*!40000 ALTER TABLE `task_attributes` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table task_group
# ------------------------------------------------------------

DROP TABLE IF EXISTS `task_group`;

CREATE TABLE `task_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `task_group` WRITE;
/*!40000 ALTER TABLE `task_group` DISABLE KEYS */;

INSERT INTO `task_group` (`id`, `created_at`, `updated_at`, `version`)
VALUES
	(193,'2015-11-08 12:04:01','2015-11-08 12:04:01',0);

/*!40000 ALTER TABLE `task_group` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
