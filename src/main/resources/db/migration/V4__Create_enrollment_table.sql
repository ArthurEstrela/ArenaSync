CREATE TABLE enrollment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(255) NOT NULL, -- Para o ENUM Status (PENDING, APPROVED, REJECTED)
    team_id BIGINT NOT NULL,
    tournament_id BIGINT NOT NULL,
    CONSTRAINT fk_enrollment_team FOREIGN KEY (team_id) REFERENCES team(id),
    CONSTRAINT fk_enrollment_tournament FOREIGN KEY (tournament_id) REFERENCES tournament(id),
    CONSTRAINT uk_team_tournament UNIQUE (team_id, tournament_id) -- Garante que um time só pode se inscrever uma vez por torneio
);