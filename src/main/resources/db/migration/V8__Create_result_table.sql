CREATE TABLE
    result (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        score_team_a INT,
        score_team_b INT,
        match_id BIGINT UNIQUE NOT NULL, -- UNIQUE para relação OneToOne
        CONSTRAINT fk_result_match FOREIGN KEY (match_id) REFERENCES game (id)
    );