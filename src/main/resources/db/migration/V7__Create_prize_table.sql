CREATE TABLE prize (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    value DOUBLE,
    tournament_id BIGINT,
    CONSTRAINT fk_prize_tournament FOREIGN KEY (tournament_id) REFERENCES tournament(id)
);