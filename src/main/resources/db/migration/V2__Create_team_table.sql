CREATE TABLE team (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image_url VARCHAR(255),
    description TEXT
);

-- Agora você pode adicionar a FK em player se não o fez antes
ALTER TABLE player
ADD CONSTRAINT fk_player_team FOREIGN KEY (team_id) REFERENCES team(id);