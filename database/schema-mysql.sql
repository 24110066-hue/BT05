-- Optional: Hibernate creates/updates these tables automatically in the mysql profile.
CREATE DATABASE IF NOT EXISTS category_user_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE category_user_db;
CREATE TABLE IF NOT EXISTS app_users (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 username VARCHAR(40) NOT NULL UNIQUE,
 email VARCHAR(120) NOT NULL UNIQUE,
 full_name VARCHAR(100) NOT NULL,
 password_hash VARCHAR(100) NOT NULL,
 role VARCHAR(10) NOT NULL,
 enabled BOOLEAN NOT NULL
);
CREATE TABLE IF NOT EXISTS categories (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(100) NOT NULL UNIQUE,
 description VARCHAR(1000),
 enabled BOOLEAN NOT NULL
);
-- No plaintext password inserts. DemoData creates BCrypt hashes on the first run.
