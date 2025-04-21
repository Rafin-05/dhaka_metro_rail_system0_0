-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 21, 2025 at 10:25 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `metro`
--

-- --------------------------------------------------------

--
-- Table structure for table `fares`
--

CREATE TABLE `fares` (
  `source_id` int(11) NOT NULL,
  `dest_id` int(11) NOT NULL,
  `fare` decimal(5,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `fares`
--

INSERT INTO `fares` (`source_id`, `dest_id`, `fare`) VALUES
(1, 2, 30.00),
(1, 3, 50.00),
(1, 4, 70.00),
(1, 5, 90.00),
(1, 6, 100.00),
(2, 3, 20.00),
(2, 4, 40.00),
(2, 5, 60.00),
(2, 6, 80.00),
(3, 4, 20.00),
(3, 5, 40.00),
(3, 6, 60.00),
(4, 5, 20.00),
(4, 6, 40.00),
(5, 6, 20.00);

-- --------------------------------------------------------

--
-- Table structure for table `mrt_rapid_pass_users`
--

CREATE TABLE `mrt_rapid_pass_users` (
  `card_number` varchar(20) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `nid` varchar(20) DEFAULT NULL,
  `card_type` varchar(10) DEFAULT NULL,
  `balance` decimal(8,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mrt_rapid_pass_users`
--

INSERT INTO `mrt_rapid_pass_users` (`card_number`, `name`, `nid`, `card_type`, `balance`) VALUES
('MRT1001', 'Abdul Karim', '1234567890', 'MRT', 500.00),
('MRT1002', 'Bijoy Ahmed', '4845678901', 'MRT', 160.00),
('MRT1745042503886', 'Mehedi Hasan', '3092105467', 'MRT', 2560.00),
('MRT1745169990731', 'Tamim', 'Ahmed', 'MRT', 250.00),
('RAP1744988884239', 'Sabbir Khan', '6706439086', 'RAPID', 1150.00),
('RAPID2001', 'Fatema Begum', '2345678901', 'RAPID', 300.00);

-- --------------------------------------------------------

--
-- Table structure for table `one_time_pass`
--

CREATE TABLE `one_time_pass` (
  `ticket_number` varchar(30) NOT NULL,
  `source_id` int(11) DEFAULT NULL,
  `dest_id` int(11) DEFAULT NULL,
  `fare` decimal(5,2) DEFAULT NULL,
  `journey_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `one_time_pass`
--

INSERT INTO `one_time_pass` (`ticket_number`, `source_id`, `dest_id`, `fare`, `journey_time`) VALUES
('OT1744982074203', 6, 1, 100.00, '2025-04-18 19:14:34'),
('OT1744982166954', 3, 6, 60.00, '2025-04-18 19:16:06'),
('OT1744987208272', 2, 3, 20.00, '2025-04-18 20:40:08'),
('OT1744988857959', 1, 4, 70.00, '2025-04-18 21:07:37'),
('OT1744989482374', 1, 6, 100.00, '2025-04-18 21:18:02'),
('OT1745042647704', 2, 5, 60.00, '2025-04-19 12:04:07'),
('OT1745214497056', 1, 6, 100.00, '2025-04-21 11:48:17'),
('OT1745221299248', 1, 5, 90.00, '2025-04-21 13:41:39');

-- --------------------------------------------------------

--
-- Table structure for table `stations`
--

CREATE TABLE `stations` (
  `station_id` int(11) NOT NULL,
  `station_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `stations`
--

INSERT INTO `stations` (`station_id`, `station_name`) VALUES
(1, 'Uttara North'),
(2, 'Pallabi'),
(3, 'Mirpur-10'),
(4, 'Agargaon'),
(5, 'Farmgate'),
(6, 'Motijheel');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `fares`
--
ALTER TABLE `fares`
  ADD PRIMARY KEY (`source_id`,`dest_id`),
  ADD KEY `dest_id` (`dest_id`);

--
-- Indexes for table `mrt_rapid_pass_users`
--
ALTER TABLE `mrt_rapid_pass_users`
  ADD PRIMARY KEY (`card_number`);

--
-- Indexes for table `one_time_pass`
--
ALTER TABLE `one_time_pass`
  ADD PRIMARY KEY (`ticket_number`),
  ADD KEY `source_id` (`source_id`),
  ADD KEY `dest_id` (`dest_id`);

--
-- Indexes for table `stations`
--
ALTER TABLE `stations`
  ADD PRIMARY KEY (`station_id`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `fares`
--
ALTER TABLE `fares`
  ADD CONSTRAINT `fares_ibfk_1` FOREIGN KEY (`source_id`) REFERENCES `stations` (`station_id`),
  ADD CONSTRAINT `fares_ibfk_2` FOREIGN KEY (`dest_id`) REFERENCES `stations` (`station_id`);

--
-- Constraints for table `one_time_pass`
--
ALTER TABLE `one_time_pass`
  ADD CONSTRAINT `one_time_pass_ibfk_1` FOREIGN KEY (`source_id`) REFERENCES `stations` (`station_id`),
  ADD CONSTRAINT `one_time_pass_ibfk_2` FOREIGN KEY (`dest_id`) REFERENCES `stations` (`station_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
