-- V11__Insert_sample_data.sql

-- Alimentando a tabela 'user'
INSERT INTO user (name, age, password, email) VALUES
('João Silva', 28, 'senha123', 'joao.silva@example.com'),
('Maria Souza', 35, 'segura456', 'maria.souza@example.com'),
('Carlos Pereira', 22, 'secreta789', 'carlos.p@example.com'),
('Ana Costa', 40, 'minhasenha', 'ana.c@example.com'),
('Pedro Almeida', 30, 'playerpass', 'pedro.a@example.com');

-- Alimentando a tabela 'organizer' (que herda de 'user')
-- IDs: 2 e 4 são organizadores
INSERT INTO organizer (id, organization_name, phone_number, bio, social_links) VALUES
(2, 'Eventos Top', '11987654321', 'Organização líder em eventos esportivos e de e-sports.', 'linkedin.com/eventostop'),
(4, 'Arena Masters', '21998765432', 'Especialistas em torneios de grande escala.', 'twitter.com/arenamasters');

-- Alimentando a tabela 'team'
INSERT INTO team (name, image_url, description) VALUES
('Os Incríveis', 'http://example.com/incriveis.png', 'Time focado em e-sports, com grandes talentos.'),
('Titans FC', 'http://example.com/titansfc.png', 'Clube de futebol amador com história de vitórias.'),
('Eagles Esports', 'http://example.com/eagles.png', 'Equipe profissional de e-sports de alto nível.'),
('Dragons Gaming', 'http://example.com/dragons.png', 'Comunidade e time de jogos diversos.');

-- Alimentando a tabela 'player' (que herda de 'user')
-- IDs: 1, 3 e 5 são jogadores
-- Associe os jogadores aos times usando os IDs gerados anteriormente
INSERT INTO player (id, position, team_id) VALUES
(1, 'Atacante', (SELECT id FROM team WHERE name = 'Titans FC')),
(3, 'Mid Laner', (SELECT id FROM team WHERE name = 'Eagles Esports')),
(5, 'Free Agent', NULL); -- Jogador sem time (agente livre)

-- Alimentando a tabela 'tournament'
-- ID do organizador 2 (Maria Souza)
INSERT INTO tournament (name, type, modality, rules, start_date, end_date, status, organizer_id) VALUES
('Campeonato de Inverno LOL', 'ESPORT', 'League of Legends', 'Regras oficiais Riot Games', '2025-07-01', '2025-07-15', 'PENDING', 2),
('Copa Verão Futebol', 'SPORT', 'Futebol', 'Regras da FIFA', '2025-08-01', '2025-08-10', 'ONGOING', 2),
('Torneio Xadrez Online', 'ESPORT', 'Xadrez', 'Regras FIDE Online', '2025-06-01', '2025-06-05', 'FINISHED', 4);

-- Alimentando a tabela 'location_platform'
INSERT INTO location_platform (name, type, description) VALUES
('Plataforma Online LoL', 'ESPORT', 'Plataforma dedicada a jogos de e-sports.'),
('Estádio Municipal', 'SPORT', 'Campo de futebol com infraestrutura completa.'),
('Chess.com Servidor', 'ESPORT', 'Servidor global para partidas de xadrez online.');

-- Alimentando a tabela 'enrollment'
INSERT INTO enrollment (status, team_id, tournament_id) VALUES
('APPROVED', (SELECT id FROM team WHERE name = 'Os Incríveis'), (SELECT id FROM tournament WHERE name = 'Campeonato de Inverno LOL')),
('PENDING', (SELECT id FROM team WHERE name = 'Titans FC'), (SELECT id FROM tournament WHERE name = 'Campeonato de Inverno LOL')),
('APPROVED', (SELECT id FROM team WHERE name = 'Titans FC'), (SELECT id FROM tournament WHERE name = 'Copa Verão Futebol')),
('APPROVED', (SELECT id FROM team WHERE name = 'Eagles Esports'), (SELECT id FROM tournament WHERE name = 'Campeonato de Inverno LOL'));


-- Alimentando a tabela 'game' (Partida)
INSERT INTO game (scheduled_date_time, score_team_a, score_team_b, tournament_id, location_platform_id, team_a_id, team_b_id) VALUES
('2025-07-05 18:00:00', NULL, NULL, (SELECT id FROM tournament WHERE name = 'Campeonato de Inverno LOL'), (SELECT id FROM location_platform WHERE name = 'Plataforma Online LoL'), (SELECT id FROM team WHERE name = 'Os Incríveis'), (SELECT id FROM team WHERE name = 'Eagles Esports')),
('2025-08-03 15:00:00', 2, 1, (SELECT id FROM tournament WHERE name = 'Copa Verão Futebol'), (SELECT id FROM location_platform WHERE name = 'Estádio Municipal'), (SELECT id FROM team WHERE name = 'Titans FC'), (SELECT id FROM team WHERE name = 'Dragons Gaming')),
('2025-06-02 10:00:00', 0, 1, (SELECT id FROM tournament WHERE name = 'Torneio Xadrez Online'), (SELECT id FROM location_platform WHERE name = 'Chess.com Servidor'), (SELECT id FROM team WHERE name = 'Os Incríveis'), (SELECT id FROM team WHERE name = 'Eagles Esports'));


-- Alimentando a tabela 'result' (Resultados de partidas)
INSERT INTO result (match_id, score_team_a, score_team_b) VALUES
((SELECT id FROM game WHERE scheduled_date_time = '2025-08-03 15:00:00'), 2, 1),
((SELECT id FROM game WHERE scheduled_date_time = '2025-06-02 10:00:00'), 0, 1);


-- Alimentando a tabela 'prize'
INSERT INTO prize (description, value, tournament_id) VALUES
('Troféu de Ouro LOL', 1000.00, (SELECT id FROM tournament WHERE name = 'Campeonato de Inverno LOL')),
('Medalha de Prata Futebol', 500.00, (SELECT id FROM tournament WHERE name = 'Copa Verão Futebol'));

-- Alimentando a tabela 'review'
INSERT INTO review (rating, comment, user_id, match_id, tournament_id) VALUES
(5, 'Excelente partida de LOL! Muito emocionante.', (SELECT id FROM user WHERE email = 'joao.silva@example.com'), (SELECT id FROM game WHERE scheduled_date_time = '2025-07-05 18:00:00'), NULL),
(4, 'Boa organização no torneio de futebol.', (SELECT id FROM user WHERE email = 'carlos.p@example.com'), NULL, (SELECT id FROM tournament WHERE name = 'Copa Verão Futebol'));

-- Alimentando a tabela 'statistic'
INSERT INTO statistic (player_id, games_played, wins, losses, score, assists, match_id) VALUES
((SELECT id FROM player WHERE id = 1), 15, 10, 5, 200, 30, (SELECT id FROM game WHERE scheduled_date_time = '2025-08-03 15:00:00')),
((SELECT id FROM player WHERE id = 3), 20, 12, 8, 350, 60, (SELECT id FROM game WHERE scheduled_date_time = '2025-07-05 18:00:00'));