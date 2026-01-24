DROP DATABASE IF EXISTS code_duel;
CREATE DATABASE IF NOT EXISTS code_duel;
USE code_duel;

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE,
    email VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(50),
    score INT
);

DROP TABLE IF EXISTS challenge;
CREATE TABLE IF NOT EXISTS challenge (
    challenge_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    description TEXT,
    difficulty VARCHAR(50),
    sample TEXT
);

DROP TABLE IF EXISTS `match`;
CREATE TABLE IF NOT EXISTS `match` (
    match_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    challenge_id BIGINT,
    difficulty VARCHAR(255),
    programming_language VARCHAR(50),
    status ENUM('PENDING', 'RUNNING', 'FINISHED') NOT NULL,
    winner_id BIGINT,
    FOREIGN KEY (challenge_id) REFERENCES challenge(challenge_id)
);

DROP TABLE IF EXISTS test_case;
CREATE TABLE IF NOT EXISTS test_case (
    test_case_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    challenge_id BIGINT,
    `input` VARCHAR(255),
    expected_output VARCHAR(255),
    FOREIGN KEY (challenge_id) REFERENCES challenge(challenge_id)
);

DROP TABLE IF EXISTS user_play_match;
CREATE TABLE IF NOT EXISTS user_play_match (
    user_id BIGINT,
    match_id BIGINT,
    username VARCHAR(255),
    user_score INT,
    PRIMARY KEY (user_id, match_id),
    FOREIGN KEY (user_id) REFERENCES `user`(user_id),
    FOREIGN KEY (match_id) REFERENCES `match`(match_id)
);

DROP TABLE IF EXISTS submission;
CREATE TABLE IF NOT EXISTS submission (
    submission_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    challenge_id BIGINT,
    submitter_id BIGINT,
    result VARCHAR(100),
    code TEXT,
    programming_language VARCHAR(50),
    FOREIGN KEY (challenge_id) REFERENCES challenge(challenge_id),
    FOREIGN KEY (submitter_id) REFERENCES `user`(user_id)
);

