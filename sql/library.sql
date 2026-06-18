-- ============================================================
--  Library Management System — Database Schema
--  Engine  : MySQL / MariaDB
--  Charset : utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS `library`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE `library`;

-- ------------------------------------------------------------
--  Table: user
--  Stores login credentials for library system users.
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `username` VARCHAR(50)  NOT NULL,
    `password` VARCHAR(100) NOT NULL,          -- store hashed passwords in production
    PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Sample users (password column stores the value as plain text for demo only)
INSERT INTO `user` (`username`, `password`) VALUES
    ('admin',  '123456'),
    ('staff',  '123456'),
    ('member', '123456');

-- ------------------------------------------------------------
--  Table: book
--  Stores the library book catalogue.
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
    `id`       INT AUTO_INCREMENT PRIMARY KEY,
    `bookname` VARCHAR(150) NOT NULL,
    `price`    DOUBLE       NOT NULL DEFAULT 0.00,
    `quantity` INT          NOT NULL DEFAULT 0,
    `owner`    VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Sample catalogue data
INSERT INTO `book` (`bookname`, `price`, `quantity`, `owner`) VALUES
    ('Rich Dad Poor Dad',        60.00, 5, 'Publisher A'),
    ('Atomic Habits',            30.00, 9, 'Publisher B'),
    ('The Great Expectations',  120.00, 3, 'Publisher C'),
    ('Clean Code',               50.00, 4, 'Publisher D'),
    ('The Pragmatic Programmer', 45.00, 6, 'Publisher E'),
    ('Deep Work',                35.00, 8, 'Publisher F');
