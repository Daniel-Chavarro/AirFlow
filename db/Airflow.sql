CREATE DATABASE IF NOT EXISTS airflow;

USE airflow;

CREATE TABLE IF NOT EXISTS `users` (
  `id_PK` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `name` varchar(40) NOT NULL,
  `last_name` varchar(40) NOT NULL,
  `email` varchar(200) NOT NULL,
  `password` varchar(20) NOT NULL,
  `isSuperUser` boolean NOT NULL,
  `created_at` datetime NOT NULL
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
  `departure_time` DATETIME NOT NULL,
  `arrival_time` DATETIME NOT NULL,
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
  `reserved_at` datetime NOT NULL,
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


-- USERS
INSERT INTO users (name, last_name, email, password, isSuperUser, created_at) VALUES
('Ana', 'Ramírez', 'ana.ramirez@example.com', 'clave123', FALSE, NOW()),
('Luis', 'Pérez', 'luis.perez@example.com', '1234abcd', TRUE, NOW());

-- AIRPLANES
INSERT INTO airplanes (airline, model, code, capacity, year) VALUES
('Avianca', 'Airbus A320', 'AV123', 180, 2018),
('Latam', 'Boeing 737', 'LA456', 160, 2015);

-- CITIES
INSERT INTO cities (name, country, code) VALUES
('Bogotá', 'Colombia', 'BOG'),
('Medellín', 'Colombia', 'MDE'),
('Cartagena', 'Colombia', 'CTG');

-- FLIGHT STATUS
INSERT INTO flight_status (id_PK, name, description) VALUES
(1, 'SCHEDULED', 'Programado'),
(2, 'CANCELLED', 'Cancelado'),
(3, 'COMPLETED', 'Completado');

-- RESERVATION STATUS
INSERT INTO reservations_status (id_PK, name, description) VALUES
(1, 'CONFIRMED', 'Reserva confirmada'),
(2, 'CANCELLED', 'Reserva cancelada');

-- FLIGHTS
INSERT INTO flights (airplane_FK, status_FK, origin_city_FK, destination_city_FK, code, departure_time, arrival_time, price_base) VALUES
(1, 1, 1, 2, 'AV202', '2024-06-10 08:00:00', '2024-06-10 09:00:00', 250000),
(2, 1, 2, 3, 'LA789', '2024-06-11 14:30:00', '2024-06-11 16:00:00', 200000);

-- SEATS
INSERT INTO seats (airplane_FK, reservation_FK, seat_number, seat_class, is_window) VALUES
(1, NULL, '1A', 'ECONOMY', TRUE),
(1, NULL, '1B', 'ECONOMY', FALSE),
(2, NULL, '2A', 'BUSINESS', TRUE),
(2, NULL, '2B', 'BUSINESS', FALSE);

-- RESERVATIONS
INSERT INTO reservations (user_FK, status_FK, flight_FK, reserved_at) VALUES
(1, 1, 1, NOW()),
(2, 1, 2, NOW());

-- Update seats to assign to reservations
UPDATE seats SET reservation_FK = 1 WHERE id_PK = 1;
UPDATE seats SET reservation_FK = 2 WHERE id_PK = 3;
