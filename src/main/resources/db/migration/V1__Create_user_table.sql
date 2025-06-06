CREATE TABLE
    user (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        age INT NOT NULL,
        password VARCHAR(255),
        email VARCHAR(255) UNIQUE
    );

CREATE TABLE
    organizer (
        id BIGINT PRIMARY KEY,
        organization_name VARCHAR(255),
        phone_number VARCHAR(255),
        bio TEXT,
        social_links VARCHAR(255),
        CONSTRAINT fk_organizer_user FOREIGN KEY (id) REFERENCES user (id)
    );

CREATE TABLE
    player (
        id BIGINT PRIMARY KEY,
        position VARCHAR(255),
        team_id BIGINT,
        CONSTRAINT fk_player_user FOREIGN KEY (id) REFERENCES user (id)
    );