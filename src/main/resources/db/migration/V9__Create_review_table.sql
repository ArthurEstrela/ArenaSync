CREATE TABLE review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rating INT NOT NULL,
    comment VARCHAR(255),
    user_id BIGINT NOT NULL,
    match_id BIGINT,
    tournament_id BIGINT,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT fk_review_match FOREIGN KEY (match_id) REFERENCES game(id),
    CONSTRAINT fk_review_tournament FOREIGN KEY (tournament_id) REFERENCES tournament(id),
    -- Garante que um usuário só pode avaliar uma partida específica uma vez
    CONSTRAINT uk_user_match UNIQUE (user_id, match_id),
    -- Garante que um usuário só pode avaliar um torneio específico uma vez
    CONSTRAINT uk_user_tournament UNIQUE (user_id, tournament_id)
);
