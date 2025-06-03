CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    age INT NOT NULL,
    password VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    -- Se você tiver outros campos como organization_name, phone_number para Organizer
    -- ou position para Player, adicione-os aqui ou em migrações separadas
    -- para as tabelas específicas de Organizer e Player, se elas herdarem de User
    -- e tiverem suas próprias tabelas (dependendo da sua estratégia de herança JPA).
    -- Para uma estratégia TABLE_PER_CLASS (comum quando se estende User),
    -- cada entidade concreta (User, Player, Organizer) teria sua própria tabela.

    -- Exemplo para o Organizer que estende User (se for @Inheritance(strategy = InheritanceType.JOINED) ou TABLE_PER_CLASS)
    -- Se Player e Organizer são tipos de User e você quer uma única tabela User com um discriminador:
    -- user_type VARCHAR(31) NOT NULL, -- Coluna discriminadora para herança SINGLE_TABLE
    -- organization_name VARCHAR(255),
    -- phone_number VARCHAR(255),
    -- bio TEXT,
    -- social_links VARCHAR(255),
    -- position VARCHAR(255)
);

-- Se Player e Organizer são tabelas separadas que referenciam User ou têm seus próprios campos:
-- (Considerando a estrutura de suas entidades Player e Organizer que estendem User)
-- Se você usar @Inheritance(strategy = InheritanceType.JOINED), você teria:

CREATE TABLE organizer (
    id BIGINT PRIMARY KEY, -- Mesmo ID da tabela User
    organization_name VARCHAR(255),
    phone_number VARCHAR(255),
    bio TEXT,
    social_links VARCHAR(255),
    CONSTRAINT fk_organizer_user FOREIGN KEY (id) REFERENCES user(id)
);

CREATE TABLE player (
    id BIGINT PRIMARY KEY, -- Mesmo ID da tabela User
    position VARCHAR(255),
    team_id BIGINT, -- Supondo que team_id venha depois na criação da tabela team
    CONSTRAINT fk_player_user FOREIGN KEY (id) REFERENCES user(id)
    -- CONSTRAINT fk_player_team FOREIGN KEY (team_id) REFERENCES team(id) -- Adicionar após criar a tabela team
);

-- NOTA: A criação de todas as tabelas (Team, Tournament, Match, etc.) deve seguir
-- em migrações subsequentes ou nesta mesma, respeitando as dependências de chaves estrangeiras.