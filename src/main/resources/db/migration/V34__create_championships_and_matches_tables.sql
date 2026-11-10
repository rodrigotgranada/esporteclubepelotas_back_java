-- ==============================================================================
-- Migration V34: Criação das tabelas de Campeonatos e Partidas (Módulo 9)
-- ==============================================================================

-- 1. Tabela de Campeonatos
CREATE TABLE IF NOT EXISTS championships (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(160) NOT NULL,
    season_year INT NOT NULL,
    category VARCHAR(40) NOT NULL DEFAULT 'PROFESSIONAL_MEN',
    logo_url VARCHAR(1000),
    table_url VARCHAR(1000),
    regulations JSONB NOT NULL DEFAULT '[]'::jsonb,
    is_active BOOLEAN NOT NULL DEFAULT true,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_championships_active_season 
ON championships (is_active, season_year, display_order) 
WHERE deleted_at IS NULL;

-- 2. Tabela de Partidas (Foco no Pelotas)
CREATE TABLE IF NOT EXISTS matches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    championship_id UUID NOT NULL REFERENCES championships(id) ON DELETE CASCADE,
    opponent_name VARCHAR(160) NOT NULL,
    opponent_logo_url VARCHAR(1000),
    home_away VARCHAR(10) NOT NULL DEFAULT 'HOME',
    stadium_name VARCHAR(160) NOT NULL,
    city VARCHAR(120),
    match_date TIMESTAMP WITH TIME ZONE NOT NULL,
    round_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    pelotas_score INT,
    opponent_score INT,
    penalty_pelotas_score INT,
    penalty_opponent_score INT,
    ticket_url VARCHAR(1000),
    broadcast_channel VARCHAR(200),
    transmission_url VARCHAR(1000),
    notes TEXT,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_matches_championship_date 
ON matches (championship_id, match_date ASC) 
WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_matches_status_date 
ON matches (status, match_date ASC) 
WHERE deleted_at IS NULL;

-- 3. Registro do Módulo no Sistema
INSERT INTO system_modules (id, name, slug, description, is_active, created_at, updated_at)
VALUES (
    'm-matches-001',
    'Campeonatos & Jogos',
    'matches',
    'Gestão de campeonatos oficiais, calendário de jogos, próximo confronto e links de transmissão/tabela',
    true,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    slug = EXCLUDED.slug,
    description = EXCLUDED.description,
    updated_at = NOW();

-- 4. Seed Realista Inicial: Gauchão Série A 2026 com Próximo Jogo e Partidas
DO $$
DECLARE
    v_champ_id UUID := '00000000-0000-4000-a000-000000000001'::UUID;
BEGIN
    -- Inserir Campeonato Ativo
    INSERT INTO championships (
        id, name, season_year, category, logo_url, table_url, regulations, is_active, display_order, created_at, updated_at
    ) VALUES (
        v_champ_id,
        'Gauchão Série A',
        2026,
        'PROFESSIONAL_MEN',
        '/images/pelotas-logo.png',
        'https://fgf.com.br/competicoes/gauchao-serie-a',
        '[
            {"title": "Regulamento Específico da Competição (REC)", "url": "https://fgf.com.br/arquivos/regulamentos/rec-gauchao-2026.pdf"},
            {"title": "Regulamento Geral das Competições FGF (RGC)", "url": "https://fgf.com.br/arquivos/regulamentos/rgc-fgf-2026.pdf"}
        ]'::jsonb,
        true,
        0,
        NOW(),
        NOW()
    ) ON CONFLICT (id) DO NOTHING;

    -- Jogo Passado 1 (Vitória na estreia)
    INSERT INTO matches (
        id, championship_id, opponent_name, opponent_logo_url, home_away, stadium_name, city, match_date, round_name, status, pelotas_score, opponent_score, broadcast_channel, created_at, updated_at
    ) VALUES (
        '10000000-0000-4000-a000-000000000001'::UUID,
        v_champ_id,
        'São Luiz',
        '/images/pelotas-logo.png',
        'AWAY',
        'Estádio 19 de Outubro',
        'Ijuí - RS',
        NOW() - INTERVAL '7 days',
        '1ª Rodada',
        'FINISHED',
        2,
        1,
        'FGF TV / Rádio Pelotense',
        NOW(),
        NOW()
    ) ON CONFLICT (id) DO NOTHING;

    -- Jogo Passado 2 (Empate na Boca do Lobo)
    INSERT INTO matches (
        id, championship_id, opponent_name, opponent_logo_url, home_away, stadium_name, city, match_date, round_name, status, pelotas_score, opponent_score, broadcast_channel, created_at, updated_at
    ) VALUES (
        '10000000-0000-4000-a000-000000000002'::UUID,
        v_champ_id,
        'Novo Hamburgo',
        '/images/pelotas-logo.png',
        'HOME',
        'Estádio Boca do Lobo',
        'Pelotas - RS',
        NOW() - INTERVAL '3 days',
        '2ª Rodada',
        'FINISHED',
        1,
        1,
        'FGF TV / Rádio Pelotense',
        NOW(),
        NOW()
    ) ON CONFLICT (id) DO NOTHING;

    -- Próximo Jogo (Em Destaque na Home)
    INSERT INTO matches (
        id, championship_id, opponent_name, opponent_logo_url, home_away, stadium_name, city, match_date, round_name, status, ticket_url, broadcast_channel, transmission_url, created_at, updated_at
    ) VALUES (
        '10000000-0000-4000-a000-000000000003'::UUID,
        v_champ_id,
        'Caxias',
        '/images/pelotas-logo.png',
        'HOME',
        'Estádio Boca do Lobo',
        'Pelotas - RS',
        NOW() + INTERVAL '3 days',
        '3ª Rodada',
        'SCHEDULED',
        'https://eleventickets.com',
        'FGF TV / Rádio Pelotense',
        'https://www.youtube.com/@ecpelotasoficial',
        NOW(),
        NOW()
    ) ON CONFLICT (id) DO NOTHING;

    -- Jogo Futuro 4
    INSERT INTO matches (
        id, championship_id, opponent_name, opponent_logo_url, home_away, stadium_name, city, match_date, round_name, status, broadcast_channel, created_at, updated_at
    ) VALUES (
        '10000000-0000-4000-a000-000000000004'::UUID,
        v_champ_id,
        'Juventude',
        '/images/pelotas-logo.png',
        'AWAY',
        'Estádio Alfredo Jaconi',
        'Caxias do Sul - RS',
        NOW() + INTERVAL '7 days',
        '4ª Rodada',
        'SCHEDULED',
        'FGF TV / Premiere',
        NOW(),
        NOW()
    ) ON CONFLICT (id) DO NOTHING;
END $$;

