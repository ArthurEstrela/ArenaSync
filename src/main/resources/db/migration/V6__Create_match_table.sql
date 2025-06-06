CREATE TABLE game (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scheduled_date_time DATETIME NOT NULL,
    score_team_a INT,
    score_team_b INT,
    tournament_id BIGINT NOT NULL,
    location_platform_id BIGINT NOT NULL,
    team_a_id BIGINT NOT NULL,
    team_b_id BIGINT NOT NULL,
    CONSTRAINT fk_game_tournament FOREIGN KEY (tournament_id) REFERENCES tournament(id),
    CONSTRAINT fk_game_location_platform FOREIGN KEY (location_platform_id) REFERENCES location_platform(id),
    CONSTRAINT fk_game_team_a FOREIGN KEY (team_a_id) REFERENCES team(id),
    CONSTRAINT fk_game_team_b FOREIGN KEY (team_b_id) REFERENCES team(id)
);