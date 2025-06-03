CREATE TABLE tournament (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL, -- Ou ENUM se o banco suportar bem e você preferir
    modality VARCHAR(255) NOT NULL,
    rules TEXT,
    start_date DATE,
    end_date DATE,
    status VARCHAR(255), -- PENDING, ONGOING, FINISHED
    organizer_id BIGINT,
    CONSTRAINT fk_tournament_organizer FOREIGN KEY (organizer_id) REFERENCES user(id) -- ou organizer(id) se for tabela separada
);