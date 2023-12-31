-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: cinema
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `film`
--

DROP TABLE IF EXISTS `film`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `film` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `length` int NOT NULL,
  `premiere` varchar(45) NOT NULL,
  `info_about` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `info_about_idx` (`info_about`),
  CONSTRAINT `info_about` FOREIGN KEY (`info_about`) REFERENCES `info_film` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `film`
--

LOCK TABLES `film` WRITE;
/*!40000 ALTER TABLE `film` DISABLE KEYS */;
INSERT INTO `film` VALUES (1,'Богемская рапсодия',134,'23.10.2018',1),(2,'Кролик Джоджо',104,'08.09.2019',2),(3,'Тайна Коко',105,'20.10.2017',3),(4,'Твоё имя',110,'03.07.2016',4),(5,'Интерстеллар',169,'26.10.2014',5),(6,'Одержимость',106,'16.01.2014',6);
/*!40000 ALTER TABLE `film` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `info_film`
--

DROP TABLE IF EXISTS `info_film`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `info_film` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `genre` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `info_film`
--

LOCK TABLES `info_film` WRITE;
/*!40000 ALTER TABLE `info_film` DISABLE KEYS */;
INSERT INTO `info_film` VALUES (1,'Брайан','Сингер','Биографический'),(2,'Тайка','Вайтити','Комедия'),(3,'Ли','Анкрич','Мультфильм'),(4,'Макото','Синкай','Аниме'),(5,'Кристофер','Нолан','Фантастика'),(6,'Кристофер','Нолан','Драма');
/*!40000 ALTER TABLE `info_film` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sessions`
--

DROP TABLE IF EXISTS `sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sessions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `numb_month` int NOT NULL,
  `date` int NOT NULL,
  `time` varchar(45) NOT NULL,
  `film` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `film_idx` (`film`),
  CONSTRAINT `film` FOREIGN KEY (`film`) REFERENCES `film` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sessions`
--

LOCK TABLES `sessions` WRITE;
/*!40000 ALTER TABLE `sessions` DISABLE KEYS */;
INSERT INTO `sessions` VALUES (1,5,1,'19:00',1),(2,5,1,'15:40',4),(3,5,5,'03:50',3),(4,5,5,'15:55',5),(5,5,10,'02:05',5),(6,5,10,'22:35',1),(7,5,15,'11:15',2),(8,5,15,'18:25',4),(9,5,20,'17:40',6),(10,5,20,'12:00',3),(11,5,20,'12:00',1),(12,5,25,'22:35',4),(13,5,25,'19:55',2),(14,5,30,'14:10',1),(15,5,30,'19:55',5);
/*!40000 ALTER TABLE `sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticketsonsession`
--

DROP TABLE IF EXISTS `ticketsonsession`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticketsonsession` (
  `id` int NOT NULL AUTO_INCREMENT,
  `row_numb` int NOT NULL,
  `seat` int NOT NULL,
  `bought` int NOT NULL,
  `id_session` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `id_session_idx` (`id_session`),
  CONSTRAINT `id_session` FOREIGN KEY (`id_session`) REFERENCES `sessions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1261 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticketsonsession`
--

LOCK TABLES `ticketsonsession` WRITE;
/*!40000 ALTER TABLE `ticketsonsession` DISABLE KEYS */;
INSERT INTO `ticketsonsession` VALUES (1,1,1,0,1),(2,1,2,0,1),(3,1,3,0,1),(4,1,4,0,1),(5,1,5,0,1),(6,1,6,0,1),(7,1,7,0,1),(8,1,8,0,1),(9,1,9,0,1),(10,1,10,0,1),(11,1,11,0,1),(12,1,12,0,1),(13,1,13,0,1),(14,1,14,0,1),(15,2,1,0,1),(16,2,2,0,1),(17,2,3,0,1),(18,2,4,0,1),(19,2,5,0,1),(20,2,6,0,1),(21,2,7,0,1),(22,2,8,0,1),(23,2,9,0,1),(24,2,10,0,1),(25,2,11,0,1),(26,2,12,0,1),(27,2,13,0,1),(28,2,14,0,1),(29,3,1,0,1),(30,3,2,0,1),(31,3,3,0,1),(32,3,4,0,1),(33,3,5,0,1),(34,3,6,0,1),(35,3,7,0,1),(36,3,8,0,1),(37,3,9,0,1),(38,3,10,0,1),(39,3,11,0,1),(40,3,12,0,1),(41,3,13,0,1),(42,3,14,0,1),(43,4,1,0,1),(44,4,2,0,1),(45,4,3,0,1),(46,4,4,0,1),(47,4,5,0,1),(48,4,6,0,1),(49,4,7,0,1),(50,4,8,0,1),(51,4,9,0,1),(52,4,10,0,1),(53,4,11,0,1),(54,4,12,0,1),(55,4,13,0,1),(56,4,14,0,1),(57,5,1,0,1),(58,5,2,0,1),(59,5,3,0,1),(60,5,4,0,1),(61,5,5,0,1),(62,5,6,0,1),(63,5,7,0,1),(64,5,8,0,1),(65,5,9,0,1),(66,5,10,0,1),(67,5,11,0,1),(68,5,12,0,1),(69,5,13,0,1),(70,5,14,0,1),(71,6,1,0,1),(72,6,2,0,1),(73,6,3,0,1),(74,6,4,0,1),(75,6,5,0,1),(76,6,6,0,1),(77,6,7,0,1),(78,6,8,0,1),(79,6,9,0,1),(80,6,10,0,1),(81,6,11,0,1),(82,6,12,0,1),(83,6,13,0,1),(84,6,14,0,1),(85,1,1,0,2),(86,1,2,0,2),(87,1,3,0,2),(88,1,4,0,2),(89,1,5,0,2),(90,1,6,0,2),(91,1,7,0,2),(92,1,8,0,2),(93,1,9,0,2),(94,1,10,0,2),(95,1,11,0,2),(96,1,12,0,2),(97,1,13,0,2),(98,1,14,0,2),(99,2,1,0,2),(100,2,2,0,2),(101,2,3,0,2),(102,2,4,0,2),(103,2,5,0,2),(104,2,6,0,2),(105,2,7,0,2),(106,2,8,0,2),(107,2,9,0,2),(108,2,10,0,2),(109,2,11,0,2),(110,2,12,0,2),(111,2,13,0,2),(112,2,14,0,2),(113,3,1,0,2),(114,3,2,0,2),(115,3,3,0,2),(116,3,4,0,2),(117,3,5,0,2),(118,3,6,0,2),(119,3,7,0,2),(120,3,8,0,2),(121,3,9,0,2),(122,3,10,0,2),(123,3,11,0,2),(124,3,12,0,2),(125,3,13,0,2),(126,3,14,0,2),(127,4,1,0,2),(128,4,2,0,2),(129,4,3,0,2),(130,4,4,0,2),(131,4,5,0,2),(132,4,6,0,2),(133,4,7,0,2),(134,4,8,0,2),(135,4,9,0,2),(136,4,10,0,2),(137,4,11,0,2),(138,4,12,0,2),(139,4,13,0,2),(140,4,14,0,2),(141,5,1,0,2),(142,5,2,0,2),(143,5,3,0,2),(144,5,4,0,2),(145,5,5,0,2),(146,5,6,0,2),(147,5,7,0,2),(148,5,8,0,2),(149,5,9,0,2),(150,5,10,0,2),(151,5,11,0,2),(152,5,12,0,2),(153,5,13,0,2),(154,5,14,0,2),(155,6,1,0,2),(156,6,2,0,2),(157,6,3,0,2),(158,6,4,0,2),(159,6,5,0,2),(160,6,6,0,2),(161,6,7,0,2),(162,6,8,0,2),(163,6,9,0,2),(164,6,10,0,2),(165,6,11,0,2),(166,6,12,0,2),(167,6,13,0,2),(168,6,14,0,2),(169,1,1,0,3),(170,1,2,0,3),(171,1,3,0,3),(172,1,4,0,3),(173,1,5,0,3),(174,1,6,0,3),(175,1,7,0,3),(176,1,8,0,3),(177,1,9,0,3),(178,1,10,0,3),(179,1,11,0,3),(180,1,12,0,3),(181,1,13,0,3),(182,1,14,0,3),(183,2,1,0,3),(184,2,2,0,3),(185,2,3,0,3),(186,2,4,0,3),(187,2,5,0,3),(188,2,6,0,3),(189,2,7,0,3),(190,2,8,0,3),(191,2,9,0,3),(192,2,10,0,3),(193,2,11,0,3),(194,2,12,0,3),(195,2,13,0,3),(196,2,14,0,3),(197,3,1,0,3),(198,3,2,0,3),(199,3,3,0,3),(200,3,4,0,3),(201,3,5,0,3),(202,3,6,0,3),(203,3,7,0,3),(204,3,8,0,3),(205,3,9,0,3),(206,3,10,0,3),(207,3,11,0,3),(208,3,12,0,3),(209,3,13,0,3),(210,3,14,0,3),(211,4,1,0,3),(212,4,2,0,3),(213,4,3,0,3),(214,4,4,0,3),(215,4,5,0,3),(216,4,6,0,3),(217,4,7,0,3),(218,4,8,0,3),(219,4,9,0,3),(220,4,10,0,3),(221,4,11,0,3),(222,4,12,0,3),(223,4,13,0,3),(224,4,14,0,3),(225,5,1,0,3),(226,5,2,0,3),(227,5,3,0,3),(228,5,4,0,3),(229,5,5,0,3),(230,5,6,0,3),(231,5,7,0,3),(232,5,8,0,3),(233,5,9,0,3),(234,5,10,0,3),(235,5,11,0,3),(236,5,12,0,3),(237,5,13,0,3),(238,5,14,0,3),(239,6,1,0,3),(240,6,2,0,3),(241,6,3,0,3),(242,6,4,0,3),(243,6,5,0,3),(244,6,6,0,3),(245,6,7,0,3),(246,6,8,0,3),(247,6,9,0,3),(248,6,10,0,3),(249,6,11,0,3),(250,6,12,0,3),(251,6,13,0,3),(252,6,14,0,3),(253,1,1,0,4),(254,1,2,0,4),(255,1,3,0,4),(256,1,4,0,4),(257,1,5,0,4),(258,1,6,0,4),(259,1,7,0,4),(260,1,8,0,4),(261,1,9,0,4),(262,1,10,0,4),(263,1,11,0,4),(264,1,12,0,4),(265,1,13,0,4),(266,1,14,0,4),(267,2,1,0,4),(268,2,2,0,4),(269,2,3,0,4),(270,2,4,0,4),(271,2,5,0,4),(272,2,6,0,4),(273,2,7,0,4),(274,2,8,0,4),(275,2,9,0,4),(276,2,10,0,4),(277,2,11,0,4),(278,2,12,0,4),(279,2,13,0,4),(280,2,14,0,4),(281,3,1,0,4),(282,3,2,0,4),(283,3,3,0,4),(284,3,4,0,4),(285,3,5,0,4),(286,3,6,0,4),(287,3,7,0,4),(288,3,8,0,4),(289,3,9,0,4),(290,3,10,0,4),(291,3,11,0,4),(292,3,12,0,4),(293,3,13,0,4),(294,3,14,0,4),(295,4,1,0,4),(296,4,2,0,4),(297,4,3,0,4),(298,4,4,0,4),(299,4,5,0,4),(300,4,6,0,4),(301,4,7,0,4),(302,4,8,0,4),(303,4,9,0,4),(304,4,10,0,4),(305,4,11,0,4),(306,4,12,0,4),(307,4,13,0,4),(308,4,14,0,4),(309,5,1,0,4),(310,5,2,0,4),(311,5,3,0,4),(312,5,4,0,4),(313,5,5,0,4),(314,5,6,0,4),(315,5,7,0,4),(316,5,8,0,4),(317,5,9,0,4),(318,5,10,0,4),(319,5,11,0,4),(320,5,12,0,4),(321,5,13,0,4),(322,5,14,0,4),(323,6,1,0,4),(324,6,2,0,4),(325,6,3,0,4),(326,6,4,0,4),(327,6,5,0,4),(328,6,6,0,4),(329,6,7,0,4),(330,6,8,0,4),(331,6,9,0,4),(332,6,10,0,4),(333,6,11,0,4),(334,6,12,0,4),(335,6,13,0,4),(336,6,14,0,4),(337,1,1,0,5),(338,1,2,0,5),(339,1,3,0,5),(340,1,4,0,5),(341,1,5,0,5),(342,1,6,0,5),(343,1,7,0,5),(344,1,8,0,5),(345,1,9,0,5),(346,1,10,0,5),(347,1,11,0,5),(348,1,12,0,5),(349,1,13,0,5),(350,1,14,0,5),(351,2,1,0,5),(352,2,2,0,5),(353,2,3,0,5),(354,2,4,0,5),(355,2,5,0,5),(356,2,6,0,5),(357,2,7,0,5),(358,2,8,0,5),(359,2,9,0,5),(360,2,10,0,5),(361,2,11,0,5),(362,2,12,0,5),(363,2,13,0,5),(364,2,14,0,5),(365,3,1,0,5),(366,3,2,0,5),(367,3,3,0,5),(368,3,4,0,5),(369,3,5,0,5),(370,3,6,0,5),(371,3,7,0,5),(372,3,8,0,5),(373,3,9,0,5),(374,3,10,0,5),(375,3,11,0,5),(376,3,12,0,5),(377,3,13,0,5),(378,3,14,0,5),(379,4,1,0,5),(380,4,2,0,5),(381,4,3,0,5),(382,4,4,0,5),(383,4,5,0,5),(384,4,6,0,5),(385,4,7,0,5),(386,4,8,0,5),(387,4,9,0,5),(388,4,10,0,5),(389,4,11,0,5),(390,4,12,0,5),(391,4,13,0,5),(392,4,14,0,5),(393,5,1,0,5),(394,5,2,0,5),(395,5,3,0,5),(396,5,4,0,5),(397,5,5,0,5),(398,5,6,0,5),(399,5,7,0,5),(400,5,8,0,5),(401,5,9,0,5),(402,5,10,0,5),(403,5,11,0,5),(404,5,12,0,5),(405,5,13,0,5),(406,5,14,0,5),(407,6,1,0,5),(408,6,2,0,5),(409,6,3,0,5),(410,6,4,0,5),(411,6,5,0,5),(412,6,6,0,5),(413,6,7,0,5),(414,6,8,0,5),(415,6,9,0,5),(416,6,10,0,5),(417,6,11,0,5),(418,6,12,0,5),(419,6,13,0,5),(420,6,14,0,5),(421,1,1,0,6),(422,1,2,0,6),(423,1,3,0,6),(424,1,4,0,6),(425,1,5,0,6),(426,1,6,0,6),(427,1,7,0,6),(428,1,8,0,6),(429,1,9,0,6),(430,1,10,0,6),(431,1,11,0,6),(432,1,12,0,6),(433,1,13,0,6),(434,1,14,0,6),(435,2,1,0,6),(436,2,2,0,6),(437,2,3,0,6),(438,2,4,0,6),(439,2,5,0,6),(440,2,6,0,6),(441,2,7,0,6),(442,2,8,0,6),(443,2,9,0,6),(444,2,10,0,6),(445,2,11,0,6),(446,2,12,0,6),(447,2,13,0,6),(448,2,14,0,6),(449,3,1,0,6),(450,3,2,0,6),(451,3,3,0,6),(452,3,4,0,6),(453,3,5,0,6),(454,3,6,0,6),(455,3,7,0,6),(456,3,8,0,6),(457,3,9,0,6),(458,3,10,0,6),(459,3,11,0,6),(460,3,12,0,6),(461,3,13,0,6),(462,3,14,0,6),(463,4,1,0,6),(464,4,2,0,6),(465,4,3,0,6),(466,4,4,0,6),(467,4,5,0,6),(468,4,6,0,6),(469,4,7,0,6),(470,4,8,0,6),(471,4,9,0,6),(472,4,10,0,6),(473,4,11,0,6),(474,4,12,0,6),(475,4,13,0,6),(476,4,14,0,6),(477,5,1,0,6),(478,5,2,0,6),(479,5,3,0,6),(480,5,4,0,6),(481,5,5,0,6),(482,5,6,0,6),(483,5,7,0,6),(484,5,8,0,6),(485,5,9,0,6),(486,5,10,0,6),(487,5,11,0,6),(488,5,12,0,6),(489,5,13,0,6),(490,5,14,0,6),(491,6,1,0,6),(492,6,2,0,6),(493,6,3,0,6),(494,6,4,0,6),(495,6,5,0,6),(496,6,6,0,6),(497,6,7,0,6),(498,6,8,0,6),(499,6,9,0,6),(500,6,10,0,6),(501,6,11,0,6),(502,6,12,0,6),(503,6,13,0,6),(504,6,14,0,6),(505,1,1,0,7),(506,1,2,0,7),(507,1,3,0,7),(508,1,4,0,7),(509,1,5,0,7),(510,1,6,0,7),(511,1,7,0,7),(512,1,8,0,7),(513,1,9,0,7),(514,1,10,0,7),(515,1,11,0,7),(516,1,12,0,7),(517,1,13,0,7),(518,1,14,0,7),(519,2,1,0,7),(520,2,2,0,7),(521,2,3,0,7),(522,2,4,0,7),(523,2,5,0,7),(524,2,6,0,7),(525,2,7,0,7),(526,2,8,0,7),(527,2,9,0,7),(528,2,10,0,7),(529,2,11,0,7),(530,2,12,0,7),(531,2,13,0,7),(532,2,14,0,7),(533,3,1,0,7),(534,3,2,0,7),(535,3,3,0,7),(536,3,4,0,7),(537,3,5,0,7),(538,3,6,0,7),(539,3,7,0,7),(540,3,8,0,7),(541,3,9,0,7),(542,3,10,0,7),(543,3,11,0,7),(544,3,12,0,7),(545,3,13,0,7),(546,3,14,0,7),(547,4,1,0,7),(548,4,2,0,7),(549,4,3,0,7),(550,4,4,0,7),(551,4,5,0,7),(552,4,6,0,7),(553,4,7,0,7),(554,4,8,0,7),(555,4,9,0,7),(556,4,10,0,7),(557,4,11,0,7),(558,4,12,0,7),(559,4,13,0,7),(560,4,14,0,7),(561,5,1,0,7),(562,5,2,0,7),(563,5,3,0,7),(564,5,4,0,7),(565,5,5,0,7),(566,5,6,0,7),(567,5,7,0,7),(568,5,8,0,7),(569,5,9,0,7),(570,5,10,0,7),(571,5,11,0,7),(572,5,12,0,7),(573,5,13,0,7),(574,5,14,0,7),(575,6,1,0,7),(576,6,2,0,7),(577,6,3,0,7),(578,6,4,0,7),(579,6,5,0,7),(580,6,6,0,7),(581,6,7,0,7),(582,6,8,0,7),(583,6,9,0,7),(584,6,10,0,7),(585,6,11,0,7),(586,6,12,0,7),(587,6,13,0,7),(588,6,14,0,7),(589,1,1,0,8),(590,1,2,0,8),(591,1,3,0,8),(592,1,4,0,8),(593,1,5,0,8),(594,1,6,0,8),(595,1,7,0,8),(596,1,8,0,8),(597,1,9,0,8),(598,1,10,0,8),(599,1,11,0,8),(600,1,12,0,8),(601,1,13,0,8),(602,1,14,0,8),(603,2,1,0,8),(604,2,2,0,8),(605,2,3,0,8),(606,2,4,0,8),(607,2,5,0,8),(608,2,6,0,8),(609,2,7,0,8),(610,2,8,0,8),(611,2,9,0,8),(612,2,10,0,8),(613,2,11,0,8),(614,2,12,0,8),(615,2,13,0,8),(616,2,14,0,8),(617,3,1,0,8),(618,3,2,0,8),(619,3,3,0,8),(620,3,4,0,8),(621,3,5,0,8),(622,3,6,0,8),(623,3,7,0,8),(624,3,8,0,8),(625,3,9,0,8),(626,3,10,0,8),(627,3,11,0,8),(628,3,12,0,8),(629,3,13,0,8),(630,3,14,0,8),(631,4,1,0,8),(632,4,2,0,8),(633,4,3,0,8),(634,4,4,0,8),(635,4,5,0,8),(636,4,6,0,8),(637,4,7,0,8),(638,4,8,0,8),(639,4,9,0,8),(640,4,10,0,8),(641,4,11,0,8),(642,4,12,0,8),(643,4,13,0,8),(644,4,14,0,8),(645,5,1,0,8),(646,5,2,0,8),(647,5,3,0,8),(648,5,4,0,8),(649,5,5,0,8),(650,5,6,0,8),(651,5,7,0,8),(652,5,8,0,8),(653,5,9,0,8),(654,5,10,0,8),(655,5,11,0,8),(656,5,12,0,8),(657,5,13,0,8),(658,5,14,0,8),(659,6,1,0,8),(660,6,2,0,8),(661,6,3,0,8),(662,6,4,0,8),(663,6,5,0,8),(664,6,6,0,8),(665,6,7,0,8),(666,6,8,0,8),(667,6,9,0,8),(668,6,10,0,8),(669,6,11,0,8),(670,6,12,0,8),(671,6,13,0,8),(672,6,14,0,8),(673,1,1,0,9),(674,1,2,0,9),(675,1,3,0,9),(676,1,4,0,9),(677,1,5,0,9),(678,1,6,0,9),(679,1,7,0,9),(680,1,8,0,9),(681,1,9,0,9),(682,1,10,0,9),(683,1,11,0,9),(684,1,12,0,9),(685,1,13,0,9),(686,1,14,0,9),(687,2,1,0,9),(688,2,2,0,9),(689,2,3,0,9),(690,2,4,0,9),(691,2,5,0,9),(692,2,6,0,9),(693,2,7,0,9),(694,2,8,0,9),(695,2,9,0,9),(696,2,10,0,9),(697,2,11,0,9),(698,2,12,0,9),(699,2,13,0,9),(700,2,14,0,9),(701,3,1,0,9),(702,3,2,0,9),(703,3,3,0,9),(704,3,4,0,9),(705,3,5,0,9),(706,3,6,0,9),(707,3,7,0,9),(708,3,8,0,9),(709,3,9,0,9),(710,3,10,0,9),(711,3,11,0,9),(712,3,12,0,9),(713,3,13,0,9),(714,3,14,0,9),(715,4,1,0,9),(716,4,2,0,9),(717,4,3,0,9),(718,4,4,0,9),(719,4,5,0,9),(720,4,6,0,9),(721,4,7,0,9),(722,4,8,0,9),(723,4,9,0,9),(724,4,10,0,9),(725,4,11,0,9),(726,4,12,0,9),(727,4,13,0,9),(728,4,14,0,9),(729,5,1,0,9),(730,5,2,0,9),(731,5,3,0,9),(732,5,4,0,9),(733,5,5,0,9),(734,5,6,0,9),(735,5,7,0,9),(736,5,8,0,9),(737,5,9,0,9),(738,5,10,0,9),(739,5,11,0,9),(740,5,12,0,9),(741,5,13,0,9),(742,5,14,0,9),(743,6,1,0,9),(744,6,2,0,9),(745,6,3,0,9),(746,6,4,0,9),(747,6,5,0,9),(748,6,6,0,9),(749,6,7,0,9),(750,6,8,0,9),(751,6,9,0,9),(752,6,10,0,9),(753,6,11,0,9),(754,6,12,0,9),(755,6,13,0,9),(756,6,14,0,9),(757,1,1,0,10),(758,1,2,0,10),(759,1,3,0,10),(760,1,4,0,10),(761,1,5,0,10),(762,1,6,0,10),(763,1,7,0,10),(764,1,8,0,10),(765,1,9,0,10),(766,1,10,0,10),(767,1,11,0,10),(768,1,12,0,10),(769,1,13,0,10),(770,1,14,0,10),(771,2,1,0,10),(772,2,2,0,10),(773,2,3,0,10),(774,2,4,0,10),(775,2,5,0,10),(776,2,6,0,10),(777,2,7,0,10),(778,2,8,0,10),(779,2,9,0,10),(780,2,10,0,10),(781,2,11,0,10),(782,2,12,0,10),(783,2,13,0,10),(784,2,14,0,10),(785,3,1,0,10),(786,3,2,0,10),(787,3,3,0,10),(788,3,4,0,10),(789,3,5,0,10),(790,3,6,0,10),(791,3,7,0,10),(792,3,8,0,10),(793,3,9,0,10),(794,3,10,0,10),(795,3,11,0,10),(796,3,12,0,10),(797,3,13,0,10),(798,3,14,0,10),(799,4,1,0,10),(800,4,2,0,10),(801,4,3,0,10),(802,4,4,0,10),(803,4,5,0,10),(804,4,6,0,10),(805,4,7,0,10),(806,4,8,0,10),(807,4,9,0,10),(808,4,10,0,10),(809,4,11,0,10),(810,4,12,0,10),(811,4,13,0,10),(812,4,14,0,10),(813,5,1,0,10),(814,5,2,0,10),(815,5,3,0,10),(816,5,4,0,10),(817,5,5,0,10),(818,5,6,0,10),(819,5,7,0,10),(820,5,8,0,10),(821,5,9,0,10),(822,5,10,0,10),(823,5,11,0,10),(824,5,12,0,10),(825,5,13,0,10),(826,5,14,0,10),(827,6,1,0,10),(828,6,2,0,10),(829,6,3,0,10),(830,6,4,0,10),(831,6,5,0,10),(832,6,6,0,10),(833,6,7,0,10),(834,6,8,0,10),(835,6,9,0,10),(836,6,10,0,10),(837,6,11,0,10),(838,6,12,0,10),(839,6,13,0,10),(840,6,14,0,10),(841,1,1,0,11),(842,1,2,0,11),(843,1,3,0,11),(844,1,4,0,11),(845,1,5,0,11),(846,1,6,0,11),(847,1,7,0,11),(848,1,8,0,11),(849,1,9,0,11),(850,1,10,0,11),(851,1,11,0,11),(852,1,12,0,11),(853,1,13,0,11),(854,1,14,0,11),(855,2,1,0,11),(856,2,2,0,11),(857,2,3,0,11),(858,2,4,0,11),(859,2,5,0,11),(860,2,6,0,11),(861,2,7,0,11),(862,2,8,0,11),(863,2,9,0,11),(864,2,10,0,11),(865,2,11,0,11),(866,2,12,0,11),(867,2,13,0,11),(868,2,14,0,11),(869,3,1,0,11),(870,3,2,0,11),(871,3,3,0,11),(872,3,4,0,11),(873,3,5,0,11),(874,3,6,0,11),(875,3,7,0,11),(876,3,8,0,11),(877,3,9,0,11),(878,3,10,0,11),(879,3,11,0,11),(880,3,12,0,11),(881,3,13,0,11),(882,3,14,0,11),(883,4,1,0,11),(884,4,2,0,11),(885,4,3,0,11),(886,4,4,0,11),(887,4,5,0,11),(888,4,6,0,11),(889,4,7,0,11),(890,4,8,0,11),(891,4,9,0,11),(892,4,10,0,11),(893,4,11,0,11),(894,4,12,0,11),(895,4,13,0,11),(896,4,14,0,11),(897,5,1,0,11),(898,5,2,0,11),(899,5,3,0,11),(900,5,4,0,11),(901,5,5,0,11),(902,5,6,0,11),(903,5,7,0,11),(904,5,8,0,11),(905,5,9,0,11),(906,5,10,0,11),(907,5,11,0,11),(908,5,12,0,11),(909,5,13,0,11),(910,5,14,0,11),(911,6,1,0,11),(912,6,2,0,11),(913,6,3,0,11),(914,6,4,0,11),(915,6,5,0,11),(916,6,6,0,11),(917,6,7,0,11),(918,6,8,0,11),(919,6,9,0,11),(920,6,10,0,11),(921,6,11,0,11),(922,6,12,0,11),(923,6,13,0,11),(924,6,14,0,11),(925,1,1,0,12),(926,1,2,0,12),(927,1,3,0,12),(928,1,4,0,12),(929,1,5,0,12),(930,1,6,0,12),(931,1,7,0,12),(932,1,8,0,12),(933,1,9,0,12),(934,1,10,0,12),(935,1,11,0,12),(936,1,12,0,12),(937,1,13,0,12),(938,1,14,0,12),(939,2,1,0,12),(940,2,2,0,12),(941,2,3,0,12),(942,2,4,0,12),(943,2,5,0,12),(944,2,6,0,12),(945,2,7,0,12),(946,2,8,0,12),(947,2,9,0,12),(948,2,10,0,12),(949,2,11,0,12),(950,2,12,0,12),(951,2,13,0,12),(952,2,14,0,12),(953,3,1,0,12),(954,3,2,0,12),(955,3,3,0,12),(956,3,4,0,12),(957,3,5,0,12),(958,3,6,0,12),(959,3,7,0,12),(960,3,8,0,12),(961,3,9,0,12),(962,3,10,0,12),(963,3,11,0,12),(964,3,12,0,12),(965,3,13,0,12),(966,3,14,0,12),(967,4,1,0,12),(968,4,2,0,12),(969,4,3,0,12),(970,4,4,0,12),(971,4,5,0,12),(972,4,6,0,12),(973,4,7,0,12),(974,4,8,0,12),(975,4,9,0,12),(976,4,10,0,12),(977,4,11,0,12),(978,4,12,0,12),(979,4,13,0,12),(980,4,14,0,12),(981,5,1,0,12),(982,5,2,0,12),(983,5,3,0,12),(984,5,4,0,12),(985,5,5,0,12),(986,5,6,0,12),(987,5,7,0,12),(988,5,8,0,12),(989,5,9,0,12),(990,5,10,0,12),(991,5,11,0,12),(992,5,12,0,12),(993,5,13,0,12),(994,5,14,0,12),(995,6,1,0,12),(996,6,2,0,12),(997,6,3,0,12),(998,6,4,0,12),(999,6,5,0,12),(1000,6,6,0,12),(1001,6,7,0,12),(1002,6,8,0,12),(1003,6,9,0,12),(1004,6,10,0,12),(1005,6,11,0,12),(1006,6,12,0,12),(1007,6,13,0,12),(1008,6,14,0,12),(1009,1,1,0,13),(1010,1,2,0,13),(1011,1,3,0,13),(1012,1,4,0,13),(1013,1,5,0,13),(1014,1,6,0,13),(1015,1,7,0,13),(1016,1,8,0,13),(1017,1,9,0,13),(1018,1,10,0,13),(1019,1,11,0,13),(1020,1,12,0,13),(1021,1,13,0,13),(1022,1,14,0,13),(1023,2,1,0,13),(1024,2,2,0,13),(1025,2,3,0,13),(1026,2,4,0,13),(1027,2,5,0,13),(1028,2,6,0,13),(1029,2,7,0,13),(1030,2,8,0,13),(1031,2,9,0,13),(1032,2,10,0,13),(1033,2,11,0,13),(1034,2,12,0,13),(1035,2,13,0,13),(1036,2,14,0,13),(1037,3,1,0,13),(1038,3,2,0,13),(1039,3,3,0,13),(1040,3,4,0,13),(1041,3,5,0,13),(1042,3,6,0,13),(1043,3,7,0,13),(1044,3,8,0,13),(1045,3,9,0,13),(1046,3,10,0,13),(1047,3,11,0,13),(1048,3,12,0,13),(1049,3,13,0,13),(1050,3,14,0,13),(1051,4,1,0,13),(1052,4,2,0,13),(1053,4,3,0,13),(1054,4,4,0,13),(1055,4,5,0,13),(1056,4,6,0,13),(1057,4,7,0,13),(1058,4,8,0,13),(1059,4,9,0,13),(1060,4,10,0,13),(1061,4,11,0,13),(1062,4,12,0,13),(1063,4,13,0,13),(1064,4,14,0,13),(1065,5,1,0,13),(1066,5,2,0,13),(1067,5,3,0,13),(1068,5,4,0,13),(1069,5,5,0,13),(1070,5,6,0,13),(1071,5,7,0,13),(1072,5,8,0,13),(1073,5,9,0,13),(1074,5,10,0,13),(1075,5,11,0,13),(1076,5,12,0,13),(1077,5,13,0,13),(1078,5,14,0,13),(1079,6,1,0,13),(1080,6,2,0,13),(1081,6,3,0,13),(1082,6,4,0,13),(1083,6,5,0,13),(1084,6,6,0,13),(1085,6,7,0,13),(1086,6,8,0,13),(1087,6,9,0,13),(1088,6,10,0,13),(1089,6,11,0,13),(1090,6,12,0,13),(1091,6,13,0,13),(1092,6,14,0,13),(1093,1,1,0,14),(1094,1,2,0,14),(1095,1,3,0,14),(1096,1,4,0,14),(1097,1,5,0,14),(1098,1,6,0,14),(1099,1,7,0,14),(1100,1,8,0,14),(1101,1,9,0,14),(1102,1,10,0,14),(1103,1,11,0,14),(1104,1,12,0,14),(1105,1,13,0,14),(1106,1,14,0,14),(1107,2,1,0,14),(1108,2,2,0,14),(1109,2,3,0,14),(1110,2,4,0,14),(1111,2,5,0,14),(1112,2,6,0,14),(1113,2,7,0,14),(1114,2,8,0,14),(1115,2,9,0,14),(1116,2,10,0,14),(1117,2,11,0,14),(1118,2,12,0,14),(1119,2,13,0,14),(1120,2,14,0,14),(1121,3,1,0,14),(1122,3,2,0,14),(1123,3,3,0,14),(1124,3,4,0,14),(1125,3,5,0,14),(1126,3,6,0,14),(1127,3,7,0,14),(1128,3,8,0,14),(1129,3,9,0,14),(1130,3,10,0,14),(1131,3,11,0,14),(1132,3,12,0,14),(1133,3,13,0,14),(1134,3,14,0,14),(1135,4,1,0,14),(1136,4,2,0,14),(1137,4,3,0,14),(1138,4,4,0,14),(1139,4,5,0,14),(1140,4,6,0,14),(1141,4,7,0,14),(1142,4,8,0,14),(1143,4,9,0,14),(1144,4,10,0,14),(1145,4,11,0,14),(1146,4,12,0,14),(1147,4,13,0,14),(1148,4,14,0,14),(1149,5,1,0,14),(1150,5,2,0,14),(1151,5,3,0,14),(1152,5,4,0,14),(1153,5,5,0,14),(1154,5,6,0,14),(1155,5,7,0,14),(1156,5,8,0,14),(1157,5,9,0,14),(1158,5,10,0,14),(1159,5,11,0,14),(1160,5,12,0,14),(1161,5,13,0,14),(1162,5,14,0,14),(1163,6,1,0,14),(1164,6,2,0,14),(1165,6,3,0,14),(1166,6,4,0,14),(1167,6,5,0,14),(1168,6,6,0,14),(1169,6,7,0,14),(1170,6,8,0,14),(1171,6,9,0,14),(1172,6,10,0,14),(1173,6,11,0,14),(1174,6,12,0,14),(1175,6,13,0,14),(1176,6,14,0,14),(1177,1,1,0,15),(1178,1,2,0,15),(1179,1,3,0,15),(1180,1,4,0,15),(1181,1,5,0,15),(1182,1,6,0,15),(1183,1,7,0,15),(1184,1,8,0,15),(1185,1,9,0,15),(1186,1,10,0,15),(1187,1,11,0,15),(1188,1,12,0,15),(1189,1,13,0,15),(1190,1,14,0,15),(1191,2,1,0,15),(1192,2,2,0,15),(1193,2,3,0,15),(1194,2,4,0,15),(1195,2,5,0,15),(1196,2,6,0,15),(1197,2,7,0,15),(1198,2,8,0,15),(1199,2,9,0,15),(1200,2,10,0,15),(1201,2,11,0,15),(1202,2,12,0,15),(1203,2,13,0,15),(1204,2,14,0,15),(1205,3,1,0,15),(1206,3,2,0,15),(1207,3,3,0,15),(1208,3,4,0,15),(1209,3,5,0,15),(1210,3,6,0,15),(1211,3,7,0,15),(1212,3,8,0,15),(1213,3,9,0,15),(1214,3,10,0,15),(1215,3,11,0,15),(1216,3,12,0,15),(1217,3,13,0,15),(1218,3,14,0,15),(1219,4,1,0,15),(1220,4,2,0,15),(1221,4,3,0,15),(1222,4,4,0,15),(1223,4,5,0,15),(1224,4,6,0,15),(1225,4,7,0,15),(1226,4,8,0,15),(1227,4,9,0,15),(1228,4,10,0,15),(1229,4,11,0,15),(1230,4,12,0,15),(1231,4,13,0,15),(1232,4,14,0,15),(1233,5,1,0,15),(1234,5,2,0,15),(1235,5,3,0,15),(1236,5,4,0,15),(1237,5,5,0,15),(1238,5,6,0,15),(1239,5,7,0,15),(1240,5,8,0,15),(1241,5,9,0,15),(1242,5,10,0,15),(1243,5,11,0,15),(1244,5,12,0,15),(1245,5,13,0,15),(1246,5,14,0,15),(1247,6,1,0,15),(1248,6,2,0,15),(1249,6,3,0,15),(1250,6,4,0,15),(1251,6,5,0,15),(1252,6,6,0,15),(1253,6,7,0,15),(1254,6,8,0,15),(1255,6,9,0,15),(1256,6,10,0,15),(1257,6,11,0,15),(1258,6,12,0,15),(1259,6,13,0,15),(1260,6,14,0,15);
/*!40000 ALTER TABLE `ticketsonsession` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-09-12 10:42:23
