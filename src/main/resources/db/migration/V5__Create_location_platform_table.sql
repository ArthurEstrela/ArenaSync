CREATE TABLE location_platform (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255), -- Para o ENUM TournamentType (SPORT, ESPORT)
    description TEXT
);