CREATE DATABASE IF NOT EXISTS airflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE airflow;

CREATE TABLE IF NOT EXISTS `users` (
  `id_PK` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `name` varchar(40) NOT NULL,
  `last_name` varchar(40) NOT NULL,
  `email` varchar(200) NOT NULL UNIQUE,
  `password` varchar(73) NOT NULL,
  `isSuperUser` boolean NOT NULL,
  `created_at` timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS `airplanes` (
  `id_PK` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `airline` varchar(20) NOT NULL,
  `model` varchar(50) NOT NULL,
  `code` varchar(10) NOT NULL,
  `capacity` int NOT NULL,
  `year` year
);

CREATE TABLE IF NOT EXISTS `cities` (
  `id_PK` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `country` varchar(100) NOT NULL,
  `code` varchar(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS `flight_status` (
  `id_PK` int PRIMARY KEY NOT NULL,
  `name` varchar(15) NOT NULL,
  `description` varchar(100)
);

CREATE TABLE IF NOT EXISTS `reservations_status` (
  `id_PK` int PRIMARY KEY NOT NULL,
  `name` varchar(15) NOT NULL,
  `description` varchar(100)
);

CREATE TABLE IF NOT EXISTS `flights` (
  `id_PK` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `airplane_FK` int NOT NULL,
  `status_FK` int NOT NULL,
  `origin_city_FK` int NOT NULL,
  `destination_city_FK` int NOT NULL,
  `code` VARCHAR(10) NOT NULL,
  `departure_time` timestamp NOT NULL,
  `arrival_time` timestamp NOT NULL,
  `price_base` DECIMAL(10,2) NOT NULL,
  FOREIGN KEY (`airplane_FK`) REFERENCES `airplanes` (`id_PK`),
  FOREIGN KEY (`status_FK`) REFERENCES `flight_status` (`id_PK`),
  FOREIGN KEY (`origin_city_FK`) REFERENCES `cities` (`id_PK`),
  FOREIGN KEY (`destination_city_FK`) REFERENCES `cities` (`id_PK`)
);

CREATE TABLE IF NOT EXISTS `reservations` (
  `id_PK` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `user_FK` int NOT NULL,
  `status_FK` int NOT NULL,
  `flight_FK` int NOT NULL,
  `reserved_at` timestamp NOT NULL,
  FOREIGN KEY (`user_FK`) REFERENCES `users` (`id_PK`),
  FOREIGN KEY (`status_FK`) REFERENCES `reservations_status` (`id_PK`),
  FOREIGN KEY (`flight_FK`) REFERENCES `flights` (`id_PK`)
);

CREATE TABLE IF NOT EXISTS `seats` (
  `id_PK` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `airplane_FK` int NOT NULL,
  `reservation_FK` int,
  `seat_number` VARCHAR(10) NOT NULL,
  `seat_class` ENUM('ECONOMY','BUSINESS','FIRST') NOT NULL,
  `is_window` BOOLEAN,
  FOREIGN KEY (`airplane_FK`) REFERENCES `airplanes` (`id_PK`),
  FOREIGN KEY (`reservation_FK`) REFERENCES `reservations` (`id_PK`)
);

