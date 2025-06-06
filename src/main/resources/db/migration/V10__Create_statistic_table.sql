CREATE TABLE
    statistic (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        score INT,
        assists INT,
        games_played INT,
        wins INT,
        losses INT,
        player_id BIGINT NOT NULL,
        match_id BIGINT, -- Pode ser nulo se for uma estatística geral do jogador e não específica da partida
        CONSTRAINT fk_statistic_player FOREIGN KEY (player_id) REFERENCES player (id),
        CONSTRAINT fk_statistic_match FOREIGN KEY (match_id) REFERENCES game (id)
    );