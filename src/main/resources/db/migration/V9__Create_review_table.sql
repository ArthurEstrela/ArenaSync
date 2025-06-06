CREATE TABLE review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rating INT NOT NULL,
    comment VARCHAR(255),
    user_id BIGINT NOT NULL,
    match_id BIGINT,
    tournament_id BIGINT,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT fk_review_match FOREIGN KEY (match_id) REFERENCES game(id),
    CONSTRAINT fk_review_tournament FOREIGN KEY (tournament_id) REFERENCES tournament(id)
    -- Considere adicionar uma restrição UNIQUE se um usuário só puder avaliar uma partida/torneio uma vez.
    -- Ex: CONSTRAINT uk_user_match UNIQUE (user_id, match_id) OU uk_user_tournament UNIQUE (user_id, tournament_id)
    -- No seu ReviewService, a validação `existsByUserIdAndMatchId` sugere que o par (user_id, match_id) deve ser único.
    -- Se for o caso, você pode adicionar: CONSTRAINT uk_user_match UNIQUE (user_id, match_id)
    -- E se houver review por torneio, pense na lógica de unicidade também.
);