-- Migration V35: Criação da tabela de Clubes Adversários (opponent_clubs)
-- Módulo dedicado para cadastro de clubes, distintivos, cidades e estádios sede

CREATE TABLE IF NOT EXISTS opponent_clubs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    short_name VARCHAR(60),
    badge_url TEXT,
    city VARCHAR(100),
    stadium_name VARCHAR(150),
    is_active BOOLEAN NOT NULL DEFAULT true,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_opponent_clubs_active 
ON opponent_clubs (is_active) 
WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_opponent_clubs_name 
ON opponent_clubs (name) 
WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_opponent_clubs_order 
ON opponent_clubs (display_order ASC, name ASC) 
WHERE deleted_at IS NULL;

-- Registro do Módulo de Clubes no Sistema caso ainda não exista
INSERT INTO system_modules (id, name, slug, description, is_active, created_at, updated_at)
VALUES (
    'm-clubs-001',
    'Clubes Adversários',
    'clubs',
    'Cadastro oficial de clubes adversários com distintivo, estádio oficial e cidade sede',
    true,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    slug = EXCLUDED.slug,
    description = EXCLUDED.description,
    updated_at = NOW();

-- Seed inicial de clubes gaúchos e tradicionais com estádios e cidades sede oficiais
INSERT INTO opponent_clubs (name, short_name, city, stadium_name, badge_url, display_order)
VALUES 
    ('Sociedade Esportiva e Recreativa Caxias do Sul', 'Caxias', 'Caxias do Sul - RS', 'Estádio Centenário', '/images/pelotas-logo.png', 1),
    ('Esporte Clube Juventude', 'Juventude', 'Caxias do Sul - RS', 'Estádio Alfredo Jaconi', '/images/pelotas-logo.png', 2),
    ('Esporte Clube Novo Hamburgo', 'Novo Hamburgo', 'Novo Hamburgo - RS', 'Estádio do Vale', '/images/pelotas-logo.png', 3),
    ('Esporte Clube São Luiz', 'São Luiz', 'Ijuí - RS', 'Estádio 19 de Outubro', '/images/pelotas-logo.png', 4),
    ('Grêmio Esportivo Brasil', 'Brasil de Pelotas', 'Pelotas - RS', 'Estádio Bento Freitas', '/images/pelotas-logo.png', 5),
    ('Ypiranga Futebol Clube', 'Ypiranga', 'Erechim - RS', 'Estádio Colosso da Lagoa', '/images/pelotas-logo.png', 6),
    ('Esporte Clube Avenida', 'Avenida', 'Santa Cruz do Sul - RS', 'Estádio dos Eucaliptos', '/images/pelotas-logo.png', 7),
    ('Guarany Futebol Clube', 'Guarany de Bagé', 'Bagé - RS', 'Estádio Estrela D''Alva', '/images/pelotas-logo.png', 8),
    ('Esporte Clube São José', 'São José', 'Porto Alegre - RS', 'Estádio Francisco Novelletto', '/images/pelotas-logo.png', 9),
    ('Clube Esportivo Aimoré', 'Aimoré', 'São Leopoldo - RS', 'Estádio Cristo Rei', '/images/pelotas-logo.png', 10),
    ('Monsoon Futebol Clube', 'Monsoon', 'Porto Alegre - RS', 'Parque Lami', '/images/pelotas-logo.png', 11),
    ('Grêmio Foot-Ball Porto Alegrense', 'Grêmio', 'Porto Alegre - RS', 'Arena do Grêmio', '/images/pelotas-logo.png', 12),
    ('Sport Club Internacional', 'Internacional', 'Porto Alegre - RS', 'Estádio Beira-Rio', '/images/pelotas-logo.png', 13),
    ('Esporte Clube Passo Fundo', 'Passo Fundo', 'Passo Fundo - RS', 'Estádio Vermelhão da Serra', '/images/pelotas-logo.png', 14),
    ('Grêmio Esportivo Bagé', 'Bagé', 'Bagé - RS', 'Estádio Pedra Moura', '/images/pelotas-logo.png', 15),
    ('Esporte Clube Internacional', 'Inter de Santa Maria', 'Santa Maria - RS', 'Estádio Presidente Vargas', '/images/pelotas-logo.png', 16)
ON CONFLICT (id) DO NOTHING;
